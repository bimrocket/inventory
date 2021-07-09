package cat.santfeliu.api.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import cat.santfeliu.api.beans.LoaderJsonObject;
import cat.santfeliu.api.enumerator.GlobalLoaderConfigKeys;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.repo.ConnectorStatsRepo;
import cat.santfeliu.api.repo.GlobalIdRepo;
import cat.santfeliu.api.utils.ConfigProperty;
import cat.santfeliu.api.utils.InventoryUtils;

public abstract class ConnectorTestLoader extends ConnectorComponent {

	// LOG
	private static final Logger logLoader = LoggerFactory.getLogger(ConnectorLoader.class);

	// PARAMS
	@ConfigProperty(name="has.end", description="Defines if objects have limited number or they keep coming to load never ending")
	protected boolean ends;

	@ConfigProperty(name = "sleep.time", description = "Time to sleep if objects have ended before loader will start another load")
	protected int sleepTime;

	@ConfigProperty(name = "loader.timeout", description = "Time to sleep if objects have ended before loader will start another load")
	protected int loadTimeout;
	
	@ConfigProperty(name = "filter.field", description = "Json Path to element with which we will filter incoming data (usually property that equals update date)", required = false)
	protected String filterField;

	@ConfigProperty(name = "filter.format", description = "Format of filter field, example for date : yyyy-MM-dd'T'HH:mm:ss'Z'", required = false)
	protected String filterFormat;
	
	@Autowired
	protected GlobalIdRepo globalIdRepo;

	@Autowired
	protected ConnectorStatsRepo connectorStatsRepo;

	@Autowired
	protected InventoryUtils invUtils;

	/**
	 * Indicates whether to restart the charger
	 */
	protected boolean reset = true;

	/**
	 * Instance of connector corresponding to the load
	 */
	protected ConnectorInstance instance;

	protected final ObjectMapper mapper = new ObjectMapper();
	/**
	 * Loaded items filtered to return by load
	 */
	protected ArrayNode loaded = mapper.createArrayNode();

	/**
	 * All items without filter
	 */
	protected ArrayNode all = mapper.createArrayNode();

	/**
	 * Current object index to retrieve via load
	 */
	protected int currentIndex = -1;

	/**
	 * Current removal index to recover via load
	 */
	protected int deletionIndex = -1;

	/**
	 * Indicates whether deletions have been checked
	 */
	protected boolean checkedDeletions = false;

	/**
	 * Corresponds to all GUIDs in the all array
	 */
	protected List<String> allGUIDs = new ArrayList<String>();

	/**
	 * Corresponds to all GUIDs in the database
	 */
	protected Map<String, String> guidsExisting = new HashMap<>();

	/**
	 * Corresponds to all GUIDs that have gone through load
	 */
	protected List<String> treatedGUIDs = new ArrayList<String>();

	/**
	 * Corresponds to all objects to be deleted It can be filled with checkDeletions
	 * method
	 */
	protected List<JsonNode> deletions = new ArrayList<>();

	/**
	 * Contains the last run It is used to filter data
	 */
	protected Page<ConnectorExecutionStatsDb> page = null;

	public abstract JsonNode load(long timeout);

	/**
	 * Initializes connector loader and retrieves config variables necessary for its
	 * execution
	 * 
	 * @param instance
	 */
	public void initLoader(ConnectorInstance instance) {
		logLoader.info("initLoader@ConnectorLoader - initialitzation of connector with name {}",
				instance.getConnector().getConnectorName());
		this.instance = instance;
		this.resetVars();
	}

	/**
	 * Restart loader variables Used if you want to reload
	 */
	protected void resetVars() {
		guidsExisting = new HashMap<>();
		List<GlobalIdDb> listGuids = globalIdRepo.findByInventory(this.inventoryName);

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
		checkedDeletions = false;
	}

	/**
	 * Returns if the loader has
	 * 
	 * @return
	 */
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

		// GlobalIds treatmentIds no longer exist
		if (!this.allGUIDs.isEmpty()) {
			for (int i = 0; i < listGUIDs.size(); i++) {
				boolean found = false;
				for (int j = 0; j < this.allGUIDs.size() && !found; j++) {
					found = this.allGUIDs.get(j).equals(listGUIDs.get(i).getGlobalId());
				}
				if (!found) {

					// GUID not found, global id must be sent with null element
					LoaderJsonObject obj = new LoaderJsonObject();
					obj.setGlobalId(listGUIDs.get(i).getGlobalId());
					try {
						deletions.add(mapper.readTree(mapper.writeValueAsString(obj)));
					} catch (JsonProcessingException e) {
						// never happens
					}
					globalIdRepo.delete(listGUIDs.get(i));
					this.instance.getConnectorStats().addDeleted();
					logLoader.debug(
							"checkDeletions@ConnectorLoader - guid not found, must send global id with null element");

				}
			}
		}

		checkedDeletions = true;

	}

	public JsonNode getDeletion() {
		this.deletionIndex++;
		JsonNode deletion = null;
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
