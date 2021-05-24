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
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import cat.santfeliu.api.components.ConnectorInstance;
import cat.santfeliu.api.components.ConnectorLoader;
import cat.santfeliu.api.components.ConnectorSender;
import cat.santfeliu.api.components.ConnectorTransformer;
import cat.santfeliu.api.dto.ConnectorStatusDTO;
import cat.santfeliu.api.enumerator.ComponentTypeEnum;
import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.ConnectorComponentDb;
import cat.santfeliu.api.model.ConnectorDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.repo.ConnectorComponentRepo;
import cat.santfeliu.api.repo.ConnectorRepo;
import cat.santfeliu.api.repo.ConnectorStatusRepo;
import cat.santfeliu.api.utils.ConfigContainer;

@Service
public class ConnectorManagerService {

	private static final Logger log = LoggerFactory.getLogger(ConnectorManagerService.class);

	@Autowired
	private ConnectorRepo conRepo;

	@Autowired
	private ConnectorStatusRepo conStatusRepo;

	@Autowired
	private ConnectorComponentRepo conCompRepo;
	
	@Autowired
	private ConnectorService conService;

	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;

	@Autowired
	private MapperService mapper;

	public ConnectorStatusDTO startConnector(@NotNull @Valid String connectorName) {
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
		status = conStatusRepo.save(status);
		conService.runConnector(setupConnector(conDb.get()));
		log.info("returning status");
		return mapper.statusDbToDTO(status);
	}

	public ConnectorStatusDTO stopConnector(@NotNull @Valid String connectorName) {
		// TODO Auto-generated method stub
		return null;
	}

	public ConnectorStatusDTO connectorStatus(@NotNull @Valid String connectorName) {
		// TODO Auto-generated method stub
		return null;
	}

	private ConnectorInstance setupConnector(ConnectorDb connector) {
		// --------------------------- Setting up classes
		Optional<ConnectorComponentDb> loaderComponentDb = conCompRepo.findById(connector.getConnectorLoader());
		if (loaderComponentDb.isEmpty()) {
			String error = String.format("connector component loader '%s' not found", connector.getConnectorLoader());
			log.error("setupConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error,
					"CONNECTOR COMPONENT LOADER NOT FOUND");

		}
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
		Optional<ConnectorComponentDb> transformerComponentDb = conCompRepo
				.findById(connector.getConnectorTransformer());
		if (transformerComponentDb.isEmpty()) {
			String error = String.format("connector component transformer '%s' not found",
					connector.getConnectorLoader());
			log.error("setupConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error,
					"CONNECTOR COMPONENT TRANSFORMER NOT FOUND");

		}
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
		Optional<ConnectorComponentDb> senderCompDb = conCompRepo.findById(connector.getConnectorSender());
		if (senderCompDb.isEmpty()) {
			String error = String.format("connector component sender '%s' not found", connector.getConnectorLoader());
			log.error("runConnector@ConnectorManagmentService - {}", error);
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR, error,
					"CONNECTOR COMPONENT SENDER NOT FOUND");

		}
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
		loader.init(getParamsForComponent(connector, ComponentTypeEnum.LOADER));
		transformer.init(getParamsForComponent(connector, ComponentTypeEnum.TRANSFORMER));
		sender.init(getParamsForComponent(connector, ComponentTypeEnum.SENDER));
		return new ConnectorInstance(connector, loader, transformer, sender);
	}

	private ConfigContainer getParamsForComponent(ConnectorDb connector, ComponentTypeEnum componentType) {
		ConfigContainer params = new ConfigContainer();
		autowireCapableBeanFactory.autowireBean(params);
		params.init(connector.getConnectorName(), componentType.getName());
		return params;
	}

}
