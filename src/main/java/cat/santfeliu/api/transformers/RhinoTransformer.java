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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.enumerator.RhinoTransformerConfigKeys;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.JavaScriptConverter;

public class RhinoTransformer extends ConnectorTransformer {

	private static final Logger log = LoggerFactory.getLogger(RhinoTransformer.class);

	@Override
	public JsonObject transform(JsonObject object) {
		Gson objMap = new Gson();
		Map<String, String> scopeObjects = new HashMap<>();
		JsonObject fJson = object;
		String modelGlobalId = "";
		String fieldRef = this.params.getParamValue(
				RhinoTransformerConfigKeys.TRANSFORMER_SCRIPT_SCOPE_MODEL_GLOBAL_ID_NAME.getKey(), false);

		if (fieldRef != null && !fieldRef.isEmpty()) {
			try {
				String modelLocalId = JsonPath.parse(fJson.toString()).<String>read(this.params
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
			}

		}

		if (modelGlobalId != null && !modelGlobalId.isBlank()) {
			scopeObjects.put(
					this.params.getParamValue(
							RhinoTransformerConfigKeys.TRANSFORMER_SCRIPT_SCOPE_MODEL_GLOBAL_ID_NAME.getKey()),
					modelGlobalId);

		}

		String output = "";
		ObjectMapper mapper = new ObjectMapper();

		JavaScriptConverter converter = new JavaScriptConverter(this.params);
		try {
			converter.begin(scopeObjects);
			JsonNode node = mapper.readTree(fJson.toString());
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
			JsonNode outputFeature = converter.convert(node);
			output = mapper.writeValueAsString(outputFeature);
		} catch (Exception ex) {
			log.error("transformData@RhinoService - error while converting feature {} with exception ",
					fJson.toString(), ex);
		} finally {
			converter.end();
		}
		log.debug("transform@RhinoTransformer - transform jsonObject object {} to jsonNode", object.toString());
		return new Gson().fromJson(output, JsonObject.class);
	}

}
