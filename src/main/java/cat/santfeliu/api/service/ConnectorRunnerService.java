package cat.santfeliu.api.service;

import java.util.HashMap;
import java.util.Map;

import org.python.icu.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorInstance;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.enumerator.ComponentTypeEnum;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.model.GlobalIdDb;
import cat.santfeliu.api.repo.ConnectorStatsRepo;
import cat.santfeliu.api.repo.ConnectorStatusRepo;
import cat.santfeliu.api.repo.GlobalIdRepo;

@Service
public class ConnectorRunnerService {
	
	public static final Map<String, ConnectorInstance> instances;
	private static final Logger log = LoggerFactory.getLogger(ConnectorRunnerService.class);
	static 
	{
		instances = new HashMap<>();
	}
	
	@Autowired
	private ConnectorStatsRepo connectorStatsRepo;
	
	@Autowired
	private ConnectorStatusRepo connectorStatusRepo;
	
	@Autowired
	private GlobalIdRepo globalIdRepo;
	
	
	@Async
	public void runConnector(ConnectorInstance instance) {
		boolean hasException = false;
		int timeout = 60000;
		log.info("runConnector@ConnectorRunnerService - running async start of connector instance {}", instance.getConnector().getConnectorName());
		instances.put(instance.getConnector().getConnectorName(), instance);
		instance.getConnectorStats().setExecutionFinalState("start of async run");
		ConnectorLoader loader = instance.getConnectorLoader();
		ConnectorTransformer transformer = instance.getConnectorTransformer();
		ConnectorSender sender = instance.getConnectorSender();
		loader.initLoader(instance);		
		JsonObject curObject = null;
		try {
			curObject = loader.load(timeout);
			boolean loaderHasEnd = loader.hasEnd();
			// ---------------------- Initializing classes
			while (!instance.shouldEnd()) {
				if (curObject == null && loaderHasEnd) {
					instance.stop();
				} else if (curObject == null && !loaderHasEnd) {
					Thread.sleep(100);
					curObject = loader.load(timeout);
				}
				if (curObject != null) {
					this.updateStats(instance, ComponentTypeEnum.LOADER);
					JsonObject transformed = null;
					try {
						transformed = transformer.transform(curObject);
						log.info("beforeTransform :: {}", curObject.toString());
					} catch (Exception e) {
						String error = String.format(
								"exception while transforming object of connector %s with message '%s' check logs for more details", instance.getConnector().getConnectorName(), e.getMessage());
						log.error("runConnector@ConnectorRunnerService - {}", error, e);
						log.error("runConnector@ConnectorRunnerService - object :: {}", curObject.toString());
					}
					if (transformed != null) {
						
						this.updateStats(instance, ComponentTypeEnum.TRANSFORMER);
						try {
							sender.send(transformed);
							log.info("sent :: {}", transformed.toString());
							this.updateStats(instance, ComponentTypeEnum.SENDER);
						} catch (Exception e) {
							String error = String.format(
									"exception while sending object of connector %s with message '%s' check logs for more details", instance.getConnector().getConnectorName(), e.getMessage());
							log.error("runConnector@ConnectorRunnerService - {}", error, e);
							log.error("runConnector@ConnectorRunnerService - object :: {}", curObject.toString());
						
						}
					
					}
					curObject = loader.load(timeout);
				}
			}

		} catch (Exception e) {
			String error = String.format(
					"exception while running connector with message '%s' check logs for more details", e.getMessage());
			log.error("runConnector@ConnectorRunnerService - {}", error, e);
			hasException = true;
		} finally {	
			if (curObject == null && loader.hasEnd()) {
				String info = String.format("runConnector@ConnectorRunnerService - processed all objects stoping connector %s", instance.getConnector().getConnectorName());
				log.info(info);
				endRun(instance, true, false, false);
			} else if (instance.shouldEnd()) {
				String info = String.format("runConnector@ConnectorRunnerService - gracefully (with api/stop) stoping connector %s", instance.getConnector().getConnectorName());
				log.info(info);
				endRun(instance, false, false, true);
			} else if (hasException) {
				endRun(instance, false, true, false);
			} else {
				endRun(instance, false, false, false);
			}
		}
		
		log.info("runConnector@ConnectorRunnerService - end async of connector instance {}", instance.getConnector().getConnectorName());
		
	}
	
	private void endRun(ConnectorInstance instance, boolean processedAllObjects, boolean hasGlobalException, boolean gracefullyStopped) {
		
		instances.remove(instance.getConnector().getConnectorName());
		ConnectorStatusDb status = instance.getConnectorStatus();
		status.setConnectorEndDate(Calendar.getInstance().getTime());
		status.setConnectorStatus("offline");
		connectorStatusRepo.save(status);
		ConnectorExecutionStatsDb stats = instance.getConnectorStats();
		stats.setExecutionEndDate(Calendar.getInstance().getTime());
		if (processedAllObjects) {
			stats.setExecutionFinalState("finalized correctly due to end of objects");
		} else if (hasGlobalException) {
			stats.setExecutionFinalState("stopped by global exception");
		} else if (gracefullyStopped) {
			stats.setExecutionFinalState("stopped by stop endpoint");
		} else {
			stats.setExecutionFinalState("finalized correctly");
		}
		connectorStatsRepo.save(stats);
		instance.destroy();
	}
	
	private void updateStats(ConnectorInstance instance, ComponentTypeEnum componentType) {
		ConnectorExecutionStatsDb stats = instance.getConnectorStats();
		if (componentType == null) {
			stats.addDeleted();
		} else if (componentType.getName().equals(ComponentTypeEnum.LOADER.getName())) {
			stats.addLoaded();
		} else if (componentType.getName().equals(ComponentTypeEnum.TRANSFORMER.getName())) {
			stats.addTransformed();
		} else if (componentType.getName().equals(ComponentTypeEnum.SENDER.getName())) {
			stats.addSent();
		}
	}
}
