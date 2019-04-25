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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;

@Component
public class ProjectService extends AbstractService<Project, ProjectDto>{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProjectService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.project}") String apiURL) {
        super(restTemplate, apiURL, ProjectDto.class);
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

		switch (response.getStatusCode()) {
		case NOT_FOUND:
			throw new TimecastNotFoundException(response.getStatusCode().getReasonPhrase());
		case INTERNAL_SERVER_ERROR:
			throw new TimecastInternalServerErrorException(response.getStatusCode().getReasonPhrase());
		case OK:
			List<ProjectDto> dtos = response.getBody();
			if (dtos == null) {
				return null;
			}
			return dtos.stream().map(dto -> mapDtoToEntity(dto)).collect(Collectors.toList());
		}
		// TODO fix
		throw new IllegalStateException();

	}

	@Override
	protected ProjectDto mapEntityToDto(Project entity) {
    	if (entity == null) return null;
    	ProjectDto dto = new ProjectDto();
    	dto.setId(entity.getId());
    	dto.setName(entity.getName());
    	dto.setFtePercentage(entity.getFtePercentage());
    	dto.setStartDate(entity.getStartDate());
    	dto.setEndDate(entity.getEndDate());
    	dto.setProjectManagerId(entity.getProjectManagerId());
		return dto;
	}

	@Override
	protected Project mapDtoToEntity(ProjectDto dto) {
		if (dto == null) return null;
		Project entity = new Project();
		entity.setId(dto.getId());
		entity.setName(dto.getName());
		entity.setFtePercentage(dto.getFtePercentage());
		entity.setStartDate(dto.getStartDate());
		entity.setEndDate(dto.getEndDate());
		entity.setProjectManagerId(dto.getProjectManagerId());
		return entity;
	}
}
