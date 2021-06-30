package cat.santfeliu.api.transformers;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.enumerator.RhinoTransformerConfigKeys;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.JavaScriptConverter;

public class RhinoTransformer extends ConnectorTransformer {

	private static final Logger log = LoggerFactory.getLogger(RhinoTransformer.class);

	@Override
	public JsonNode transform(JsonNode object) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> scopeObjects = new HashMap<>();
		JsonNode fJson = object;
		String modelGlobalId = "";
		String fieldRef = this.params.getParamValue(
				RhinoTransformerConfigKeys.TRANSFORMER_SCRIPT_SCOPE_MODEL_GLOBAL_ID_NAME.getKey(), false);
		if (fieldRef != null && !fieldRef.isEmpty()) {
			try {
				String modelLocalId = JsonPath.parse(mapper.writeValueAsString(fJson)).<String>read(this.params
						.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_JSON_PATH_LOCAL_MODEL_ID.getKey()));
				String modelInventoryName = this.params
						.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_MODEL_INVENTORY_NAME.getKey());
				Optional<GlobalIdDb> globalIdModel = globalIdRepo.findByInventoryAndLocalId(modelInventoryName,
						modelLocalId);

				if (globalIdModel.isPresent()) {
					modelGlobalId = globalIdModel.get().getGlobalId();
				} else {
					modelGlobalId = invUtils.getGuid();
					GlobalIdDb guid = new GlobalIdDb();
					guid.setGlobalId(modelGlobalId);
					guid.setInventoryName(modelInventoryName);
					guid.setLocalId(modelLocalId);
					globalIdRepo.save(guid);
				}
			} catch (PathNotFoundException e) {
				// delete record incoming ignore
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		}

		if (modelGlobalId != null && !modelGlobalId.isBlank()) {
			scopeObjects.put(
					this.params.getParamValue(
							RhinoTransformerConfigKeys.TRANSFORMER_SCRIPT_SCOPE_MODEL_GLOBAL_ID_NAME.getKey()),
					modelGlobalId);

		}

		JsonNode outputFeature = null;

		JavaScriptConverter converter = new JavaScriptConverter(this.params);
		try {
			converter.begin(scopeObjects);
			JsonNode node = fJson;
			String transformGeoStr = this.params.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_TRANSFORM_GEOMETRY.getKey(), false);
			if (transformGeoStr != null && Boolean.valueOf(transformGeoStr)) {
				String geojsonPath = this.params
						.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_JSON_PATH_GEOMETRY.getKey());
				String geojson = mapper.writeValueAsString(JsonPath.parse(fJson.toString()).<LinkedHashMap<String, String>>read(geojsonPath));
				JsonNode geoJsonNode = mapper.readTree(geojson);
				String outputGeo = this.params.getParamValue(RhinoTransformerConfigKeys.TRANSFORMER_TRANSFORM_GEOMETRY_OUTPUT.getKey());
				if (outputGeo.equals("Point")) {
					GeometryJSON gjson = new GeometryJSON();						
					Reader reader = new StringReader(geojson.strip());
					Geometry geo = geo = gjson.read(reader);
					Point p = geo.getCentroid();
					String[] paths = geojsonPath.split("\\.");
					JsonNode currentNode = node;
					for (int i=1;i < paths.length - 1;i++) {
						currentNode = currentNode.get(paths[i]);
					}
					((ObjectNode)currentNode).put(paths[paths.length-1], gjson.toString(p));
				}
				
			}
			outputFeature = converter.convert(node); 
		} catch (Exception ex) {
			try {
				log.error("transformData@RhinoService - error while converting feature {} with exception ",
						mapper.writeValueAsString(fJson), ex);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} finally {
			converter.end();
		}
		try {
			log.debug("transform@RhinoTransformer - transform jsonObject object {} to jsonNode", mapper.writeValueAsString(object));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (outputFeature.get("globalId").isNull()) {
			log.info("gid is null");
		}
		return outputFeature;
	}

}
