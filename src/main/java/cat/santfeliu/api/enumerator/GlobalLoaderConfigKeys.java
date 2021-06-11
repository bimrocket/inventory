package cat.santfeliu.api.enumerator;

public enum GlobalLoaderConfigKeys {

	LOADER_HAS_END("has.end", "Defineix si objectes van arribant sense fin o hi ha un número definit de objectes", true), 
	LOADER_SLEEP_TIME("sleep.time", "Cada quan dormir el thread en miliseconds si no es troba un objecte (=null), es utilitza només si el loader no té fin", false),
	LOADER_TIMEOUT("load.timeout", "Timeout per carregar objectes, per example en cas de geoserver es el màxim que tarda la petició", true),
	LOADER_FILTER_FIELD("filter.field", "Param que indica amb quin camp correspon a data de actualització del objecte, si no es indica es fa carregua completa", false),
	LOADER_FILTER_FORMAT("filter.format", "Indica format de data d'actualització", false);

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
