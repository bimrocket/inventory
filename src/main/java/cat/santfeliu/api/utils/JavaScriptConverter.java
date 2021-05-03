package cat.santfeliu.api.utils;

import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cat.santfeliu.api.model.InputOutputIoModel;


/**
 *
 * @author realor
 */
public class JavaScriptConverter extends ObjectConverter {
	protected Script script;
	protected Context context;
	protected ScriptableObject scope;
	protected ObjectMapper mapper;
	protected Map<String, String> scopeObjects;

	public JavaScriptConverter(InputOutputIoModel inventory, Map<String, String> scopeObjects) {
		super(inventory);
		this.scopeObjects = scopeObjects;
		mapper = new ObjectMapper();
	}

	@Override
	public void begin() throws Exception {
		context = Context.enter();

		String source = io.getOutputTemplateOrScript();

		script = context.compileString(source, "converter", 0, null);

		scope = context.initStandardObjects();

		for (String key : scopeObjects.keySet()) {
			ScriptableObject.putProperty(scope, key, scopeObjects.get(key));
		}
		
		ScriptableObject.putProperty(scope, "mapper", mapper);
	}

	@Override
	public void end() {
		Context.exit();
	}

	@Override
	public JsonNode convert(JsonNode object) {
		ScriptableObject.putProperty(scope, "node", object);
		ScriptableObject.putProperty(scope, "object", new JsonNodeScriptable(scope, object));

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
