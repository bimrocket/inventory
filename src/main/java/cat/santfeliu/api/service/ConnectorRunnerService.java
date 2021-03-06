package cat.santfeliu.api.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.python.icu.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.santfeliu.api.components.ConnectorInstance;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.enumerator.ComponentTypeEnum;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.repo.ConnectorStatsRepo;
import cat.santfeliu.api.repo.ConnectorStatusRepo;
import cat.santfeliu.api.repo.GlobalIdRepo;
import cat.santfeliu.api.utils.InventoryErrorSentinelUtils;

@Service
public class ConnectorRunnerService {

	public static final Map<String, ConnectorInstance> instances;
	private static final Logger log = LoggerFactory.getLogger(ConnectorRunnerService.class);
	ObjectMapper mapper = new ObjectMapper();
	static {
		instances = new HashMap<>();
	}

	@Autowired
	private ConnectorStatsRepo connectorStatsRepo;

	@Autowired
	private ConnectorStatusRepo connectorStatusRepo;

	@Autowired
	private InventoryErrorSentinelUtils sentinel;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private GlobalIdRepo globalIdRepo;
	
	@Autowired
	private MapperService mapperService;

	/**
	 * Main data flow loop It serves both to read kafka and to write The previously
	 * generated ConnectorInstance must be passed by ConnectorManagerService
	 * setupConnector method This method is asynchronous, a new thread is generated
	 * which is responsible for the execution of the method.
	 * 
	 * @param instance
	 */
	@Async
	public void runConnector(ConnectorInstance instance) {
		boolean hasException = false;
		log.info("runConnector@ConnectorRunnerService - running async start of connector instance {}",
				instance.getConnector().getConnectorName());
		instances.put(instance.getConnector().getConnectorName(), instance);
		instance.getConnectorStats().setExecutionFinalState("start of async run");
		ConnectorLoader loader = instance.getConnectorLoader();
		ConnectorTransformer transformer = instance.getConnectorTransformer();
		ConnectorSender sender = instance.getConnectorSender();
		loader.initLoader(instance);
		JsonNode curObject = null;
		try {
			curObject = loader.load(loader.getLoadTimeout());
			boolean loaderHasEnd = loader.hasEnd();
			// ---------------------- Initializing classes
			while (!instance.shouldEnd()) {
				if (curObject == null && loaderHasEnd) {
					log.debug("runConnector@ConnectorRunnerService - saving any errors from import");
					sentinel.saveAllAndFlush(instance.getConnector().getConnectorName());

					log.debug("runConnector@ConnectorRunnerService - stopping instance due to has end");
					instance.stop();
				} else if (curObject == null && !loaderHasEnd) {
					log.debug("runConnector@ConnectorRunnerService - saving any errors from import");
					sentinel.saveAllAndFlush(instance.getConnector().getConnectorName());

					log.debug(
							"runConnector@ConnectorRunnerService - connector {} put to sleep {}ms due to end of objects",
							instance.getConnector().getConnectorName(), loader.getSleepTime());

					int seconds = (int) (loader.getSleepTime() / 1000);
					int actualSeconds = 0;
					while (actualSeconds < seconds && !instance.shouldEnd()) {
						Thread.sleep(1000);
						actualSeconds++;
						log.debug(
								"runConnector@ConnectorRunnerService - connector {} will return from sleep in {}s",
								instance.getConnector().getConnectorName(), seconds - actualSeconds);
					}
					if (!instance.shouldEnd()) {
						log.debug(
								"runConnector@ConnectorRunnerService - connector {} ends sleep and will try now to retrive objects from loader",
								instance.getConnector().getConnectorName());
						curObject = loader.load(loader.getLoadTimeout());
					} else {
						log.debug(
								"runConnector@ConnectorRunnerService - connector {} stopped sleep and should end",
								instance.getConnector().getConnectorName());
					}
				}
				if (curObject != null) {
					log.debug("currentOnject :: {}", mapper.writeValueAsString(curObject));
					this.updateStats(instance, ComponentTypeEnum.LOADER);
					JsonNode transformed = null;
					try {
						transformed = transformer.transform(curObject);
						log.info("afterTransform :: {}", mapper.writeValueAsString(transformed));
					} catch (Exception e) {
						String error = String.format(
								"exception while transforming object of connector %s with message '%s' check logs for more details",
								instance.getConnector().getConnectorName(), e.getMessage());
						log.error("runConnector@ConnectorRunnerService - {}", error, e);
						log.error("runConnector@ConnectorRunnerService - object :: {}",
								new ObjectMapper().writeValueAsString(curObject));
					}
					if (transformed != null) {

						this.updateStats(instance, ComponentTypeEnum.TRANSFORMER);
						try {
							sender.send(transformed);
							log.debug("sent :: {}", new ObjectMapper().writeValueAsString(transformed));
							this.updateStats(instance, ComponentTypeEnum.SENDER);
						} catch (Exception e) {
							String error = String.format(
									"exception while sending object of connector %s with message '%s' check logs for more details",
									instance.getConnector().getConnectorName(), e.getMessage());
							log.error("runConnector@ConnectorRunnerService - {}", error, e);
							log.error("runConnector@ConnectorRunnerService - object :: {}",
									new ObjectMapper().writeValueAsString(curObject));

						}

					}
					curObject = loader.load(loader.getLoadTimeout());
				}
			}

		} catch (Exception e) {
			String error = String.format(
					"exception while running connector with message '%s' check logs for more details", e.getMessage());
			log.error("runConnector@ConnectorRunnerService - {}", error, e);
			hasException = true;
		} finally {
			if (curObject == null && loader.hasEnd()) {
				String info = String.format(
						"runConnector@ConnectorRunnerService - processed all objects stoping connector %s",
						instance.getConnector().getConnectorName());
				log.info(info);
				endRun(instance, true, false, false);
			} else if (instance.shouldEnd()) {
				String info = String.format(
						"runConnector@ConnectorRunnerService - gracefully (with api/stop) stoping connector %s",
						instance.getConnector().getConnectorName());
				log.info(info);
				endRun(instance, false, false, true);
			} else if (hasException) {
				endRun(instance, false, true, false);
			} else {
				endRun(instance, false, false, false);
			}
		}

		log.info("runConnector@ConnectorRunnerService - end async of connector instance {}",
				instance.getConnector().getConnectorName());

	}

