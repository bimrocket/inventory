package cat.santfeliu.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import cat.santfeliu.api.dto.ConnectorComponentDTO;
import cat.santfeliu.api.dto.ConnectorDTO;
import cat.santfeliu.api.exceptions.ApiErrorException;
import cat.santfeliu.api.model.ConnectorComponentDb;
import cat.santfeliu.api.model.ConnectorDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.repo.ConnectorComponentConfigRepo;
import cat.santfeliu.api.repo.ConnectorComponentRepo;
import cat.santfeliu.api.repo.ConnectorRepo;
import cat.santfeliu.api.repo.ConnectorStatusRepo;

@Service
public class ConnectorApiService {

	private static final Logger log = LoggerFactory.getLogger(ConnectorApiService.class);
	
	@Autowired
	private ConnectorRepo conRepo;
	
	@Autowired
	private ConnectorStatusRepo conStatusRepo;
	
	@Autowired
	private ConnectorComponentConfigRepo configConRepo;
	
	@Autowired
	private ConnectorComponentRepo conComponentRepo;
	
	@Autowired
	private MapperService mapper;
	
	
	public List<ConnectorDTO> getAllConnectors() {
		List<ConnectorDTO> toReturn = new ArrayList<ConnectorDTO>();

		log.debug("getAllConnectors@ConnectorAllConnectors - find all connectors");
		Iterable<ConnectorDb> set = conRepo.findAll();
		for (ConnectorDb con : set) {
			toReturn.add(mapper.connectorDbToDTO(con));
		}
		return toReturn;
	}

	@Transactional
	public ConnectorDTO createConnector(ConnectorDTO connector) {
		Optional<ConnectorDb> exists = conRepo.findById(connector.getConnectorName());
		if (exists.isPresent()) {
			log.error("createConnector@ConnectorManagmentService - trying to create connector with name '{}' it already exists.", connector.getConnectorName());
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					String.format("trying to create connector with name '%s' it already exists.", connector.getConnectorName()), "error creating");
		}
		ConnectorDb db = mapper.connectorDTOToDb(connector);
		conRepo.save(db);
		configConRepo.saveAll(mapper.connectorConfigSetDTOToDb(db.getConnectorName(), connector.getConnectorParams()));
		exists = conRepo.findById(connector.getConnectorName());
		if (exists.isEmpty()) {
			log.error("createConnector@ConnectorManagmentService - error creating connector {} can't find it after creation", connector.getConnectorName());
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					String.format("cant find connector %s", connector.getConnectorName()), "error creating");
		}
		ConnectorStatusDb status = new ConnectorStatusDb();
		status.setConnectorName(db.getConnectorName());
		status.setConnectorStatus("offline");
		conStatusRepo.save(status);
		log.debug("createConnector@ConnectorApiService - create new connector {}", connector.getConnectorName());
		return mapper.connectorDbToDTO(exists.get());
		
	}
	
	@Transactional
	public ConnectorDTO updateConnector(ConnectorDTO connector) {
		Optional<ConnectorDb> exists = conRepo.findById(connector.getConnectorName());
		if (exists.isEmpty()) {
			log.error("createConnector@ConnectorManagmentService - trying to update connector with name '{}' but it doesn't exist.", connector.getConnectorName());
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					String.format("trying to update connector with name '{}' but it doesn't exist.", connector.getConnectorName()), "error updating");
		}
		ConnectorDb db = mapper.connectorDTOToDb(connector);
		conRepo.save(db);
		configConRepo.deleteAll(configConRepo.findAllByProducer(connector.getConnectorName()));
		configConRepo.saveAll(mapper.connectorConfigSetDTOToDb(db.getConnectorName(), connector.getConnectorParams()));
		exists = conRepo.findById(connector.getConnectorName());
		if (exists.isEmpty()) {
			log.error("createConnector@ConnectorManagmentService - error creating connector {} can't find it after update", connector.getConnectorName());
			throw new ApiErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					String.format("cant find connector %s", connector.getConnectorName()), "error creating");
		}
		log.debug("updateConnector@ConnectorApiService - update connector {}",connector.getConnectorName());
		return mapper.connectorDbToDTO(exists.get());
	}

	public ConnectorDTO getConnector(@NotNull @Valid String connectorName) {
		Optional<ConnectorDb> exists = conRepo.findById(connectorName);
		if (exists.isEmpty()) {
			return null;
		}
		log.debug("getConnector@ConnectorApiService - get existing connector {}",connectorName);
		return mapper.connectorDbToDTO(exists.get());
	}

	public List<ConnectorComponentDTO> getAllComponents() {
		List<ConnectorComponentDTO> toReturn = new ArrayList<>();
		List<ConnectorComponentDb> compsDb = conComponentRepo.findAllComponents();
		for (ConnectorComponentDb compDb : compsDb) {
			ConnectorComponentDTO dto = mapper.componentDbToDTO(compDb);
			toReturn.add(dto);
		}
		log.debug("getAllComponents@ConnectorApiServcie - find all components");
		return toReturn;
	}

	

}
