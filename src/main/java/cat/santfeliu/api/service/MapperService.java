package cat.santfeliu.api.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import cat.santfeliu.api.dto.ComponentConfigKeyDTO;
import cat.santfeliu.api.dto.ConnectorComponentDTO;
import cat.santfeliu.api.dto.ConnectorDTO;
import cat.santfeliu.api.dto.ConnectorParamDTO;
import cat.santfeliu.api.dto.ConnectorStatsDTO;
import cat.santfeliu.api.dto.ConnectorStatusDTO;
import cat.santfeliu.api.enumerator.ComponentEnum;
import cat.santfeliu.api.enumerator.GemWebLoaderConfigKeys;
import cat.santfeliu.api.enumerator.GeoserverLoaderConfigKeys;
import cat.santfeliu.api.enumerator.GeoserverSenderConfigKeys;
import cat.santfeliu.api.enumerator.GlobalLoaderConfigKeys;
import cat.santfeliu.api.enumerator.JSONKafkaLoaderConfigKeys;
import cat.santfeliu.api.enumerator.RhinoTransformerConfigKeys;
import cat.santfeliu.api.model.ConnectorComponentConfigDb;
import cat.santfeliu.api.model.ConnectorComponentDb;
import cat.santfeliu.api.model.ConnectorDb;
import cat.santfeliu.api.model.ConnectorExecutionStatsDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.model.PageStatsDTO;
import cat.santfeliu.api.repo.ConnectorComponentConfigRepo;

@Service
public class MapperService {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ConnectorComponentConfigRepo conConfigRepo;
	
	/**
	 * Retorna dto de ConnectorStatusDb
	 * @param statusDb
	 * @return dto ConnectorStatusDTO
	 */
	public ConnectorStatusDTO statusDbToDTO(ConnectorStatusDb statusDb) {
		ConnectorStatusDTO dto = new ConnectorStatusDTO();
		return modelMapper.map(statusDb, ConnectorStatusDTO.class);
		
	}
	
	/**
	 * Retorna dto de componente de connector ConnectorComponentDb
	 * @param db
	 * @return dto
	 */
	public ConnectorComponentDTO componentDbToDTO(ConnectorComponentDb db) {
		ConnectorComponentDTO dto = modelMapper.map(db, ConnectorComponentDTO.class);
		if (dto.getConnectorComponentName().equals(ComponentEnum.GEOSERVER_LOADER.getName())) {
			
			for (GeoserverLoaderConfigKeys key : GeoserverLoaderConfigKeys.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
			for (GlobalLoaderConfigKeys key : GlobalLoaderConfigKeys.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
			
		} else if (dto.getConnectorComponentName().equals(ComponentEnum.JSON_KAFKA_LOADER.getName())) {
			for (JSONKafkaLoaderConfigKeys key : JSONKafkaLoaderConfigKeys	.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
			
			for (GlobalLoaderConfigKeys key : GlobalLoaderConfigKeys.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
		} else if (dto.getConnectorComponentName().equals(ComponentEnum.GEMWEB_LOADER.getName())) {
			for (GemWebLoaderConfigKeys key : GemWebLoaderConfigKeys.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
			
			for (GlobalLoaderConfigKeys key : GlobalLoaderConfigKeys.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
		} else if (dto.getConnectorComponentName().equals(ComponentEnum.RHINO_TRANSFORMER.getName())) {
			for (RhinoTransformerConfigKeys key : RhinoTransformerConfigKeys	.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
		} else if (dto.getConnectorComponentName().equals(ComponentEnum.GEOSERVER_SENDER.getName())) {
			for (GeoserverSenderConfigKeys key : GeoserverSenderConfigKeys	.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
		} else if (dto.getConnectorComponentName().equals(ComponentEnum.JSON_KAFKA_SENDER.getName())) {
			for (GeoserverSenderConfigKeys key : GeoserverSenderConfigKeys	.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
		}
		return dto;
	}
	
	/**
	 * Retorna dto de connector desde ConnectorDb
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
		dto.setConnectorParams(params);
		return dto;
	}
	
	/**
	 * ConnectorDTO a model de base de dades ConnectorDb
	 * @param dto
	 * @return db
	 */
	public ConnectorDb connectorDTOToDb(ConnectorDTO dto) {
		ConnectorDb db = modelMapper.map(dto, ConnectorDb.class);;
		return db;
	}
	
	/**
	 * Configs de connector com dto a Set de base de dades
	 * @param connectorName
	 * @param dto
	 * @return set db
	 */
	public Set<ConnectorComponentConfigDb> connectorConfigSetDTOToDb(String connectorName,List<ConnectorParamDTO> dto) {
		Set<ConnectorComponentConfigDb> paramsDb = new HashSet<>();
		for (ConnectorParamDTO param : dto) {
			ConnectorComponentConfigDb paramDb = modelMapper.map(param, ConnectorComponentConfigDb.class);
			paramDb.setConnectorName(connectorName);
			paramsDb.add(paramDb);
		}
		return paramsDb;
	}

	/**
	 * Estadístiques db paginades a PageStatsDTO
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
		return pageDto;
	}
}
