package cat.santfeliu.api.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import cat.santfeliu.api.api.JobApi;
import cat.santfeliu.api.service.JobService;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-12-07T18:27:59.210Z[GMT]")
@RestController
public class JobApiController implements JobApi {

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

	@Override
	public ResponseEntity<Object> runJob(@PathVariable("id") String jobId) {
		jobService.runJob(jobId);
		return ResponseEntity.ok().build();
	}
}
