package wodss.timecastfrontend.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import wodss.timecastfrontend.domain.AllocationDto;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.services.mocks.MockContractService;
import wodss.timecastfrontend.services.mocks.MockProjectService;

@Component
public class AllocationService extends AbstractService<Allocation, AllocationDto>{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private ContractService contractService;
    private ProjectService projectService;

    @Autowired
    public AllocationService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.allocation}") String apiURL, MockContractService contractService, MockProjectService projectService) {
        super(restTemplate, apiURL, AllocationDto.class);
        this.contractService = contractService;
        this.projectService = projectService;
    }
    
    public List<Allocation> getAllocations(Token token, long employeeId, long projectId, String fromDate, String toDate) {
    	Map<String, String> uriVar = new HashMap<>();
		if (employeeId >= 0) {
			uriVar.put("employeeId", String.valueOf(employeeId));
		}
		if (projectId >= 0) {
			uriVar.put("projectId", String.valueOf(projectId));
		}
		if (fromDate != null && !fromDate.equals("")) {
			uriVar.put("fromDate", fromDate);
		}
		if (toDate != null && !toDate.equals("")) {
			uriVar.put("toDate", toDate);
		}
		logger.debug("Get Allocations with params: {} {} {} {}",  employeeId, projectId, fromDate, toDate );
		ResponseEntity<List<AllocationDto>> response = restTemplate.exchange(apiURL, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<AllocationDto>>() {
				}, uriVar);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			AbstractService.throwStatusCodeException(response.getStatusCode());
		}
		
		List<AllocationDto> allocationDtos = response.getBody();
        logger.debug("Received allocations: {}", allocationDtos);
        List<Allocation> allocations = allocationDtos.stream().map(dto -> mapDtoToEntity(token, dto)).collect(Collectors.toList());
		return allocations;
    }

	@Override
	protected AllocationDto mapEntityToDto(Token token, Allocation entity) {
		if (entity == null) return null;
		AllocationDto allocationDto = new AllocationDto();
		allocationDto.setId(entity.getId());
		allocationDto.setContractId(entity.getContract().getId());
		allocationDto.setStartDate(entity.getStartDate());
		allocationDto.setEndDate(entity.getEndDate());
		allocationDto.setProjectId(entity.getProject().getId());
		allocationDto.setPensumPercentage(entity.getPensumPercentage());
		return allocationDto;
	}

	@Override
	protected Allocation mapDtoToEntity(Token token, AllocationDto dto) {
		if (dto == null) return null;
		Allocation allocation = new Allocation(dto.getStartDate(), dto.getEndDate(), dto.getPensumPercentage());
		allocation.setContract(contractService.getById(token, dto.getContractId()));
		allocation.setId(dto.getId());
		allocation.setProject(projectService.getById(token, dto.getProjectId()));
		return allocation;
	}

	
}
