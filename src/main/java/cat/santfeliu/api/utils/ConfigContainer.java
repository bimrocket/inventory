package cat.santfeliu.api.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.ConnectorComponentConfigDb;
import cat.santfeliu.api.repo.ConnectorComponentConfigRepo;

public class ConfigContainer {

	private static final Logger log = LoggerFactory.getLogger(ConfigContainer.class);
	
	private String connectorName = "";
	private String connectorComponentType = "";
	private Map<String, String> params = new HashMap<String, String>();
	
	@Autowired
	private ConnectorComponentConfigRepo configRepo;
	
	public void init(String producer, String type) {
		this.params = new HashMap<String, String>();
		List<ConnectorComponentConfigDb> configs = configRepo.findAllByProducerAndType(producer, type);
		for (ConnectorComponentConfigDb config : configs) {
			this.params.put(config.getConfigKey(), config.getConfigValue());
		}
		this.connectorName = producer;
		this.connectorComponentType = type;
	}
	
	public String getParamValue(String key) {
		return this.getParamValue(key, true);
	}
	
	/**
	 * Returns param by key
	 * If strict throws ApiErrorException if param not found
	 * @param key
	 * @return
	 */
	public String getParamValue(String key, boolean strict) {
		if (!strict) {
			return params.get(key);
		} else {
			String param = params.get(key);
			if (param == null) {
				throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
						String.format("no param named '%s' found in strict mode for connector '%s' with type '%s'", key, this.connectorName, this.connectorComponentType), "error param not found");
			}
			return param;
		}
	}
	
	/**
	 * Returns list of params following this syntax
	 * the key needs to contain * which translates to 01,02,03
	 * Example if we load key ContainerProducer.loader.layers.*
	 * It will return list of all layers defined with key : ContainerProducer.loader.layers.01, ContainerProducer.loader.layers.02, ContainerProducer.loader.layers.xx
	 * @param key
	 * @return
	 */
	public List<String> getMultipleParamValues(String key) {
		if (!key.contains("*")) {
			String error = String.format("key '%s' doesn't contain * which means param number like 01, it's obligatory for loading list of params", key);
			log.error("getMultipleParams@ConfigContainer - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, error);
		}
		List<String> paramsToReturn = new ArrayList<>();
		String orgKey = key.toString();
		boolean found = true;
		int loopCount = 1;
		while (found) {
			StringBuilder strBuilder = new StringBuilder();
			String stringCount = "";
			if (loopCount < 10) {
				stringCount = strBuilder.append("0").append(loopCount).toString();
			} else {
				stringCount = strBuilder.append(loopCount).toString();
			}
			String loopKey = orgKey.replace("*", stringCount);
			String param = this.getParamValue(loopKey, false);
			found = param != null;
			if (found) {
				paramsToReturn.add(param);
			}
			loopCount++;
		}
		return paramsToReturn;
	}

}
