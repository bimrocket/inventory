package cat.santfeliu.api.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.enumerator.GlobalLoaderConfigKeys;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.repo.ConnectorStatsRepo;
import cat.santfeliu.api.repo.GlobalIdRepo;

public abstract class ConnectorLoader extends ConnectorComponent {

	private static final Logger logLoader = LoggerFactory.getLogger(ConnectorLoader.class);
	
	@Autowired
	protected GlobalIdRepo globalIdRepo;

	@Autowired
	protected ConnectorStatsRepo connectorStatsRepo;

	protected boolean ends;

	protected boolean reset = true;

	protected long sleepTime;

	protected long loadTimeout;

	protected ConnectorInstance instance;

	protected JsonArray loaded = null;

	protected JsonArray all = null;

	protected final Gson gson = new Gson();
	
	protected int currentIndex = -1;

	protected int deletionIndex = -1;

	protected boolean checkedDeletions = false;

	protected List<String> allGUIDs = new ArrayList<String>();

	protected Page<ConnectorExecutionStatsDb> page = null;

	protected Map<String, String> guidsExisting = new HashMap<>();

	protected List<String> treatedGUIDs = new ArrayList<String>();

	protected List<JsonObject> deletions = new ArrayList<>();

	public abstract JsonObject load(long timeout);

	public void initLoader(ConnectorInstance instance) {
		this.instance = instance;
		this.ends = Boolean.valueOf(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_HAS_END.getKey()));
		this.sleepTime = Long
				.parseLong(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_SLEEP_TIME.getKey(), false));
		this.loadTimeout = Long
				.parseLong(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_TIMEOUT.getKey(), false));
		this.resetVars();
	}

	protected void resetVars() {
		guidsExisting = new HashMap<>();
		List<GlobalIdDb> listGuids = globalIdRepo.findByInventory(instance.getConnector().getInventoryName());

		for (GlobalIdDb guid : listGuids) {
			guidsExisting.put(guid.getLocalId(), guid.getGlobalId());
		}
		page = connectorStatsRepo.findByExecutionConnectorNameOrderByExecutionStartDateDesc(
				this.instance.getConnector().getConnectorName(), PageRequest.of(0, 1));
		treatedGUIDs = new ArrayList<String>();
		deletions = new ArrayList<>();
		allGUIDs = new ArrayList<>();
		currentIndex = -1;
		deletionIndex = -1;
		reset = true;
		loaded = null;
		all = null;
	}

	public boolean hasEnd() {
		return ends;
	}

	public long getSleepTime() {
		return this.sleepTime;
	}

	public long getLoadTimeout() {
		return this.loadTimeout;
	}

	public abstract void stop();

	public boolean hasDeletions() {
		return !this.deletions.isEmpty();
	}

	public void checkDeletions() {
		List<GlobalIdDb> listGUIDs = globalIdRepo.findByInventory(this.inventoryName);

		// Tractament globalIds no existents ja
		if (!this.allGUIDs.isEmpty()) {
			for (int i = 0; i < listGUIDs.size(); i++) {
				boolean found = Collections.binarySearch(this.allGUIDs, listGUIDs.get(i).getGlobalId()) > 0;
				if (!found) {

					// No s'ha trobat GUID cal enviar global id amb element null
					LoaderJsonObject obj = new LoaderJsonObject();
					obj.setGlobalId(listGUIDs.get(i).getGlobalId());
					deletions.add(gson.fromJson(gson.toJson(obj), JsonObject.class));
					globalIdRepo.delete(listGUIDs.get(i));
					this.instance.getConnectorStats().addDeleted();

				}
			}
		}

		checkedDeletions = true;

	}

	public JsonObject getDeletion() {			
		this.deletionIndex++;
		JsonObject deletion = null;
		try {
			deletion = deletions.get(deletionIndex);
		} catch (IndexOutOfBoundsException e) {
			deletion = null;
		}	
		if (deletion == null && !this.hasEnd()) {
			this.resetVars();
			return null;
		}
		return deletion;
	}
}
