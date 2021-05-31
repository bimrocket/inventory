package cat.santfeliu.api.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cat.santfeliu.api.dto.ComponentConfigKeyDTO;
import cat.santfeliu.api.dto.ConnectorComponentDTO;
import cat.santfeliu.api.dto.ConnectorDTO;
import cat.santfeliu.api.dto.ConnectorParamDTO;
import cat.santfeliu.api.dto.ConnectorStatusDTO;
import cat.santfeliu.api.enumerator.ComponentEnum;
import cat.santfeliu.api.enumerator.GeoserverLoaderConfigKeys;
import cat.santfeliu.api.enumerator.RhinoTransformerConfigKeys;
import cat.santfeliu.api.model.ConnectorComponentConfigDb;
import cat.santfeliu.api.model.ConnectorComponentDb;
import cat.santfeliu.api.model.ConnectorDb;
import cat.santfeliu.api.model.ConnectorStatusDb;
import cat.santfeliu.api.repo.ConnectorComponentConfigRepo;

@Service
public class MapperService {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ConnectorComponentConfigRepo conConfigRepo;
	
	public ConnectorStatusDTO statusDbToDTO(ConnectorStatusDb statusDb) {
		ConnectorStatusDTO dto = new ConnectorStatusDTO();
		return modelMapper.map(statusDb, ConnectorStatusDTO.class);
		
	}
	
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
		} else if (dto.getConnectorComponentName().equals(ComponentEnum.RHINO_TRANSFORMER.getName())) {
			for (RhinoTransformerConfigKeys key : RhinoTransformerConfigKeys	.values()) {
				ComponentConfigKeyDTO configDto = new ComponentConfigKeyDTO();
				configDto.setConfigKey(key.getKey());
				configDto.setRequired(key.isRequired());
				configDto.setDescription(key.getDescription());
				dto.getComponentConfigKeys().add(configDto);
			}
		}
		return dto;
	}
	
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
	
	public ConnectorDb connectorDTOToDb(ConnectorDTO dto) {
		ConnectorDb db = modelMapper.map(dto, ConnectorDb.class);;
		return db;
	}
	
	public Set<ConnectorComponentConfigDb> connectorConfigSetDTOToDb(String connectorName,List<ConnectorParamDTO> dto) {
		Set<ConnectorComponentConfigDb> paramsDb = new HashSet<>();
		for (ConnectorParamDTO param : dto) {
			ConnectorComponentConfigDb paramDb = modelMapper.map(param, ConnectorComponentConfigDb.class);
			paramDb.setConnectorName(connectorName);
			paramsDb.add(paramDb);
		}
		return paramsDb;
	}
}
