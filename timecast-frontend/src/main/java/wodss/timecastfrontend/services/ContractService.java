package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.mocks.MockEmployeeService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContractService extends AbstractService<Contract, ContractDto> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private EmployeeService employeeService;

    @Autowired
    public ContractService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.contract}") String apiURL,
                           MockEmployeeService employeeService) {
        super(restTemplate, apiURL, ContractDto.class);
        this.employeeService = employeeService;
    }

    public List<Contract> getByEmployee(Token token, Employee employee) {
        logger.debug("Request list for ContractDtos by Employee " + employee.getId() + " from api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<List<ContractDto>> response = restTemplate.exchange(apiURL, HttpMethod.GET, request,
                new ParameterizedTypeReference<List<ContractDto>>() {});

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            throwStatusCodeException(statusCode);
        }

        List<ContractDto> dtos = response.getBody();
        logger.debug("Received ContractDto list: " + dtos);
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .filter(dto -> dto.getEmployeeId() == employee.getId())
                .map(dto -> mapDtoToEntity(token, dto))
                .collect(Collectors.toList());
    }

    protected ContractDto mapEntityToDto(Token token, Contract entity) {
        if (entity == null) return null;
        ContractDto dto = new ContractDto();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setPensumPercentage(entity.getPensumPercentage());
        return dto;
    }

    protected Contract mapDtoToEntity(Token token, ContractDto dto) {
        if (dto == null) return null;
        Contract entity = new Contract();
        entity.setId(dto.getId());
        entity.setEmployee(employeeService.getById(token, dto.getEmployeeId()));
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setPensumPercentage(dto.getPensumPercentage());
        return entity;
    }
}
