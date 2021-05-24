package cat.santfeliu.api.enumerator;

public enum RhinoTransformerKeys {
	TRANSFORMER_JSON_PATH_DATA_ARRAY("paths.input.data.array"),
	TRANSFORMER_JSON_PATH_LOCAL_ID("paths.input.local.id"),
	TRANSFORMER_JSON_PATH_MODEL_ID("paths.input.model.id"),
	TRANSFORMER_JSON_MODEL_NAME("model.name");
	
	private String key;
	
	private RhinoTransformerKeys(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
}
