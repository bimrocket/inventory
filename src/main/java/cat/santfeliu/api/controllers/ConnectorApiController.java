package cat.santfeliu.api.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cat.santfeliu.api.api.ConnectorApi;
import cat.santfeliu.api.dto.ConnectorComponentDTO;
import cat.santfeliu.api.dto.ConnectorDTO;
import cat.santfeliu.api.dto.ConnectorStatusDTO;
import cat.santfeliu.api.dto.PageErrorsDTO;
import cat.santfeliu.api.dto.PageStatsDTO;
import cat.santfeliu.api.service.ConnectorApiService;
import cat.santfeliu.api.service.ConnectorManagerService;

@RestController
public class ConnectorApiController implements ConnectorApi {

	private static final Logger log = LoggerFactory.getLogger(ConnectorApiController.class);

	@Autowired
	private ConnectorApiService apiService;
	
	@Autowired
	private ConnectorManagerService managerService;
	
	@Override
	public ResponseEntity<List<ConnectorDTO>> getConnectors() {

		log.info("getConnectors@ConnectorApiController - find all connectors");
		return ResponseEntity.ok(apiService.getAllConnectors());
	}

	@Override
	public ResponseEntity<ConnectorDTO> createConnector(@NotNull @Valid @PathVariable("connectorName") String connectorName, @RequestBody ConnectorDTO connector) {

		log.info("createConnector@ConnectorApiController - create connector with name {}",connectorName);
		return ResponseEntity.ok(apiService.createConnector(connector));
	}

	@Override
	public ResponseEntity<ConnectorDTO> updateConnector(@NotNull @Valid @PathVariable("connectorName") String connectorName, @RequestBody ConnectorDTO connector) {

		log.info("updateConnector@ConnectorApiController - update connector with name {}", connectorName);
		return ResponseEntity.ok(apiService.updateConnector(connector));
	}

	@Override
	public ResponseEntity<ConnectorDTO> getConnector(@NotNull @Valid @PathVariable("connectorName") String connectorName) {

		log.info("getConnector@ConnectorApiController - get connector with name {}", connectorName);
		return ResponseEntity.ok(apiService.getConnector(connectorName));
	}

	@Override
	public ResponseEntity<ConnectorStatusDTO> startConnector(@NotNull @Valid @PathVariable("connectorName") String connectorName) {

		log.info("startConnector@ConnectorApiController - start controller with name {}", connectorName);
		return ResponseEntity.ok(managerService.startConnector(connectorName));
	}

	@Override
	public ResponseEntity<ConnectorStatusDTO> stopConnector(@NotNull @Valid @PathVariable("connectorName") String connectorName) {

		log.info("stopConnector@ConnectorApiController - stop connetor with name {}", connectorName);
		return ResponseEntity.ok(managerService.stopConnector(connectorName));
		
	}

	@Override
	public ResponseEntity<ConnectorStatusDTO> connectorStatus(@NotNull @Valid @PathVariable("connectorName") String connectorName) {

		log.info("connectorStatus@ConnectorApiController - get status of connector with name {}", connectorName);
		return ResponseEntity.ok(managerService.connectorStatus(connectorName));
		
	}

	@Override
	public ResponseEntity<List<ConnectorComponentDTO>> connectorComponents() {

		log.info("connectorComponents@ConnectorApiController - get all components");
		return ResponseEntity.ok(apiService.getAllComponents());
	}

	@Override
	public ResponseEntity<PageStatsDTO> connectorStats(@NotNull @Valid @PathVariable("connectorName") String connectorName, @RequestParam(name = "page", defaultValue = "0") int page,@RequestParam(name = "size", defaultValue = "10") int size) {
		
		log.info("connectorStats@ConnectorApiController - get stats of connector with name {} of page {} with size {}",connectorName,page,size);
		return ResponseEntity.ok(managerService.connectorStats(connectorName, page, size));
		
	}
	
	@Override
	public ResponseEntity<PageErrorsDTO> connectorErrors(@NotNull @Valid @PathVariable("connectorName") String connectorName, @RequestParam(name = "page", defaultValue = "0") int page,@RequestParam(name = "size", defaultValue = "10") int size) {
		
		log.info("connectorErrors@ConnectorApiController - get stats of connector with name {} of page {} with size {}",connectorName,page,size);
		return ResponseEntity.ok(managerService.connectorErrors(connectorName, page, size));
		
	}
}
