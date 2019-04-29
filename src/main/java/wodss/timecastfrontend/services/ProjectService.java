package wodss.timecastfrontend.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.dto.ProjectDto;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;

/**
 * Service handles all calls regarding projects to the backend
 *
 */
@Component
public class ProjectService extends AbstractService<Project, ProjectDto>{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private EmployeeService employeeService;

	/**
	 * Constructor
	 * @param restTemplate
	 * @param apiURL
	 * @param employeeService
	 */
    @Autowired
    public ProjectService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.project}") String apiURL, EmployeeService employeeService) {
        super(restTemplate, apiURL, ProjectDto.class, new ParameterizedTypeReference<List<ProjectDto>>() {});
        this.employeeService = employeeService;
    }

    /**
     * Get all projects in a timespan
     * @param token
     * @param fromDate null if not defined
     * @param toDate null if not defined
     * @return List of projects
     * @throws TimecastNotFoundException
     * @throws TimecastInternalServerErrorException
     */
	public List<Project> getProjects(Token token, String fromDate, String toDate)
			throws TimecastNotFoundException, TimecastInternalServerErrorException {
    	StringBuilder paramUrl = new StringBuilder(apiURL);
    	if (fromDate != null || toDate != null) {
    		paramUrl.append("?");
		}
		if (fromDate != null) {
			paramUrl.append("fromDate=");
			paramUrl.append(fromDate);
			paramUrl.append("&");
		}
		if (toDate != null) {
			paramUrl.append("toDate=");
			paramUrl.append(toDate);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token.getToken());
		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<List<ProjectDto>> response = restTemplate.exchange(paramUrl.toString(), HttpMethod.GET, request,
				new ParameterizedTypeReference<List<ProjectDto>>() {});

		HttpStatus statusCode = response.getStatusCode();
		if (statusCode != HttpStatus.OK) {
			// Other status codes are mapped by the RestTemplate Error Handler
			throw new IllegalStateException(statusCode.toString());
		}

		List<ProjectDto> dtos = response.getBody();
		if (dtos == null) {
			return null;
		}
		return dtos.stream().map(dto -> mapDtoToEntity(token, dto)).collect(Collectors.toList());
	}

	/**
	 * Maps Project to dto
	 * @param token
	 * @param entity
	 * @return ProjectDto
	 */
	@Override
	protected ProjectDto mapEntityToDto(Token token, Project entity) {
    	if (entity == null) return null;
    	ProjectDto dto = new ProjectDto();
    	dto.setId(entity.getId());
    	dto.setName(entity.getName());
    	dto.setFtePercentage(entity.getFtePercentage());
    	DateFormat domainFormat = new SimpleDateFormat("yyyy-MM-dd");
    	dto.setStartDate(domainFormat.format(entity.getStartDate()));
    	dto.setEndDate(domainFormat.format(entity.getEndDate()));
    	dto.setProjectManagerId(entity.getProjectManager().getId());
		return dto;
	}

	/**
	 * Map dto to Project
	 * @param token
	 * @param dto
	 * @return Project
	 */
	@Override
	protected Project mapDtoToEntity(Token token, ProjectDto dto) {
		if (dto == null) return null;
		Project entity = new Project();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setFtePercentage(dto.getFtePercentage());
		try {
			DateFormat dtoFormat = new SimpleDateFormat("yyyy-MM-dd");
			entity.setStartDate(dtoFormat.parse(dto.getStartDate()));
			entity.setEndDate(dtoFormat.parse(dto.getEndDate()));
		} catch (ParseException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		entity.setProjectManager(employeeService.getById(token, dto.getProjectManagerId()));
		return entity;
	}
}
