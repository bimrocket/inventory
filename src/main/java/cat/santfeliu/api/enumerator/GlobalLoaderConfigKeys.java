package cat.santfeliu.api.enumerator;

public enum GlobalLoaderConfigKeys {

	LOADER_HAS_END("has.end", "Defineix si objectes van arribant sense fin o hi ha un número definit de objectes", true),
	LOADER_IS_FULL_LOAD("is.full.load", "Indica si es una càrrega completa", true);

	private String key;
	private String description;
	private boolean required;

	private GlobalLoaderConfigKeys(String key, String description, boolean required) {
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
