package cat.santfeliu.api.loaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cat.santfeliu.api.beans.LoaderResponse;
import cat.santfeliu.api.utils.ConfigContainer;

public abstract class Loader {

	protected ConfigContainer params;

	public abstract LoaderResponse read();

	/**
	 * Init de loader loads database params 
	 * following key like :producerKey.loader.*
	 * @param producerKey
	 */
	public void initLoader(ConfigContainer params) {
		this.params = params;
	}
	
}
