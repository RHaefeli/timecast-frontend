package wodss.timecastfrontend.services;

import java.nio.channels.IllegalSelectorException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;

@Component
public class ProjectService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private final RestTemplate restTemplate;

	@Value("${wodss.timecastfrontend.api.url.project}")
	private String apiURL;

	@Autowired
	public ProjectService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<Project> getProjects() throws TimecastNotFoundException, TimecastInternalServerErrorException {
		ResponseEntity<List<Project>> response = restTemplate.exchange(apiURL, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Project>>() {
				});

		switch (response.getStatusCode()) {
		case NOT_FOUND:
			throw new TimecastNotFoundException(response.getStatusCode().getReasonPhrase());
		case INTERNAL_SERVER_ERROR:
			throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
		case OK:
			return response.getBody();
		}
		// TODO fix
		throw new IllegalStateException();
	}

	public List<Project> getProjects(String fromDate, String toDate)
			throws TimecastNotFoundException, TimecastInternalServerErrorException {
		Map<String, String> uriVar = new HashMap<>();
		if (fromDate != null) {
			uriVar.put("fromDate", fromDate);
		}
		if (toDate != null) {
			uriVar.put("toDate", toDate);
		}
		ResponseEntity<List<Project>> response = restTemplate.exchange(apiURL, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Project>>() {
				}, uriVar);

		switch (response.getStatusCode()) {
		case NOT_FOUND:
			throw new TimecastNotFoundException(response.getStatusCode().getReasonPhrase());
		case INTERNAL_SERVER_ERROR:
			throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
		case OK:
			return response.getBody();
		}
		// TODO fix
		throw new IllegalStateException();

	}

	public Project getProject(int id) throws TimecastNotFoundException, TimecastInternalServerErrorException, TimecastForbiddenException {
		ResponseEntity<Project> response = restTemplate.getForEntity(apiURL + "/" + id, Project.class);

		switch (response.getStatusCode()) {
		case NOT_FOUND:
			throw new TimecastNotFoundException(response.getStatusCode().getReasonPhrase());
		case FORBIDDEN:
			throw new TimecastForbiddenException(response.getStatusCode().getReasonPhrase());
		case INTERNAL_SERVER_ERROR:
			throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
		case OK:
			return response.getBody();
		}
		
		//TODO fix
		throw new IllegalStateException();
	}

	public Project createProject(Project newProject) throws TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		ResponseEntity<Project> response = restTemplate.postForEntity(apiURL, newProject, Project.class);
		
		switch (response.getStatusCode()) {
		case PRECONDITION_FAILED:
			throw new TimecastPreconditionFailedException(response.getStatusCode().getReasonPhrase());
		case FORBIDDEN:
			throw new TimecastForbiddenException(response.getStatusCode().getReasonPhrase());
		case INTERNAL_SERVER_ERROR:
			throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
		case OK:
			return response.getBody();
		}
		
		//TODO fix
		throw new IllegalStateException();
	}

	public Project updateProject(int id, Project updatedProject) throws TimecastNotFoundException, TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		HttpEntity<Project> requestEntity = new HttpEntity<Project>(updatedProject);
		ResponseEntity<Project> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.PUT, requestEntity, Project.class);
		
		switch (response.getStatusCode()) {
		case NOT_FOUND:
			throw new TimecastNotFoundException(response.getStatusCode().getReasonPhrase());
		case PRECONDITION_FAILED:
			throw new TimecastPreconditionFailedException(response.getStatusCode().getReasonPhrase());
		case FORBIDDEN:
			throw new TimecastForbiddenException(response.getStatusCode().getReasonPhrase());
		case INTERNAL_SERVER_ERROR:
			throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
		case OK:
			return response.getBody();
		}
		
		//TODO fix
		throw new IllegalStateException();
	}

	public void deleteProject(int id) throws TimecastInternalServerErrorException, TimecastForbiddenException, TimecastNotFoundException {
		ResponseEntity<Void> response = restTemplate.exchange(apiURL + "/" + id, HttpMethod.DELETE,	null, Void.class);
		
		switch (response.getStatusCode()) {
		case NOT_FOUND:
			throw new TimecastNotFoundException(response.getStatusCode().getReasonPhrase());

		case FORBIDDEN:
			throw new TimecastForbiddenException(response.getStatusCode().getReasonPhrase());
		case INTERNAL_SERVER_ERROR:
			throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
		case OK:
			return;
		}
		
		//TODO fix
		throw new IllegalStateException();
	}

}
