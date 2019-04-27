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

@Component
public class ProjectService extends AbstractService<Project, ProjectDto>{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private EmployeeService employeeService;

    @Autowired
    public ProjectService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.project}") String apiURL, EmployeeService employeeService) {
        super(restTemplate, apiURL, ProjectDto.class, new ParameterizedTypeReference<List<ProjectDto>>() {});
        this.employeeService = employeeService;
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
		ResponseEntity<List<ProjectDto>> response = restTemplate.exchange(apiURL, HttpMethod.GET, request,
				new ParameterizedTypeReference<List<ProjectDto>>() {
				}, uriVar);

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
