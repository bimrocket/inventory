	package cat.santfeliu.api.api;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.websocket.server.PathParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cat.santfeliu.api.dto.ConnectorComponentDTO;
import cat.santfeliu.api.dto.ConnectorDTO;
import cat.santfeliu.api.dto.ConnectorStatusDTO;
import cat.santfeliu.api.dto.PageErrorsDTO;
import cat.santfeliu.api.dto.PageStatsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface ConnectorApi {

    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors",
        method = RequestMethod.GET)
    ResponseEntity<List<ConnectorDTO>> getConnectors();
    
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/{connectorName}",
        method = RequestMethod.POST)
    ResponseEntity<ConnectorDTO> createConnector(@NotNull @Parameter(in = ParameterIn.PATH, description = "" ,required=true,schema=@Schema()) @Valid @PathParam(value = "connectorName") String connectorName, @RequestBody ConnectorDTO connector);
    
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/{connectorName}",
        method = RequestMethod.PUT)
    ResponseEntity<ConnectorDTO> updateConnector(@NotNull @Parameter(in = ParameterIn.PATH, description = "" ,required=true,schema=@Schema()) @Valid @PathParam(value = "connectorName") String connectorName, @RequestBody ConnectorDTO connector);
    
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/{connectorName}",
        method = RequestMethod.GET)
    ResponseEntity<ConnectorDTO> getConnector(@NotNull @Parameter(in = ParameterIn.PATH, description = "" ,required=true,schema=@Schema()) @Valid @PathParam(value = "connectorName") String connectorName);
    
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/{connectorName}/start",
        method = RequestMethod.GET)
    ResponseEntity<ConnectorStatusDTO> startConnector(@NotNull @Parameter(in = ParameterIn.PATH, description = "" ,required=true,schema=@Schema()) @Valid @PathParam(value = "connectorName") String connectorName);
    
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/{connectorName}/stop",
        method = RequestMethod.GET)
    ResponseEntity<ConnectorStatusDTO> stopConnector(@NotNull @Parameter(in = ParameterIn.PATH, description = "" ,required=true,schema=@Schema()) @Valid @PathParam(value = "connectorName") String connectorName);
    
    
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/{connectorName}/status",
        method = RequestMethod.GET)
    ResponseEntity<ConnectorStatusDTO> connectorStatus(@NotNull @Parameter(in = ParameterIn.PATH, description = "" ,required=true,schema=@Schema()) @Valid @PathParam(value = "connectorName") String connectorName);
    
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/components",
        method = RequestMethod.GET)
    ResponseEntity<List<ConnectorComponentDTO>> connectorComponents();
   
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/{connectorName}/stats",
        method = RequestMethod.GET)
    ResponseEntity<PageStatsDTO> connectorStats(@NotNull @Parameter(in = ParameterIn.PATH, description = "" ,required=true,schema=@Schema()) @Valid @PathParam(value = "connectorName") String connectorName, int page, int size);
    
    @Operation(summary = "", description = "", tags={ "connectors" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/connectors/{connectorName}/errors",
        method = RequestMethod.GET)
    ResponseEntity<PageErrorsDTO> connectorErrors(@NotNull @Parameter(in = ParameterIn.PATH, description = "" ,required=true,schema=@Schema()) @Valid @PathParam(value = "connectorName") String connectorName, int page, int size);
    
}

