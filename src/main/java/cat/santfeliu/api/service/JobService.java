package cat.santfeliu.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.beans.KafkaMessageDelete;
import cat.santfeliu.api.beans.ResponseWfsContainer;
import cat.santfeliu.api.enumerator.JobSourcesEnum;
import cat.santfeliu.api.enumerator.JobSourcesParamsEnum;
import cat.santfeliu.api.model.GlobalIdModel;
import cat.santfeliu.api.model.InputOutputIoModel;
import cat.santfeliu.api.model.InventoryModel;
import cat.santfeliu.api.repo.GlobalidRepo;
import cat.santfeliu.api.repo.InputOutputIoRepo;
import cat.santfeliu.api.repo.InventoryRepo;
import cat.santfeliu.api.repo.SourceRepo;
import cat.santfeliu.api.utils.InventoryUtils;
import cat.santfeliu.api.utils.SourceParamsUtils;
import net.minidev.json.JSONArray;

@Service
public class JobService {

	private static final Logger log = LoggerFactory.getLogger(JobService.class);

	@Autowired
	InventoryRepo invRepo;

	@Autowired
	SourceRepo srcRepo;

	@Autowired
	GeoserverJobService geoserverService;

	@Autowired
	TransformerService transformer;

	@Autowired
	SourceParamsUtils sourceParamUtils;

	@Autowired
	GlobalidRepo globalIdRepo;

	@Autowired
	InputOutputIoRepo ioRepo;
	
	@Autowired
	KafkaProducer kafkaPro;

	@Autowired
	InventoryUtils invUtils;

	public void runJob(String jobId) {
		Optional<InventoryModel> jobModel = invRepo.findJob(jobId);
		log.info("Starting job with id '{}' and source '{}'", jobId);
		if (jobModel.isPresent() && jobModel.get().getInventorySourceId().equals(JobSourcesEnum.GEOSERVER.getValue())) {
			runGeoserverJob(jobModel.get());
		}

	}

	private void runGeoserverJob(InventoryModel inv) {
		Map<String, String> sourceParams = sourceParamUtils.getSourceParams(JobSourcesEnum.GEOSERVER.getValue());
		Optional<InputOutputIoModel> ioModel = ioRepo.findById(inv.getInventoryIoId());

		String format = sourceParams.get(JobSourcesParamsEnum.GEOSERVER_FORMAT.getParamKey());
		ResponseWfsContainer responses = geoserverService.getWfsFeatures(
				sourceParams.get(JobSourcesParamsEnum.GEOSERVER_URI.getParamKey()),
				sourceParams.get(JobSourcesParamsEnum.GEOSERVER_USERNAME.getParamKey()),
				sourceParams.get(JobSourcesParamsEnum.GEOSERVER_PASSWORD.getParamKey()), format,
				sourceParams.get(JobSourcesParamsEnum.GEOSERVER_PARAMS.getParamKey()),
				sourceParams.get(JobSourcesParamsEnum.GEOSERVER_LAYER.getParamKey()));

		if (responses.getHttpStatus() != 200) {
			log.error("Error response code not 200 in job id {}, response json : '{}'", inv.getInventoryId(),
					responses.getResponse());
			return;
		}

		if (ioModel.isPresent() && ioModel.get().getInputDataFormat().equals("application/json")) {
			runInputJsonJob(inv, ioModel.get(), responses.getResponse());
		} else {
			log.error("Unrecognized format in job id {}", inv.getInventoryId());
		}

	}

	private void runInputJsonJob(InventoryModel inv, InputOutputIoModel ioModel, String json) {

		List<String> listTreatedGUIDs = new ArrayList<>();
		Gson objMap = new Gson();
		JsonArray fArray = new JsonArray();
		try {
			String fString = JsonPath.parse(json).<JSONArray>read(ioModel.getInputDataPath()).toJSONString();
			log.info(fString);
			fArray = objMap.fromJson(fString, JsonArray.class);
		} catch (Exception e) {
			log.error("Error while parsing features array in object json :: {], with json addresse exception :::", json,
					e);
		}
		if (fArray.size() == 0) {
			log.error("Error no features found in job id {}, response json : '{}'", inv.getInventoryId(), json);
			return;
		}

		// Transformació dades i publicació a Kafka
		for (int i = 0; i < fArray.size(); i++) {
			JsonObject fJson = fArray.get(i).getAsJsonObject();

			String localId = JsonPath.parse(fJson.toString()).<String>read(ioModel.getInputLocalIdPath());
			String globalId = null;
			Optional<GlobalIdModel> hasIdOpt = globalIdRepo.findByInventoryAndLocalId(inv.getInventoryId(), localId);
			if (hasIdOpt.isEmpty()) {
				// No s'ha trobat GUID cal obtenir-lo i inserir-lo
				globalId = invUtils.getGuid();
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

	}

}