	private void endRun(ConnectorInstance instance, boolean processedAllObjects, boolean hasGlobalException,
			boolean gracefullyStopped) {

		instances.remove(instance.getConnector().getConnectorName());
		ConnectorStatusDb status = instance.getConnectorStatus();
		status.setConnectorEndDate(Calendar.getInstance().getTime());
		status.setConnectorStatus("offline");
		connectorStatusRepo.save(status);
		simpMessagingTemplate.convertAndSend("/topic/status", mapperService.statusDbToDTO(status));
		log.debug("endRun@ConnectorRunnerService - saving any errors from import");
		sentinel.saveAllAndFlush(instance.getConnector().getConnectorName());
		ConnectorExecutionStatsDb stats = instance.getConnectorStats();
		stats.setExecutionEndDate(Calendar.getInstance().getTime());
		if (processedAllObjects) {
			stats.setExecutionFinalState("finalized correctly due to end of objects");
			log.debug(
					"endRun@ConnectorRunnerService - connector with name {} finalized correctly due to end of objects",
					instance.getConnector().getConnectorName());
		} else if (hasGlobalException) {
			stats.setExecutionFinalState("stopped by global exception");
			log.debug("endRun@ConnectorRunnerService - connector with name {} stopped by global exception",
					instance.getConnector().getConnectorName());
		} else if (gracefullyStopped) {
			stats.setExecutionFinalState("stopped by stop endpoint");
			log.debug("endRun@ConnectorRunnerService - connector with name {} stopped by stop endpoint",
					instance.getConnector().getConnectorName());
		} else {
			stats.setExecutionFinalState("finalized correctly");
			log.debug("endRun@ConnectorRunnerService - connector with name {} finalized correctly",
					instance.getConnector().getConnectorName());
		}
		connectorStatsRepo.save(stats);
		instance.destroy();
	}

	private void updateStats(ConnectorInstance instance, ComponentTypeEnum componentType) {
		ConnectorExecutionStatsDb stats = instance.getConnectorStats();
		if (componentType == null) {
			stats.addDeleted();
			log.debug("updateStats@ConnectorRunnerService - connector with name {} stats deleted",
					instance.getConnector().getConnectorName());
		} else if (componentType.getName().equals(ComponentTypeEnum.LOADER.getName())) {
			stats.addLoaded();
			log.debug("updateStats@ConnectorRunnerService - connector with name {} stats {} loaded",
					instance.getConnector().getConnectorName(), stats.toString());
		} else if (componentType.getName().equals(ComponentTypeEnum.TRANSFORMER.getName())) {
			stats.addTransformed();
			log.debug("updateStats@ConnectorRunnerService - connector with name {} stats {} transformed",
					instance.getConnector().getConnectorName(), stats.toString());
		} else if (componentType.getName().equals(ComponentTypeEnum.SENDER.getName())) {
			stats.addSent();
			log.debug("updateStats@ConnectorRunnerService - connector with name {} stats {} sent",
					instance.getConnector().getConnectorName(), stats.toString());
		}
	}
}
