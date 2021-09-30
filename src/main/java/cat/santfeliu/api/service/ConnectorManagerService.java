package cat.santfeliu.api.service;

import java.lang.reflect.Constructor;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.python.icu.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import cat.santfeliu.api.components.ConnectorInstance;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.dto.ConnectorStatusDTO;
import cat.santfeliu.api.dto.PageErrorsDTO;
import cat.santfeliu.api.dto.PageStatsDTO;
import cat.santfeliu.api.enumerator.ComponentTypeEnum;
import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.ConnectorComponentDb;
import cat.santfeliu.api.model.ConnectorDb;
import cat.santfeliu.api.model.ConnectorExecutionErrorsDb;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.repo.ConnectorComponentRepo;
import cat.santfeliu.api.repo.ConnectorErrorsRepoPaged;
import cat.santfeliu.api.repo.ConnectorRepo;
import cat.santfeliu.api.repo.ConnectorStatsRepo;
import cat.santfeliu.api.repo.ConnectorStatusRepo;
import cat.santfeliu.api.repo.GlobalIdRepo;
import cat.santfeliu.api.utils.ConfigContainer;
import cat.santfeliu.api.utils.InventoryErrorSentinelUtils;

@Service
public class ConnectorManagerService {

	private static final Logger log = LoggerFactory.getLogger(ConnectorManagerService.class);

	@Autowired
	private ConnectorRepo conRepo;

	@Autowired
	private ConnectorStatusRepo conStatusRepo;

	@Autowired
	private ConnectorStatsRepo conStatsRepo;
	
	@Autowired
	private ConnectorErrorsRepoPaged errorsRepo;

	@Autowired
	private ConnectorComponentRepo conCompRepo;

	@Autowired
	private ConnectorRunnerService conService;

	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;

	@Autowired
	private MapperService mapper;

	@Autowired
	private GlobalIdRepo globalIdRepo;

