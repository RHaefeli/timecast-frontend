package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.dto.ContractDto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ContractService extends AbstractService<Contract, ContractDto> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private EmployeeService employeeService;

    @Autowired
    public ContractService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.contract}") String apiURL,
                           EmployeeService employeeService) {
        super(restTemplate, apiURL, ContractDto.class, new ParameterizedTypeReference<List<ContractDto>>() {});
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
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
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

    public List<Contract> getCurrentContracts(Token token) {
        logger.debug("Request current ContractDtos from api: " + apiURL);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        Map<String, String> uriVar = new HashMap<>();
        DateFormat dtoFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = dtoFormat.format(new Date());
        String paramUrl = apiURL + "?fromDate=" + fromDate;

        HttpEntity<?> request = new HttpEntity<>(headers);
        ResponseEntity<List<ContractDto>> response = restTemplate.exchange(paramUrl, HttpMethod.GET, request,
                new ParameterizedTypeReference<List<ContractDto>>() {}, uriVar);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.OK) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }

        List<ContractDto> dtos = response.getBody();
        if (dtos != null && dtos.size() > 0) {
            return dtos.stream().map(dto -> mapDtoToEntity(token, dto)).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    protected ContractDto mapEntityToDto(Token token, Contract entity) {
        if (entity == null) return null;
        ContractDto dto = new ContractDto();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployee().getId());
        DateFormat domainFormat = new SimpleDateFormat("yyyy-MM-dd");
        dto.setStartDate(domainFormat.format(entity.getStartDate()));
        dto.setEndDate(domainFormat.format(entity.getEndDate()));
        dto.setPensumPercentage(entity.getPensumPercentage());
        return dto;
    }

    protected Contract mapDtoToEntity(Token token, ContractDto dto) {
        if (dto == null) return null;
        Contract entity = new Contract();
        entity.setId(dto.getId());
        entity.setEmployee(employeeService.getById(token, dto.getEmployeeId()));

        try {
            DateFormat dtoFormat = new SimpleDateFormat("yyyy-MM-dd");
            entity.setStartDate(dtoFormat.parse(dto.getStartDate()));
            entity.setEndDate(dtoFormat.parse(dto.getEndDate()));
        } catch (ParseException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

        entity.setPensumPercentage(dto.getPensumPercentage());
        return entity;
    }
}
