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

	/**
	 * Indica si loader te fin
	 */
	protected boolean ends;

	/**
	 * Indica si cal reiniciar loader
	 */
	protected boolean reset = true;

	/**
	 * Indica temps que cal dormir-lo en cas de que no tingui fin
	 */
	protected long sleepTime;

	/**
	 * Timeout per el mètode load
	 */
	protected long loadTimeout;

	/**
	 * Instancia de connector corresponent a la carrega
	 */
	protected ConnectorInstance instance;
	
	/**
	 * Elements carregats filtrats a retornar per load
	 */
	protected JsonArray loaded = null;

	/**
	 * Tots els elements sense filtra
	 */
	protected JsonArray all = null;

	protected final Gson gson = new Gson();
	
	/**
	 * Index de objecte actual per recuperar via load
	 */
	protected int currentIndex = -1;

	/**
	 * Index de eliminacio actual per recupera via load
	 */
	protected int deletionIndex = -1;

	/**
	 * Indica si s'han comprovat eliminacions
	 */
	protected boolean checkedDeletions = false;

	/**
	 * Correspon a tots els GUIDs de la array all
	 */
	protected List<String> allGUIDs = new ArrayList<String>();

	/**
	 * Correspon a tots els GUIDs de la base de dades
	 */
	protected Map<String, String> guidsExisting = new HashMap<>();

	/**
	 * Correspon a tots GUIDs que han passat per load
	 */
	protected List<String> treatedGUIDs = new ArrayList<String>();

	/**
	 * Correspon a tots els objects a eliminar
	 * Es pot omplir amb mètode checkDeletions
	 */
	protected List<JsonObject> deletions = new ArrayList<>();

	/**
	 * Conté la última execució 
	 * Es utilitza per filtra dades
	 */
	protected Page<ConnectorExecutionStatsDb> page = null;

	public abstract JsonObject load(long timeout);

	/**
	 * Inicialitza connector loader i recupera variables de config 
	 * necessaries per la seva execució
	 * @param instance
	 */
	public void initLoader(ConnectorInstance instance) {
		this.instance = instance;
		this.ends = Boolean.valueOf(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_HAS_END.getKey()));
		this.sleepTime = Long
				.parseLong(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_SLEEP_TIME.getKey(), false));
		this.loadTimeout = Long
				.parseLong(this.params.getParamValue(GlobalLoaderConfigKeys.LOADER_TIMEOUT.getKey(), false));
		this.resetVars();
	}

	/**
	 * Reinicia variables de loader
	 * Es utilitza si es vol a tornar a fer la carrega
	 */
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

	/**
	 * Retorna si el loader t
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