	@Autowired
	private InventoryErrorSentinelUtils sentinel;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	public ConnectorStatusDTO startConnector(@NotNull @Valid String connectorName) {
		// Make checks in db
		Optional<ConnectorDb> conDb = conRepo.findById(connectorName);
		if (conDb.isEmpty()) {
			String error = String.format("trying to start non existing connector '%s'", connectorName);
			log.error("startConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, "CONNECTOR NOT FOUND");
		}
		Optional<ConnectorStatusDb> conStatusDb = conStatusRepo.findById(connectorName);
		if (conStatusDb.isEmpty()) {
			String error = String.format("couldn't find status of connector '%s'", connectorName);
			log.error("startConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, "CONNECTOR STATUS NOT FOUND");
		}
		ConnectorStatusDb status = conStatusDb.get();
		if (status.getConnectorStatus().equals("online")) {
			String error = String.format("connector '%s' is already running", connectorName);
			log.error("startConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, "CONNECTOR IS RUNNING");
		}
		status.setConnectorStartDate(Calendar.getInstance().getTime());
		status.setConnectorEndDate(null);
		status.setConnectorStatus("online");
		simpMessagingTemplate.convertAndSend("/topic/status", mapper.statusDbToDTO(status));
		status = conStatusRepo.save(status);
		ConnectorExecutionStatsDb stats = new ConnectorExecutionStatsDb();
		stats.setExecutionConnectorName(conDb.get().getConnectorName());
		stats.setExecutionStartDate(Calendar.getInstance().getTime());
		stats.setExecutionObjectsLoaded(0);
		stats.setExecutionObjectsSent(0);
		stats.setExecutionObjectsTransformed(0);
		stats.setExecutionObjectsDeleted(0);
		stats.setExecutionFinalState("initialized and sent to connection runner service");
		stats = conStatsRepo.save(stats);
		ConnectorInstance instance = null;
		try {
			instance = setupConnector(conDb.get(), stats);
			instance.setConnectorStatus(status);
			instance.setConnectorStats(stats);
		} catch (Exception e) {
			this.sentinel.senError(conDb.get().getConnectorName(), stats.getExecutionId(), "INIT_FAILED").describe("Init of connector failed more info in exception").foundErr().exception(e);
			log.error("startConnector@ConnectorManagerService - {} - Probably missconfigured connector, can't initialize components", conDb.get().getConnectorName());
			status.setConnectorEndDate(Calendar.getInstance().getTime());
			status.setConnectorStatus("offline");
			conStatusRepo.save(status);
			log.debug("startConnector@ConnectorManagerService - saving any errors from import");
			sentinel.saveAllAndFlush(conDb.get().getConnectorName());
			stats.setExecutionEndDate(Calendar.getInstance().getTime());
			stats.setExecutionFinalState("exited while initializing");
			conStatsRepo.save(stats);
			throw e;
		}

		conService.runConnector(instance);
		log.debug("startConnector@ConnectorManagerService -  initialization of connector {} correct", connectorName);
		return mapper.statusDbToDTO(status);
	}

	public ConnectorStatusDTO stopConnector(@NotNull @Valid String connectorName) {
		Optional<ConnectorStatusDb> conStatusDb = conStatusRepo.findById(connectorName);
		if (conStatusDb.isEmpty()) {
			String error = String.format("couldn't find status of connector '%s'", connectorName);
			log.error("startConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, "CONNECTOR STATUS NOT FOUND");
		}
		ConnectorStatusDb status = conStatusDb.get();
		ConnectorInstance instance = ConnectorRunnerService.instances.get(connectorName);
		if (instance != null) {
			instance.setEnd(true);
		}
		status.setConnectorEndDate(Calendar.getInstance().getTime());
		status.setConnectorStatus("offline");
		status = conStatusRepo.save(status);
		log.debug("stopConnector@ConnectorManagerService - stop connector {}", connectorName);
		return mapper.statusDbToDTO(status);
	}

	public ConnectorStatusDTO connectorStatus(@NotNull @Valid String connectorName) {
		Optional<ConnectorStatusDb> conStatusDb = conStatusRepo.findById(connectorName);
		if (conStatusDb.isEmpty()) {
			String error = String.format("couldn't find status of connector '%s'", connectorName);
			log.error("startConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error, "CONNECTOR STATUS NOT FOUND");
		}
		ConnectorStatusDb status = conStatusDb.get();
		log.debug("connectorStatusDTO@ConnectorManagerService - get status of connector {}", connectorName);
		return mapper.statusDbToDTO(status);
	}

	/***
	 * Main method that starts components (loader, transformer, sender) with its
	 * corresponding class defined in database model equivalent to the Connector,
	 * Aside from starting components you also get your configs (ConfigContainer)
	 * that they are loaded from db and saved on a Map. If the ComponentLoader,
	 * etc..They contain an @Autowired object as well here the Spring context Bean
	 * is assigned
	 * 
	 * @param connector Database model
	 * @return Instance connector ready to run with runConnector from
	 *         {ConnectorRunnerService}
	 */
	private ConnectorInstance setupConnector(ConnectorDb connector, ConnectorExecutionStatsDb stats) {
		// --------------------------- Setting up classes
		// Checking if loader component defined in connector exists in database and
		// recovers class package path
		Optional<ConnectorComponentDb> loaderComponentDb = conCompRepo.findById(connector.getConnectorLoader());
		if (loaderComponentDb.isEmpty()) {
			String error = String.format("connector component loader '%s' not found", connector.getConnectorLoader());
			log.error("setupConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error,
					"CONNECTOR COMPONENT LOADER NOT FOUND");

		}
		log.debug("setupConnector@ConnectorManagerService - check if loader component of connector {} exists",
				connector.getConnectorName());
		// Creating new instance of loader (equals to new XXXLoader())
		String loaderClassName = loaderComponentDb.get().getConnectorComponentClass();
		ConnectorLoader loader = null;
		try {
			Object[] params = {};
			Class[] paramsCon = {};
			var loaderClass = Class.forName(loaderClassName);
			Constructor con = loaderClass.getConstructor(paramsCon);

			loader = (ConnectorLoader) con.newInstance(params);
		} catch (Exception e) {
			String error = String.format("couldn't create loader class %s for connector %s", loaderClassName,
					connector.getConnectorName());
			log.error("setupConnector@ConnectorManagmentService - {}", error, e);
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, error, error);

		}
		log.debug("setupConnector@ConnectorManagerService - create new instance of loader component of connector {}",
				connector.getConnectorName());
		// Checking if transformer component exists in database and recovers class
		// package path
		Optional<ConnectorComponentDb> transformerComponentDb = conCompRepo
				.findById(connector.getConnectorTransformer());
		if (transformerComponentDb.isEmpty()) {
			String error = String.format("connector component transformer '%s' not found",
					connector.getConnectorLoader());
			log.error("setupConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error,
					"CONNECTOR COMPONENT TRANSFORMER NOT FOUND");

		}
		log.debug("setupConnector@ConnectorManagerService - check if transformer component of connector {} exists",
				connector.getConnectorName());
		// Creating new instance of transformer (equals to new XXXTransfomer())
		String transformerClassName = transformerComponentDb.get().getConnectorComponentClass();
		ConnectorTransformer transformer = null;
		try {
			Object[] params = {};
			Class[] paramsCon = {};
			var transformerClass = Class.forName(transformerClassName);
			Constructor con = transformerClass.getConstructor(paramsCon);

			transformer = (ConnectorTransformer) con.newInstance(params);
		} catch (Exception e) {
			String error = String.format("couldn't create transformer class %s for connector %s", transformerClassName,
					connector.getConnectorName());
			log.error("runConnector@ConnectorManagmentService - {}", error, e);
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, error, error);

		}
		log.debug(
				"setupConnector@ConnectorManagerService - create new instance of transformer component of connector {}",
				connector.getConnectorName());
		// Checking if sender component exists in database and recovers class package
		// path
		Optional<ConnectorComponentDb> senderCompDb = conCompRepo.findById(connector.getConnectorSender());
		if (senderCompDb.isEmpty()) {
			String error = String.format("connector component sender '%s' not found", connector.getConnectorLoader());
			log.error("runConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error,
					"CONNECTOR COMPONENT SENDER NOT FOUND");

		}
		log.debug("setupConnector@ConnectorManagerService - check if sender component of connector {} exists",
				connector.getConnectorName());
		// Creating new instance of sender (equals to new XXXSender())
		String senderClassName = senderCompDb.get().getConnectorComponentClass();
		ConnectorSender sender = null;
		try {
			Object[] params = {};
			Class[] paramsCon = {};
			var senderClass = Class.forName(senderClassName);
			Constructor con = senderClass.getConstructor(paramsCon);

			sender = (ConnectorSender) con.newInstance(params);
		} catch (Exception e) {
			String error = String.format("couldn't create sender class %s for connector %s", senderClassName,
					connector.getConnectorName());
			log.error("runConnector@ConnectorManagmentService - {}", error, e);
			throw new ApiErrorException(HttpStatus.BAD_REQUEST, error, error);

		}
		// Invoked init of component passing inventoryName and ConfigContainer
		loader.init(connector.getInventoryName(), connector.getConnectorName(), stats.getExecutionId(),
				getParamsForComponent(connector, ComponentTypeEnum.LOADER));
		transformer.init(connector.getInventoryName(), connector.getConnectorName(), stats.getExecutionId(),
				getParamsForComponent(connector, ComponentTypeEnum.TRANSFORMER));
		sender.init(connector.getInventoryName(), connector.getConnectorName(), stats.getExecutionId(),
				getParamsForComponent(connector, ComponentTypeEnum.SENDER));
		log.debug("setupConnector@ConnectorManagerService - initialization of components of connector {}",
				connector.getConnectorName());

		// Autowires @Autowired objects of components
		autowireCapableBeanFactory.autowireBean(loader);
		autowireCapableBeanFactory.autowireBean(transformer);
		autowireCapableBeanFactory.autowireBean(sender);
		log.debug("setupConnector@ConnectorManagerService - autowire object of components of connector {}",
				connector.getConnectorName());
		// Returns instance which contains connector,loader, transformer and sender
		// ready to run

		log.debug("setupConnector@ConnectorManagerService - create new instance of sender component of connector {}",
				connector.getConnectorName());

		return new ConnectorInstance(connector, loader, transformer, sender);
	}

	/**
	 * With connector database model and component type indicated by enum
	 * ComponentTypeEnum (loader, transformer, sender) returns the ConfigContainer.
	 * 
	 * @param connector     Connector model
	 * @param componentType Enum indicated what type of component it is
	 * @return ConfigContainer contains all database params in a Map and methods
	 *         like getParamValue.
	 */
	private ConfigContainer getParamsForComponent(ConnectorDb connector, ComponentTypeEnum componentType) {
		ConfigContainer params = new ConfigContainer();
		autowireCapableBeanFactory.autowireBean(params);
		params.init(connector.getConnectorName(), componentType.getName());
		log.debug("getParamsForComponent@ConnectorManagerService - get params of connector {} and component type {}",
				connector.getConnectorName(), componentType.getName());
		return params;
	}

	public PageStatsDTO connectorStats(@NotNull @Valid String connectorName, int page, int size) {
		if (size > 100) {
			size = 100;
		}
		Page<ConnectorExecutionStatsDb> statsDb = conStatsRepo
				.findByExecutionConnectorNameOrderByExecutionStartDateDesc(connectorName, PageRequest.of(page, size));
		PageStatsDTO pageDto = new PageStatsDTO();
		pageDto = mapper.connectorStatsDbToDTO(statsDb);
		log.debug("connectorStats@ConnectorManagerService - get stats of connector {} of page {} with size {}",
				connectorName, page, size);
		return pageDto;

	}

	public PageErrorsDTO connectorErrors(@NotNull @Valid String connectorName, int page, int size) {
		if (size > 100) {
			size = 100;
		}
		Page<ConnectorExecutionErrorsDb> errorsDb = errorsRepo.findByExecutionConnectorNameOrderByExecutionIdDesc(connectorName, PageRequest.of(page, size));
		PageErrorsDTO pageDto = new PageErrorsDTO();
		pageDto = mapper.connectorErrorsDbToDTO(errorsDb);
		log.debug("connectorStats@ConnectorManagerService - get stats of connector {} of page {} with size {}",
				connectorName, page, size);
		return pageDto;
	}

}
