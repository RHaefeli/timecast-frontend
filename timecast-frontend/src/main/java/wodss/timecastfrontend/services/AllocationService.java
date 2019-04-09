package wodss.timecastfrontend.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Project;

@Component
public class AllocationService extends AbstractService<Allocation>{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AllocationService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.allocation}") String apiURL) {
        super(restTemplate, apiURL, Allocation.class);
    }
    
    public List<Allocation> getAllocations(long employeeId, long projectId) {
    	Map<String, String> uriVar = new HashMap<>();
		if (employeeId >= 0) {
			uriVar.put("employeeId", String.valueOf(employeeId));
		}
		if (projectId >= 0) {
			uriVar.put("projectId", String.valueOf(projectId));
		}
		logger.debug("Get Allocations with params: {} {}",  employeeId, projectId );
		ResponseEntity<List<Allocation>> response = restTemplate.exchange(apiURL, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Allocation>>() {
				}, uriVar);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			AbstractService.throwStatusCodeException(response.getStatusCode());
		}
		
		List<Allocation> allocations = response.getBody();
        logger.debug("Received allocations: {}", allocations);
		
		return response.getBody();
    }

	
}
