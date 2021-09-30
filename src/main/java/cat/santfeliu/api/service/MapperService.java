package cat.santfeliu.api.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import cat.santfeliu.api.components.ConnectorComponent;
import cat.santfeliu.api.dto.ComponentConfigKeyDTO;
import cat.santfeliu.api.dto.ConnectorComponentDTO;
import cat.santfeliu.api.dto.ConnectorDTO;
import cat.santfeliu.api.dto.ConnectorErrorsDTO;
import cat.santfeliu.api.dto.ConnectorParamDTO;
import cat.santfeliu.api.dto.ConnectorStatsDTO;
import cat.santfeliu.api.dto.ConnectorStatusDTO;
import cat.santfeliu.api.dto.PageErrorsDTO;
import cat.santfeliu.api.dto.PageStatsDTO;
import cat.santfeliu.api.model.ConnectorComponentConfigDb;
import cat.santfeliu.api.model.ConnectorComponentDb;
import cat.santfeliu.api.model.ConnectorDb;
import cat.santfeliu.api.model.ConnectorExecutionErrorsDb;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.repo.ConnectorComponentConfigRepo;
import cat.santfeliu.api.repo.ConnectorStatusRepo;
import cat.santfeliu.api.utils.ConfigProperty;

@Service
public class MapperService {

	private static final Logger log = LoggerFactory.getLogger(MapperService.class);
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ConnectorComponentConfigRepo conConfigRepo;
	
	@Autowired
	private ConnectorStatusRepo conStatusRepo;

	/**
	 * Return dto of ConnectorStatusDb
	 * 
	 * @param statusDb
	 * @return dto ConnectorStatusDTO
	 */
	public ConnectorStatusDTO statusDbToDTO(ConnectorStatusDb statusDb) {
		ConnectorStatusDTO dto = new ConnectorStatusDTO();
		log.debug("statusDbToDTO@MapperService - get dto of statutsDb {}", statusDb.getConnectorName());
		return modelMapper.map(statusDb, ConnectorStatusDTO.class);

	}

