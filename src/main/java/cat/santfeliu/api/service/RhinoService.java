//package cat.santfeliu.api.service;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jayway.jsonpath.DocumentContext;
//import com.jayway.jsonpath.JsonPath;
//
//import cat.santfeliu.api.model.GlobalIdModel;
//import cat.santfeliu.api.model.InputOutputIoModel;
//import cat.santfeliu.api.model.InventoryModel;
//import cat.santfeliu.api.repo.GlobalidRepo;
//import cat.santfeliu.api.repo.InventoryRepo;
//import cat.santfeliu.api.utils.InventoryUtils;
//import cat.santfeliu.api.utils.JavaScriptConverter;
//
//@Service
//public class RhinoService {
//
//	private static final Logger log = LoggerFactory.getLogger(RhinoService.class);
//	
//	@Autowired
//	private InventoryRepo invRepo;
//	
//	@Autowired
//	private GlobalidRepo globalIdRepo;
//
//	public String transformData(InventoryModel inv, InputOutputIoModel ioModel, String globalId, String data) {
//
//		ObjectMapper mapper = new ObjectMapper();
//		Map<String, String> scopeObjects = new HashMap<>();
//		scopeObjects.put("globalId", globalId);
//		String modelGlobalId = "";
//		String fieldRef = ioModel.getInputLocalModelIdPath();
//		if (fieldRef != null && !fieldRef.isEmpty()) {
//
//			DocumentContext jsonPath = JsonPath.parse(data);
//			String typeModel = ioModel.getInputLocalModelName();
//			Optional<InventoryModel> invTypeModel = invRepo.findInvByName(typeModel);
//			if (invTypeModel.isEmpty()) {
//				log.error("modelDataForKafka@TransformerService - couldn't find typeModel in ioModel {}", ioModel.toString());
//			} else {
//				String modelLocalId = jsonPath.<String>read(fieldRef);
//				Optional<GlobalIdModel> globalIdModel = globalIdRepo.findByInventoryAndLocalId(invTypeModel.get().getInventoryId(), modelLocalId);
//				String typeModelGlobalId = "";
//				if (globalIdModel.isPresent()) {
//					typeModelGlobalId = globalIdModel.get().getGlobalId();
//				} else {
//					typeModelGlobalId = InventoryUtils.getGuid();
//					GlobalIdModel guid = new GlobalIdModel();
//					guid.setGlobalId(globalId);
//					guid.setInventoryModel(invTypeModel.get());
//					guid.setLocalId(modelLocalId);
//					globalIdRepo.save(guid);
//				}
//				modelGlobalId = typeModelGlobalId;
//			}
//				
//		}
//		if (modelGlobalId != null && !modelGlobalId.isEmpty()) {
//			scopeObjects.put("typeModelGlobalId", modelGlobalId);
//		}
//		String output = "";
//		JavaScriptConverter converter = new JavaScriptConverter(ioModel, scopeObjects);
//		try {
//			converter.begin();
//
//			JsonNode outputFeature = converter.convert(mapper.readTree(data));
//			output = mapper.writeValueAsString(outputFeature);
//		} catch (Exception ex) {
//			log.error("transformData@RhinoService - error while converting feature {} with exception ", data, ex);
//		} finally {
//			converter.end();
//		}
//
//		return output;
//	}
//}
