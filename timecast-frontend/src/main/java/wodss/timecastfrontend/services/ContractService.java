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

import wodss.timecastfrontend.domain.dto.ContractDTO;
import wodss.timecastfrontend.domain.dto.ProjectDTO;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;

@Component
public class ContractService extends AbstractService<ContractDTO> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public ContractService(RestTemplate restTemplate,
			@Value("${wodss.timecastfrontend.api.url.project}") String apiURL) {
		super(restTemplate, apiURL, ContractDTO.class);
	}

	public List<ContractDTO> getContracts(String fromDate, String toDate) {
		Map<String, String> uriVar = new HashMap<>();
		if (fromDate != null) {
			uriVar.put("fromDate", fromDate);
		}
		if (toDate != null) {
			uriVar.put("toDate", toDate);
		}
		ResponseEntity<List<ContractDTO>> response = restTemplate.exchange(apiURL, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<ContractDTO>>() {
				}, uriVar);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			AbstractService.throwStatusCodeException(response.getStatusCode());
		}
		
		return response.getBody();

	}
}
