package cat.santfeliu.api.service;

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

import cat.santfeliu.api.model.InputOutputIoModel;
import cat.santfeliu.api.model.InventoryModel;
import cat.santfeliu.api.repo.GlobalidRepo;

@Service
public class TransformerService {

	private static final Logger log = LoggerFactory.getLogger(TransformerService.class);

	@Autowired
	private GlobalidRepo globalIdRepo;

	private final Integer INTERPRETER_JSON_PATH = 3;

	public String modelDataForKafka(InventoryModel inv, String globalId, InputOutputIoModel ioModel, String data) {
		String dataTrans = "";

		String template = ioModel.getOutputTemplateOrScript();
		if (ioModel.getInputDataFormat().equals("application/json")
				&& ioModel.getInputInterpreterId() == INTERPRETER_JSON_PATH) {
			JsonObject json = JsonParser.parseString(data).getAsJsonObject();
			if (ioModel.getOutputInterpreterId() == INTERPRETER_JSON_PATH) {
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
