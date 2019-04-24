package wodss.timecastfrontend.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;

@Component
public class ProjectService extends AbstractService<Project>{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProjectService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.project}") String apiURL) {
        super(restTemplate, apiURL, Project.class);
    }

	public List<Project> getProjects(Token token, String fromDate, String toDate)
			throws TimecastNotFoundException, TimecastInternalServerErrorException {
		Map<String, String> uriVar = new HashMap<>();
		if (fromDate != null) {
			uriVar.put("fromDate", fromDate);
		}
		if (toDate != null) {
			uriVar.put("toDate", toDate);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<List<Project>> response = restTemplate.exchange(apiURL, HttpMethod.GET, request,
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
}
