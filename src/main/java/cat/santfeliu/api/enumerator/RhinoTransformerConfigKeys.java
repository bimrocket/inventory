package cat.santfeliu.api.enumerator;

public enum RhinoTransformerConfigKeys {
	
	TRANSFORMER_JAVASCRIPT_SCRIPT("javascript.script", "JS script used for transformation", true),
	TRANSFORMER_TRANSFORM_LOCAL_ID("transform.local.id", "Boolean that indicates if we should transform local id to global id", true),
	TRANSFORMER_JSON_PATH_LOCAL_ID("json.path.local.id", "JSON Path to local_id used for global id creation", false),
	TRANSFORMER_JSON_PATH_LOCAL_MODEL_ID("json.path.local.model.id", "JSON Path to model local id used for model global id creation", false),
	TRANSFORMER_MODEL_INVENTORY_NAME("model.inventory.name", "Inventory name of model", false),
	TRANSFORMER_SCRIPT_SCOPE_JSON_OBJECT_NAME("scope.json.object.name", "Name of arriving json node to transform inside javascript, should be singular name", true),
	TRANSFORMER_SCRIPT_SCOPE_GLOBAL_ID_NAME("scope.global.id.name", "Name of globalId variable inside javascript, should be singular name", false),
	TRANSFORMER_SCRIPT_SCOPE_MODEL_GLOBAL_ID_NAME("scope.model.global.id.name", "Name of model globalId variable inside javascript, should be singular name", false),
	TRANSFORMER_TRANSFORM_GEOMETRY("transform.geometry", "Should transform geometry ? (true or false)", true),
	TRANSFORMER_JSON_PATH_GEOMETRY("json.path.geometry", "JSON Path to geometry object", false),
	TRANSFORMER_TRANSFORM_GEOMETRY_OUTPUT("transform.geometry.output", "Output geometry can be Line, MultiLine, Polygon, etc..", true);

	
	private String key;
	private String description;
	private boolean required;
	
	private RhinoTransformerConfigKeys(String key, String description, boolean required) {
		this.key = key;
		this.description = description;
		this.required = required;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public boolean isRequired() {
		return this.required;
	}
}
