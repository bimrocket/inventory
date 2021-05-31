package cat.santfeliu.api.enumerator;

public enum JSONKafkaSenderConfigKeys {
	KAFKA_TOPIC_NAME("topic.name", "Nombre del topic de kafka al qual enviar", true),
	KAFKA_PARTITIONS("topic.partitions", "Particiones de topic kafka", true);
	
	private String key;
	private String description;
	private boolean required;

	private JSONKafkaSenderConfigKeys(String key, String description, boolean required) {
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
