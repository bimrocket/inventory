package cat.santfeliu.api.transformers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.exceptions.ApiErrorException;

public class RhinoTransformer extends ConnectorTransformer {

	private static final Logger log = LoggerFactory.getLogger(RhinoTransformer.class);
	
	@Override
	public JsonObject transform(JsonObject object) {
		return object;
	}

	public String transform(String inputFormat, String records) {
		if (!inputFormat.equals("application/json")) {
			String error = String.format("invalid format '%s' incoming for RhinoTransformer", inputFormat);
			log.error("transform@RhinoTransformer - {}", error);
        	throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, error);
		}

//		List<String> listTreatedGUIDs = new ArrayList<>();
//		Gson objMap = new Gson();
//		JsonArray fArray = new JsonArray();
//		try {
//			String fString = JsonPath.parse(records).<JSONArray>read(this.params.getParamValue(RhinoTransformerKeys.TRANSFORMER_JSON_PATH_DATA_ARRAY.getKey())).toJSONString();
//			fArray = objMap.fromJson(fString, JsonArray.class);
//		} catch (Exception e) {
//			log.error("transform@RhinoTransformer - Error while parsing features array in object json :: {}, with json addresse exception :::", records,
//					e);
//			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while parsing features array", "Error while parsing features array");
//			
//		}
//		if (fArray.size() == 0) {
//			log.error("transform@RhinoTransformer - Error no features found");
//			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error no features found", "Error no features found");
//			
//		}

		
		/*
		// Transformació dades i publicació a Kafka
		for (int i = 0; i < fArray.size(); i++) {
			JsonObject fJson = fArray.get(i).getAsJsonObject();

			String localId = JsonPath.parse(fJson.toString()).<String>read(this.params.getParamValue(RhinoTransformerKeys.TRANSFORMER_JSON_PATH_LOCAL_ID.getKey()));
			String globalId = null;
			Optional<GlobalIdModel> hasIdOpt = globalIdRepo.findByInventoryAndLocalId(inv.getInventoryId(), localId);
			if (hasIdOpt.isEmpty()) {
				// No s'ha trobat GUID cal obtenir-lo i inserir-lo
				globalId = InventoryUtils.getGuid();
				GlobalIdModel guid = new GlobalIdModel();
				guid.setGlobalId(globalId);
				guid.setInventoryModel(inv);
				guid.setLocalId(localId);
				globalIdRepo.save(guid);
			} else {
				globalId = hasIdOpt.get().getGlobalId();
			}
			boolean alreadyTreated = false;
			for (int j = 0; j < listTreatedGUIDs.size() && !alreadyTreated; j++) {
				alreadyTreated = listTreatedGUIDs.get(j).equals(globalId);
			}
			if (!alreadyTreated) {
				listTreatedGUIDs.add(globalId);
				String modelKafka = transformer.modelDataForKafka(inv, globalId, ioModel, fJson.toString());
				if (modelKafka != null && !modelKafka.equals("")) {
					kafkaPro.sendMessage(inv.getInventoryKafkaTopicId(), modelKafka);
				} else {
					log.error("runInputJsonJob@JobService - empty model created for json {}", fJson.toString());
				}
			}
			
		}
		List<GlobalIdModel> listGUIDs = globalIdRepo.findByInventory(inv.getInventoryId());
		
		// Tractament globalIds no existents ja
		for (int i = 0; i < listGUIDs.size(); i++) {
			boolean found = false;
			for (int j = 0; j < listTreatedGUIDs.size() && !found;j ++) {
				found = listTreatedGUIDs.get(j).equals(listGUIDs.get(i).getGlobalId());
			}
			if (!found) {

				// No s'ha trobat GUID cal enviar missatge a Kafka de eliminació i eliminar-ho de base de dades
				if (ioModel.getOutputDataFormat().equals("application/json")) {
					KafkaMessageDelete deleteMsg = new KafkaMessageDelete();
					deleteMsg.setId(listGUIDs.get(i).getGlobalId());
					deleteMsg.setType(inv.getInventoryName());
					kafkaPro.sendMessage(inv.getInventoryKafkaTopicId(), objMap.toJson(deleteMsg));
				}
				
				globalIdRepo.delete(listGUIDs.get(i));
			}
		}
*/
		return records;
	}



}
