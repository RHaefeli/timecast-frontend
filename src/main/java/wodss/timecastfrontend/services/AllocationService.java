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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.dto.AllocationDto;
import wodss.timecastfrontend.domain.Token;

@Component
public class AllocationService extends AbstractService<Allocation, AllocationDto>{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private ContractService contractService;
    private ProjectService projectService;

    @Autowired
    public AllocationService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.allocation}") String apiURL, ContractService contractService, ProjectService projectService) {
        super(restTemplate, apiURL, AllocationDto.class, new ParameterizedTypeReference<List<AllocationDto>>() {});
        this.contractService = contractService;
        this.projectService = projectService;
    }
    
    public List<Allocation> getAllocations(Token token, long employeeId, long projectId, Date fromDate, Date toDate) {
    	DateFormat domainFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Map<String, String> uriVar = new HashMap<>();
		if (employeeId >= 0) {
			uriVar.put("employeeId", String.valueOf(employeeId));
		}
		if (projectId >= 0) {
			uriVar.put("projectId", String.valueOf(projectId));
		}
		if (fromDate != null) {
			uriVar.put("fromDate", domainFormat.format(fromDate));
		}
		if (toDate != null) {
			uriVar.put("toDate", domainFormat.format(toDate));
		}
		logger.debug("Get Allocations with params: {} {} {} {}",  employeeId, projectId, fromDate, toDate );
		ResponseEntity<List<AllocationDto>> response = restTemplate.exchange(apiURL, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<AllocationDto>>() {
				}, uriVar);
		
		if (response.getStatusCode() != HttpStatus.OK) {
			// Other status codes are mapped by the RestTemplate Error Handler
			throw new IllegalStateException(response.getStatusCode().toString());
		}
		
		List<AllocationDto> allocationDtos = response.getBody();
        logger.debug("Received allocations: {}", allocationDtos);
        List<Allocation> allocations = allocationDtos.stream().map(dto -> mapDtoToEntity(token, dto)).collect(Collectors.toList());
		return allocations;
    }

	@Override
	protected AllocationDto mapEntityToDto(Token token, Allocation entity) {
		if (entity == null) return null;
		System.out.println(1);
		AllocationDto allocationDto = new AllocationDto();
		System.out.println(allocationDto);
		allocationDto.setId(entity.getId());
		System.out.println(3);
		allocationDto.setContractId(entity.getContract().getId());
		System.out.println(4);
		DateFormat domainFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(5);
		allocationDto.setStartDate(domainFormat.format(entity.getStartDate()));
		System.out.println(6);
		allocationDto.setEndDate(domainFormat.format(entity.getEndDate()));
		System.out.println(7);
		allocationDto.setProjectId(entity.getProject().getId());
		System.out.println(8);
		allocationDto.setPensumPercentage(entity.getPensumPercentage());
		System.out.println(9);
		return allocationDto;
	}

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
