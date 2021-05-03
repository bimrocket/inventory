package cat.santfeliu.api.service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import cat.santfeliu.api.model.GlobalIdModel;
import cat.santfeliu.api.model.InputOutputIoModel;
import cat.santfeliu.api.model.InventoryModel;
import cat.santfeliu.api.repo.GlobalidRepo;
import cat.santfeliu.api.repo.InventoryRepo;
import cat.santfeliu.api.utils.InventoryUtils;

@Service
public class TransformerService {

	private static final Logger log = LoggerFactory.getLogger(TransformerService.class);

	@Autowired
	private GlobalidRepo globalIdRepo;
	
	@Autowired
	private InventoryRepo invRepo;
	
	@Autowired
	private RhinoService jsService;

	private final Integer INTERPRETER_JS = 1;
	private final Integer INTERPRETER_JSON_PATH = 3;

	public String modelDataForKafka(InventoryModel inv, String globalId, InputOutputIoModel ioModel, String data) {
		String dataTrans = "";

		String template = ioModel.getOutputTemplateOrScript();
		if (ioModel.getInputDataFormat().equals("application/json")
				&& ioModel.getInputInterpreterId() == INTERPRETER_JSON_PATH) {
			JsonObject json = JsonParser.parseString(data).getAsJsonObject();
			if (ioModel.getOutputInterpreterId() == INTERPRETER_JS) {
				return jsService.transformData(inv, ioModel, globalId, data);
			} else if (ioModel.getOutputInterpreterId() == INTERPRETER_JSON_PATH) {
				// We work with template
				Pattern p = Pattern.compile("#[\\w|.|$]+#");
				Matcher m = p.matcher(template);
				String replacedTemplate = new String(template);
				DocumentContext jsonPath = JsonPath.parse(data);
				while (m.find()) {
					String variable = m.group();
					switch (variable) {
					case "#context.globalId#":
						replacedTemplate = replacedTemplate.replace("#context.globalId#", globalId);
						break;
					case "#context.modelGlobalId#":
						String fieldRef = ioModel.getInputLocalModelIdPath();
						if (fieldRef != null && !fieldRef.isEmpty()) {
							String typeModel = ioModel.getInputLocalModelName();
							Optional<InventoryModel> invTypeModel = invRepo.findInvByName(typeModel);
							if (invTypeModel.isEmpty()) {
								log.error("modelDataForKafka@TransformerService - couldn't find typeModel in template json {}", template);
							} else {
								String modelLocalId = jsonPath.<String>read(fieldRef);
								Optional<GlobalIdModel> globalIdModel = globalIdRepo.findByInventoryAndLocalId(invTypeModel.get().getInventoryId(), modelLocalId);
								String typeModelGlobalId = "";
								if (globalIdModel.isPresent()) {
									typeModelGlobalId = globalIdModel.get().getGlobalId();
								} else {
									typeModelGlobalId = InventoryUtils.getGuid();
									GlobalIdModel guid = new GlobalIdModel();
									guid.setGlobalId(globalId);
									guid.setInventoryModel(invTypeModel.get());
									guid.setLocalId(modelLocalId);
									globalIdRepo.save(guid);
								}
								replacedTemplate = replacedTemplate.replace("#context.modelGlobalId#", typeModelGlobalId);
									
							}
								
						}
						break;
					default:
						String jsonPathVar = variable.replaceAll("#", "");
						replacedTemplate = replacedTemplate.replace(variable, jsonPath.<String>read(jsonPathVar));
						break;
					}
				}
				dataTrans = replacedTemplate;
				return dataTrans;
			} else {

			}
		}
		log.error("modelDataForKafka@TransformerService - not recognized input format '{}' for inventory {}",
				ioModel.getInputDataFormat(), inv.getInventoryId());
		return dataTrans;

	}

}