	/**
	 * Returns connector component dto ConnectorComponentDb
	 * 
	 * @param db
	 * @return dto
	 */
	public ConnectorComponentDTO componentDbToDTO(ConnectorComponentDb db) {
		ConnectorComponentDTO dto = modelMapper.map(db, ConnectorComponentDTO.class);
		String className = db.getConnectorComponentClass();
		ConnectorComponent component = null;
		try {
			Object[] params = {};
			Class[] paramsCon = {};
			var loaderClass = Class.forName(className);
			Constructor con = loaderClass.getConstructor(paramsCon);

			component = (ConnectorComponent) con.newInstance(params);
		} catch (Exception e) {
			String error = String.format("couldn't create component class %s", className);
			log.error("componentDbToDTO@MapperService - {}", error);
			return null;

		}
		Class cls = component.getClass();
		Field[] fields = Stream
				.concat(Arrays.stream(cls.getDeclaredFields()), Arrays.stream(cls.getSuperclass().getDeclaredFields()))
				.toArray(Field[]::new);
		for (Field field : fields) {
			ConfigProperty property = field.getAnnotation(ConfigProperty.class);
			if (property != null) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(property.name());
				configDto.setRequired(property.required());
				configDto.setDescription(property.description());
				configDto.setHidden(property.hidden());
				dto.getComponentConfigKeys().add(configDto);
			}
		}
		return dto;
	}

	/**
	 * Returns connector dto from ConnectorDb
	 * 
	 * @param con
	 * @return dto
	 */
	public ConnectorDTO connectorDbToDTO(ConnectorDb con) {
		ConnectorDTO dto = modelMapper.map(con, ConnectorDTO.class);
		List<ConnectorComponentConfigDb> paramsDb = conConfigRepo.findAllByProducer(con.getConnectorName());
		List<ConnectorParamDTO> params = new ArrayList<ConnectorParamDTO>();
		for (ConnectorComponentConfigDb paramDb : paramsDb) {
			params.add(modelMapper.map(paramDb, ConnectorParamDTO.class));
		}
		Optional<ConnectorStatusDb> conStatusDb = conStatusRepo.findById(con.getConnectorName());
		if (conStatusDb.isEmpty()) {
			String error = String.format("couldn't find status of connector '%s'", con.getConnectorName());
			log.error("connectorDbToDTO@MapperService - {}", error);
		} else {
			ConnectorStatusDb status = conStatusDb.get();
			log.debug("connectorDbToDTO@MapperService - get status of connector {}", con.getConnectorName());
			dto.setConnectorStatus(statusDbToDTO(status));			
		}

		dto.setConnectorParams(params);
		log.debug("connectorDbToDTO@MapperService - connector db {} to dto", con.getConnectorName());
		return dto;
	}

	/**
	 * ConnectorDTO to ConnectorDb database model
	 * 
	 * @param dto
	 * @return db
	 */
	public ConnectorDb connectorDTOToDb(ConnectorDTO dto) {
		ConnectorDb db = modelMapper.map(dto, ConnectorDb.class);
		;
		log.debug("connectorDTOToDb@MapperService - connector dto {} to db", db.getConnectorName());
		return db;
	}

	/**
	 * Configs de connector com dto a Set de base de dades
	 * 
	 * @param connectorName
	 * @param dto
	 * @return set db
	 */
	public Set<ConnectorComponentConfigDb> connectorConfigSetDTOToDb(String connectorName,
			List<ConnectorParamDTO> dto) {
		Set<ConnectorComponentConfigDb> paramsDb = new HashSet<>();
		for (ConnectorParamDTO param : dto) {
			ConnectorComponentConfigDb paramDb = modelMapper.map(param, ConnectorComponentConfigDb.class);
			paramDb.setConnectorName(connectorName);
			paramsDb.add(paramDb);
		}
		log.debug("connectorConfigSetDTOToDb@MapperService - connector dto with name {} and a list of param to db",
				connectorName);
		return paramsDb;
	}

	/**
	 * Database statistics paginated on PageStatsDTO
	 * 
	 * @param statsDb
	 * @return
	 */
	public PageStatsDTO connectorStatsDbToDTO(Page<ConnectorExecutionStatsDb> statsDb) {
		PageStatsDTO pageDto = new PageStatsDTO();
		pageDto.setPage(statsDb.getNumber());
		pageDto.setSize(statsDb.getSize());
		pageDto.setTotalELements((int) statsDb.getTotalElements());
		pageDto.setTotalPages(statsDb.getTotalPages());
		for (ConnectorExecutionStatsDb statDb : statsDb.getContent()) {
			ConnectorStatsDTO statDto = modelMapper.map(statDb, ConnectorStatsDTO.class);
			pageDto.getStats().add(statDto);
		}
		log.debug("connectorStatsDbToDTO@MapperService - page of statsDb to page of statsDto");
		return pageDto;
	}

	public PageErrorsDTO connectorErrorsDbToDTO(Page<ConnectorExecutionErrorsDb> errorsDb) {
		PageErrorsDTO pageDto = new PageErrorsDTO();
		pageDto.setPage(errorsDb.getNumber());
		pageDto.setSize(errorsDb.getSize());
		pageDto.setTotalELements((int) errorsDb.getTotalElements());
		pageDto.setTotalPages(errorsDb.getTotalPages());
		for (ConnectorExecutionErrorsDb errorDb : errorsDb.getContent()) {
			ConnectorErrorsDTO errortDto = modelMapper.map(errorDb, ConnectorErrorsDTO.class);
			pageDto.getErrors().add(errortDto);
		}
		log.debug("connectorErrorsDbToDTO@MapperService - page of errorsDb to page of errorDto");
		return pageDto;
	}
}
