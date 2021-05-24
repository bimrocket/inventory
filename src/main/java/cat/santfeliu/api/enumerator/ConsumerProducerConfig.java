package cat.santfeliu.api.enumerator;

public enum ConsumerProducerConfig {

	LOADER_URL("ContainerProducer.loader.url"),
	LOADER_PARAMS("ContainerProducer.loader.params"),
	LOADER_USERNAME("ContainerProducer.loader.username"),
	LOADER_PASSWORD("ContainerProducer.loader.password"),
	LOADER_LAYERS_MULTIPLE("ContainerProducer.loader.layers.*"),
	LOADER_FORMAT("ContainerProducer.loader.format");
	
	private String key;
	
	private ConsumerProducerConfig(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
}
