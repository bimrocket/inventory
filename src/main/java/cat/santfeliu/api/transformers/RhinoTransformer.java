package cat.santfeliu.api.transformers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.utils.ConfigProperty;

public class RhinoTransformer extends ConnectorTransformer {

	private static final Logger log = LoggerFactory.getLogger(RhinoTransformer.class);

	@ConfigProperty(name = "scope.model.global.id.name", description = "Name of model globalId variable inside javascript, should be singular name", required=false)
	String scriptScopeModelGlobalIdName;

	@ConfigProperty(name = "json.path.local.model.id",description = "JSON Path to model local id used for model global id creation", required=false)
	String jsonPathLocalModelId;

	@ConfigProperty(name = "model.inventory.name", description = "Inventory name of model", required=false)
	String model_InventoryName;

	@ConfigProperty(name = "javascript.script", description = "JS script used for transformation")
	String javaScript_Script;

	@ConfigProperty(name = "scope.json.object.name", description = "Name of arriving json node to transform inside javascript, should be singular name")
	String scriptScopeJsonObjectName;

	protected Script script;
	protected Context context;
	protected ScriptableObject scope;
	protected ObjectMapper mapper;

	@Override
	public JsonNode transform(JsonNode object) {

		mapper = new ObjectMapper();

		Map<String, String> scopeObjects = new HashMap<>();
		JsonNode fJson = object;
		String modelGlobalId = "";
		String fieldRef = scriptScopeModelGlobalIdName;
		if (fieldRef != null && !fieldRef.isEmpty()) {
			log.debug("transform@RhinoTransformer - {} - fieldRef found searching to modelLocalId", this.connectorName);
			try {
				String modelLocalId = JsonPath.parse(mapper.writeValueAsString(fJson)).<String>read(jsonPathLocalModelId);
				if (modelLocalId != null && modelLocalId.isEmpty()) {
					log.debug("transform@RhinoTransformer - {} - model local id '{}' found creating global id for model", this.connectorName, modelLocalId);
					String modelInventoryName = model_InventoryName;
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
				}

			} catch (PathNotFoundException e) {
				// delete record incoming ignore
			} catch (JsonProcessingException e) {
				this.senError("TRANSFORMER_JSON_MODEL_ID").describe("Error while processing model local id").foundErr().exception(e);
				log.error("transform@RhinoTransformer - {} - error while processing model local id for json {} with JsonException", this.connectorName, fJson.toPrettyString(), e);
			}

		}

		if (modelGlobalId != null && !modelGlobalId.isBlank()) {
			scopeObjects.put(scriptScopeModelGlobalIdName, modelGlobalId);

		}

		JsonNode outputFeature = null;

		try {
			begin(scopeObjects);
			JsonNode node = fJson;
			outputFeature = convert(node);
		} catch (Exception ex) {
			this.senError("TRANSFORMER_CONVERTING_FEATURE").describe("Error while converting feature with exception").foundErr().exception(ex);
			log.error("transform@RhinoService - {} - error while converting feature {} with exception ",
						this.connectorName, fJson.toPrettyString(), ex);
		} finally {
			end();
		}
		return outputFeature;
	}

	public void begin(Map<String, String> scopeObjects) throws Exception {
		context = Context.enter();

		if (javaScript_Script == null) {
			String error = String.format(
					"script is null in javasript converter");
			log.error("begin@JavaScriptConverter - {} - {}", this.connectorName, error);
			return;
		}

		script = context.compileString(javaScript_Script, "converter", 0, null);

		scope = context.initStandardObjects();
		ScriptableObject.putProperty(scope, "mapper", mapper);

		for (String key : scopeObjects.keySet()) {
			ScriptableObject.putProperty(scope, key, scopeObjects.get(key));
		}
	}

	public void end() {
		Context.exit();
	}

	public JsonNode convert(JsonNode object) {
		ScriptableObject.putProperty(scope, "node", object);
		ScriptableObject.putProperty(scope, scriptScopeJsonObjectName, new JsonNodeScriptable(scope, object));

		Object result = script.exec(context, scope);

		if (result instanceof NativeJavaObject) {
			result = ((NativeJavaObject) result).unwrap();
			if (result instanceof JsonNode) {
				return (JsonNode) result;
			}
		} else if (result instanceof Scriptable) {
			return toJsonNode((Scriptable) result);
		}
		return null;
	}

