package cat.santfeliu.api.enumerator;

public enum JSONKafkaLoaderConfigKeys {
	KAFKA_GROUP_ID("consumer.group.id", "Nombre del group kafka al qual pertany el loader", true),
	KAFKA_TOPIC_NAME("consumer.topic.name", "Nombre del topic de kafka al qual enviar", true),
	LOADER_HAS_END("has.end", "Defineix si objectes van arribant sense fin o hi ha un número definit de objectes", true);
	
	private String key;
	private String description;
	private boolean required;

	private JSONKafkaLoaderConfigKeys(String key, String description, boolean required) {
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
