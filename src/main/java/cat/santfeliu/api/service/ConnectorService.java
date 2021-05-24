package cat.santfeliu.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorInstance;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.components.ConnectorTransformer;

@Service
public class ConnectorService {
	
	private static final Logger log = LoggerFactory.getLogger(ConnectorService.class);

	@Async
	public void runConnector(ConnectorInstance instance) {
		log.info("runConnector@ConnectorManagerService - running async connector instance {}", instance.getConnector().getConnectorName());
		ConnectorLoader loader = instance.getConnectorLoader();
		ConnectorTransformer transformer = instance.getConnectorTransformer();
		ConnectorSender sender = instance.getConnectorSender();
		try {
			// ---------------------- Initializing classes
			JsonObject curObject = loader.load(60000);
			while (curObject != null) {
				sender.send(transformer.transform(curObject));
				curObject = loader.load(60000);
			}
		} catch (Exception e) {
			String error = String.format(
					"exception while running connector with message '%s' check logs for more details", e.getMessage());
			log.error("runConnector@ConnectorManagerService - {}", error, e);
		} finally {
			instance.destroy();
		}
	}
}