	protected JsonNode toJsonNode(Scriptable scriptable) {
		JsonNode node = null;
		if (scriptable instanceof NativeObject) {
			ObjectNode objectNode = mapper.createObjectNode();
			NativeObject object = (NativeObject) scriptable;
			Object[] ids = object.getIds();
			for (Object id : ids) {
				String name = String.valueOf(id);
				Object value = object.get(id);
				setObjectNodeValue(objectNode, name, value);
			}
			node = objectNode;
		} else if (scriptable instanceof NativeArray) {
			ArrayNode arrayNode = mapper.createArrayNode();
			NativeArray array = (NativeArray) scriptable;
			for (Object elem : array) {
				addArrayNodeValue(arrayNode, elem);
			}
			node = arrayNode;
		} else if (scriptable instanceof JsonNodeScriptable) {
			JsonNodeScriptable nodeScriptable = (JsonNodeScriptable) scriptable;
			node = nodeScriptable.node;
		}
		return node;
	}

	private void setObjectNodeValue(ObjectNode node, String name, Object value) {
		if (value instanceof Scriptable) {
			Scriptable scriptValue = (Scriptable) value;
			node.set(name, toJsonNode(scriptValue));
		} else if (value instanceof JsonNode) {
			node.set(name, (JsonNode) value);
		} else if (value instanceof ConsString) {
			node.put(name, ((ConsString) value).toString());
		} else {
			if (value == null) {
				node.put(name, (String) null);
			} else if (value instanceof String) {
				node.put(name, (String) value);
			} else if (value instanceof Integer) {
				node.put(name, (Integer) value);
			} else if (value instanceof Long) {
				node.put(name, (Long) value);
			} else if (value instanceof Float) {
				node.put(name, (Float) value);
			} else if (value instanceof Double) {
				node.put(name, (Double) value);
			} else if (value instanceof Boolean) {
				node.put(name, (Boolean) value);
			}
		}
	}

	private void addArrayNodeValue(ArrayNode node, Object value) {
		if (value instanceof Scriptable) {
			Scriptable scriptValue = (Scriptable) value;
			node.add(toJsonNode(scriptValue));
		} else if (value instanceof JsonNode) {
			node.add((JsonNode) value);
		} else if (value instanceof ConsString) {
			node.add(((ConsString) value).toString());
		} else {
			if (value == null) {
				node.add((String) null);
			} else if (value instanceof String) {
				node.add((String) value);
			} else if (value instanceof Integer) {
				node.add((Integer) value);
			} else if (value instanceof Long) {
				node.add((Long) value);
			} else if (value instanceof Float) {
				node.add((Float) value);
			} else if (value instanceof Double) {
				node.add((Double) value);
			} else if (value instanceof Boolean) {
				node.add((Boolean) value);
			}
		}
	}

	public class JsonNodeScriptable extends NativeJavaObject {
		private final JsonNode node;

		public JsonNodeScriptable(Scriptable scope, JsonNode node) {
			super(scope, node, JsonNode.class);
			this.node = node;
		}

		@Override
		public String getClassName() {
			return "JsonNodeScriptable";
		}

		@Override
		public Object get(String name, Scriptable scope) {
			if (node.has(name)) {
				JsonNode valueNode = node.get(name);
				if (valueNode.isArray() || valueNode.isObject()) {
					return new JsonNodeScriptable(scope, valueNode);
				} else {
					return nodeToJava(valueNode);
				}
			}
			return super.get(name, scope);
		}

		@Override
		public Object get(int i, Scriptable scope) {
			if (i < node.size()) {
				JsonNode valueNode = node.get(i);
				if (valueNode.isArray() || valueNode.isObject()) {
					return new JsonNodeScriptable(scope, valueNode);
				} else {
					return nodeToJava(valueNode);
				}
			}
			return super.get(i, scope);
		}

		private Object nodeToJava(JsonNode valueNode) {
			if (valueNode.isInt())
				return valueNode.asInt();
			if (valueNode.isLong())
				return valueNode.asInt();
			if (valueNode.isNumber())
				return valueNode.asDouble();
			if (valueNode.isBoolean())
				return valueNode.asBoolean();
			if (valueNode.isTextual())
				return valueNode.asText();
			return null;
		}
	}

}
