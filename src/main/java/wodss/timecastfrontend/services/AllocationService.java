package wodss.timecastfrontend.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.dto.AllocationDto;
import wodss.timecastfrontend.domain.Token;

/**
 * Service handles all calls regarding allocations to the backend
 * 
 */
@Component
public class AllocationService extends AbstractService<Allocation, AllocationDto>{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private ContractService contractService;
    private ProjectService projectService;

    /**
     * Contructor
     * @param restTemplate
     * @param apiURL
     * @param contractService
     * @param projectService
     */
    @Autowired
    public AllocationService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.allocation}") String apiURL, ContractService contractService, ProjectService projectService) {
        super(restTemplate, apiURL, AllocationDto.class, new ParameterizedTypeReference<List<AllocationDto>>() {});
        this.contractService = contractService;
        this.projectService = projectService;
    }
    
    /**
     * Get all allocations
     * @param token
     * @param employeeId -1 for not defined
     * @param projectId -1 for not defined
     * @param fromDate null for not defined
     * @param toDate null for not defined
     * @return List of allocations
     */
    public List<Allocation> getAllocations(Token token, long employeeId, long projectId, Date fromDate, Date toDate) {
		logger.debug("Get Allocations with params: {} {} {} {}",  employeeId, projectId, fromDate, toDate );
		StringBuilder paramUrl = new StringBuilder(apiURL);
		if (employeeId >= 0 || projectId >= 0 || fromDate != null || toDate != null) {
			paramUrl.append("?");
		}

		DateFormat domainFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (employeeId >= 0) {
			paramUrl.append("employeeId=");
			paramUrl.append(String.valueOf(employeeId));
			paramUrl.append("&");
		}
		if (projectId >= 0) {
			paramUrl.append("projectId=");
			paramUrl.append(String.valueOf(projectId));
			paramUrl.append("&");
		}
		if (fromDate != null) {
			paramUrl.append("fromDate=");
			paramUrl.append(domainFormat.format(fromDate));
			paramUrl.append("&");
		}
		if (toDate != null) {
			paramUrl.append("toDate=");
			paramUrl.append(domainFormat.format(toDate));
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);

		ResponseEntity<List<AllocationDto>> response = restTemplate.exchange(paramUrl.toString(), HttpMethod.GET, request,
				new ParameterizedTypeReference<List<AllocationDto>>() {});
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// Other status codes are mapped by the RestTemplate Error Handler
			throw new IllegalStateException(response.getStatusCode().toString());
		}
		
		List<AllocationDto> allocationDtos = response.getBody();
        logger.debug("Received allocations: {}", allocationDtos);
        List<Allocation> allocations = allocationDtos.stream().map(dto -> mapDtoToEntity(token, dto)).collect(Collectors.toList());
		return allocations;
    }

    /**
     * Map Allocation to Dto
     * @param token
     * @param entity
     * @return allocation dto
     */
	@Override
	protected AllocationDto mapEntityToDto(Token token, Allocation entity) {
		if (entity == null) return null;
		AllocationDto allocationDto = new AllocationDto();
		allocationDto.setId(entity.getId());
		allocationDto.setContractId(entity.getContract().getId());
		DateFormat domainFormat = new SimpleDateFormat("yyyy-MM-dd");
		allocationDto.setStartDate(domainFormat.format(entity.getStartDate()));
		allocationDto.setEndDate(domainFormat.format(entity.getEndDate()));
		allocationDto.setProjectId(entity.getProject().getId());
		allocationDto.setPensumPercentage(entity.getPensumPercentage());
		return allocationDto;
	}

	/**
	 * Map Dto to allocation
	 * @param token
	 * @param dto
	 * @return Allocation
	 */
	@Override
	protected Allocation mapDtoToEntity(Token token, AllocationDto dto) {
		if (dto == null) return null;
		DateFormat dtoFormat = new SimpleDateFormat("yyyy-MM-dd");
		Allocation allocation;
		try {
			allocation = new Allocation(dtoFormat.parse(dto.getStartDate()), dtoFormat.parse(dto.getEndDate()), dto.getPensumPercentage());
		allocation.setContract(contractService.getById(token, dto.getContractId()));
		allocation.setId(dto.getId());
		allocation.setProject(projectService.getById(token, dto.getProjectId()));
		return allocation;
		} catch (ParseException e) {
			logger.debug("Could not parse dates: {} or {}", dto.getStartDate(), dto.getEndDate());
			throw new IllegalStateException();
		}
	}

	
}
