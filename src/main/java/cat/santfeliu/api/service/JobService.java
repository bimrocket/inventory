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

		List<GlobalIdModel> listGUIDs = globalIdRepo.findByInventory(inv.getInventoryId());
		List<String> listNew = new ArrayList<>();
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

		for (int i = 0; i < fArray.size(); i++) {
			JsonObject fJson = fArray.get(i).getAsJsonObject();

			String localId = JsonPath.parse(fJson.toString()).<String>read(ioModel.getInputLocalIdPath());
			String globalId = null;
			Optional<GlobalIdModel> hasIdOpt = globalIdRepo.findByInventoryAndLocalId(inv.getInventoryId(), localId);
			if (hasIdOpt.isEmpty()) {
				globalId = invUtils.getGuid();
			} else {
				globalId = hasIdOpt.get().getGlobalId();
			}
			listNew.add(globalId);
			String modelKafka = transformer.modelDataForKafka(inv, globalId, ioModel, fJson.toString());
			log.info(modelKafka);
		}

	}

}
