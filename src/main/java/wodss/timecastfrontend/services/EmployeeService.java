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
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.EmployeeLogin;
import wodss.timecastfrontend.dto.EmployeeDto;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.*;

import java.util.Collections;
import java.util.List;

/**
 * Service handles all calls regarding employees to the backend
 *
 */
@Component
public class EmployeeService extends AbstractService<Employee, EmployeeDto> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructor
     * @param restTemplate
     * @param apiURL
     */
    @Autowired
    public EmployeeService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.employee}") String apiURL) {
        super(restTemplate, apiURL, EmployeeDto.class, new ParameterizedTypeReference<List<EmployeeDto>>() {});
    }

    /**
     * Unsupported Operation, use create(Token token, EmployeeLogin entity)
     */
    @Override
    public Employee create(Token token, Employee entity) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastPreconditionFailedException, TimecastInternalServerErrorException {
        throw new UnsupportedOperationException("Use create method with EmployeeLogin");
    }

    /**
     * Creates a new Employee
     * @param token
     * @param entity Employee with additional login data
     * @return
     * @throws TimecastUnauthorizedException
     * @throws TimecastForbiddenException
     * @throws TimecastNotFoundException
     * @throws TimecastPreconditionFailedException
     * @throws TimecastInternalServerErrorException
     */
    public Employee create(Token token, EmployeeLogin entity) throws TimecastUnauthorizedException, TimecastForbiddenException,
            TimecastNotFoundException, TimecastPreconditionFailedException, TimecastInternalServerErrorException {
        logger.debug("Create Employee entity " + entity + " to api: " + apiURL);
        entity.setActive(true);
        EmployeeDto employeeDto = mapEntityToDto(token, entity);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiURL)
                .queryParam("password", entity.getPassword())
                .queryParam("role", entity.getRole().getValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<EmployeeDto> request = new HttpEntity<>(employeeDto, headers);
        ResponseEntity<EmployeeDto> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request, EmployeeDto.class);

        HttpStatus statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.CREATED) {
            // Other status codes are mapped by the RestTemplate Error Handler
            throw new IllegalStateException(statusCode.toString());
        }

        EmployeeDto newEntity = response.getBody();
        logger.debug("Received Employee entity: " + newEntity);
        return mapDtoToEntity(token, newEntity);
    }

    /**
     * Maps entity to dto
     * @param token
     * @param employee
     * @return EmployeeDto
     */
    protected EmployeeDto mapEntityToDto(Token token, Employee employee) {
        if (employee == null) return null;
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(employee.getId());
        employeeDto.setEmailAddress(employee.getEmailAddress());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setActive(employee.isActive());
        employeeDto.setRole(employee.getRole().getValue());
        return employeeDto;
    }

    /**
     * Maps dto to Employee
     * @param token
     * @param employeeDto
     * @return Employee
     */
    protected Employee mapDtoToEntity(Token token, EmployeeDto employeeDto) {
        if (employeeDto == null) return null;
        Employee employee = new Employee();
        employee.setId(employeeDto.getId());
        employee.setEmailAddress(employeeDto.getEmailAddress());
        employee.setLastName(employeeDto.getLastName());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setActive(employeeDto.isActive());
        employee.setRole(Role.valueOf(employeeDto.getRole()));
        return employee;
    }
}
