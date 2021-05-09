package cat.santfeliu.api.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import cat.santfeliu.api.annotations.SantFeliuRestController;
import cat.santfeliu.api.dto.ApiError;
import cat.santfeliu.api.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@SantFeliuRestController
public class JobApiController {

	private static final Logger log = LoggerFactory.getLogger(JobApiController.class);

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	@Autowired
	private JobService jobService;

	@org.springframework.beans.factory.annotation.Autowired
	public JobApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

    @Operation(summary = "", description = "", tags={ "downloads" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "OK") })
    @RequestMapping(value = "/run/job/{id}",
        method = RequestMethod.GET)
	public ResponseEntity<Object> runJob(@PathVariable("id") String jobId) {
		try { 
			jobService.runJob(jobId);
		} catch (Exception e) {
			log.error("runJob@JobApiController - exception running job {}", e.getMessage(), e);
			return new ResponseEntity<>(new ApiError("error_501", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), ""), HttpStatus.INTERNAL_SERVER_ERROR);

		} finally {
			
		}
		return ResponseEntity.ok().build();
	}
}
