package cat.santfeliu.api.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.enumerator.RhinoTransformerConfigKeys;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.repo.GlobalIdRepo;
import cat.santfeliu.api.utils.InventoryUtils;
import cat.santfeliu.api.utils.JavaScriptConverter;

public class RhinoTransformer extends ConnectorTransformer {

	private static final Logger log = LoggerFactory.getLogger(RhinoTransformer.class);

	@Override
	public JsonObject transform(JsonObject object) {
		List<String> listTreatedGUIDs = new ArrayList<>();
		Gson objMap = new Gson();
		JsonObject fJson = object;

		String localId = JsonPath.parse(fJson.toString()).<String>read(
				this.params.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_JSON_PATH_LOCAL_ID.getKey()));
		String globalId = null;
		Optional<GlobalIdDb> hasIdOpt = globalIdRepo.findByInventoryAndLocalId(this.inventoryName, localId);
		if (hasIdOpt.isEmpty()) {
			// No s'ha trobat GUID cal obtenir-lo i inserir-lo
			globalId = InventoryUtils.getGuid();
			GlobalIdDb guid = new GlobalIdDb();
			guid.setGlobalId(globalId);
			guid.setInventoryName(this.inventoryName);
			guid.setLocalId(localId);
			globalIdRepo.save(guid);
		} else {
			globalId = hasIdOpt.get().getGlobalId();
		}
		boolean alreadyTreated = false;
		for (int j = 0; j < listTreatedGUIDs.size() && !alreadyTreated; j++) {
			alreadyTreated = listTreatedGUIDs.get(j).equals(globalId);
		}		
		Map<String, String> scopeObjects = new HashMap<>();	
		String modelGlobalId = "";
		if (!alreadyTreated) {
			listTreatedGUIDs.add(globalId);

			scopeObjects.put(this.params.getParamValue(
					RhinoTransformerConfigKeys.TRANSFORMER_SCRIPT_SCOPE_GLOBAL_ID_NAME.getKey()), globalId);
			String fieldRef = this.params
					.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_SCRIPT_SCOPE_MODEL_GLOBAL_ID_NAME.getKey(), false);

			if (fieldRef != null && !fieldRef.isEmpty()) {

				String modelLocalId = JsonPath.parse(fJson.toString()).<String>read(this.params
						.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_JSON_PATH_LOCAL_MODEL_ID.getKey()));
				String modelInventoryName = this.params
						.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_MODEL_INVENTORY_NAME.getKey());
				Optional<GlobalIdDb> globalIdModel = globalIdRepo
						.findByInventoryAndLocalId(modelInventoryName, modelLocalId);
	
				if (globalIdModel.isPresent()) {
					modelGlobalId = globalIdModel.get().getGlobalId();
				} else {
					modelGlobalId = InventoryUtils.getGuid();
					GlobalIdDb guid = new GlobalIdDb();
					guid.setGlobalId(globalId);
					guid.setInventoryName(modelInventoryName);
					guid.setLocalId(modelLocalId);
					globalIdRepo.save(guid);
				}
			}

		}
		if (modelGlobalId != null && !modelGlobalId.isBlank()) {
			scopeObjects.put(this.params.getParamValue(
					RhinoTransformerConfigKeys.TRANSFORMER_SCRIPT_SCOPE_MODEL_GLOBAL_ID_NAME.getKey()), modelGlobalId );
				
		}
		String output = "";
		JavaScriptConverter converter = new JavaScriptConverter(this.params);
		try {
			converter.begin(scopeObjects);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode outputFeature = converter.convert(mapper.readTree(fJson.toString()));
			output = mapper.writeValueAsString(outputFeature);
		} catch (Exception ex) {
			log.error("transformData@RhinoService - error while converting feature {} with exception ", fJson.toString(), ex);
		} finally {
			converter.end();
		}
		return new Gson().fromJson(output, JsonObject.class);

//		List<GlobalIdDb> listGUIDs = globalIdRepo.findByInventory(this.inventoryName);
//
//		// Tractament globalIds no existents ja
//		for(int i = 0;i<listGUIDs.size();i++)
//		{
//			boolean found = false;
//			for (int j = 0; j < listTreatedGUIDs.size() && !found; j++) {
//				found = listTreatedGUIDs.get(j).equals(listGUIDs.get(i).getGlobalId());
//			}
//			if (!found) {
//
//				// No s'ha trobat GUID cal enviar missatge a Kafka de eliminació i eliminar-ho
//				// de base de dades
//				if (ioModel.getOutputDataFormat().equals("application/json")) {
//					KafkaMessageDelete deleteMsg = new KafkaMessageDelete();
//					deleteMsg.setId(listGUIDs.get(i).getGlobalId());
//					deleteMsg.setType(inv.getInventoryName());
//					kafkaPro.sendMessage(inv.getInventoryKafkaTopicId(), objMap.toJson(deleteMsg));
//				}
//
//				globalIdRepo.delete(listGUIDs.get(i));
//			}
//			return object;
//		}
	}




}
