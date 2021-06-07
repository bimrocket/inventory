package cat.santfeliu.api.components;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cat.santfeliu.api.beans.KafkaMessageDelete;
import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.enumerator.GlobalLoaderConfigKeys;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.repo.GlobalIdRepo;

public abstract class ConnectorLoader extends ConnectorComponent {

	private static final Logger logLoader = LoggerFactory.getLogger(ConnectorLoader.class);

	protected boolean ends;

	protected ConnectorInstance instance;

	@Autowired
	protected GlobalIdRepo globalIdRepo;

	protected final List<String> treatedGUIDs = new ArrayList<String>();

	protected final List<JsonObject> deletions = new ArrayList<>();

	protected final Gson gson = new Gson();

	private int deletionIndex = 0;

	protected boolean checkedDeletions = false;

	public abstract JsonObject load(int timeout);

	public void initLoader(ConnectorInstance instance) {
		this.instance = instance;
		this.ends = Boolean.valueOf(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_HAS_END.getKey()));
	}

	public boolean hasEnd() {
		return ends;
	}

	public abstract void stop();

	public boolean hasDeletions() {
		return !this.deletions.isEmpty();
	}

	public boolean isFullLoad() {
		return Boolean.valueOf(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_IS_FULL_LOAD.getKey()));
	}
	
	public void checkDeletions() {
		List<GlobalIdDb> listGUIDs = globalIdRepo.findByInventory(this.inventoryName);

		// Tractament globalIds no existents ja
		if (!this.treatedGUIDs.isEmpty()) {
			for (int i = 0; i < listGUIDs.size(); i++) {
				boolean found = false;
				for (int j = 0; j < this.treatedGUIDs.size() && !found; j++) {
					found = this.treatedGUIDs.get(j).equals(listGUIDs.get(i).getGlobalId());
				}
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
		try {
			JsonObject deletion = deletions.get(deletionIndex);
			this.deletionIndex = deletionIndex + 1;
			return deletion;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
