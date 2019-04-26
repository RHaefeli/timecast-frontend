package wodss.timecastfrontend.services.mocks;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.ProjectService;

@Component
public class MockProjectService extends ProjectService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ProjectDTO> projectRepo;

	public MockProjectService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.project}") String apiURL) {
		super(restTemplate, apiURL);
        logger.debug("Using Mock Project Service!");
        logger.debug("API URL " + apiURL + " will not be used in the mock service!");
        
        //TODO fix workaround
        logger.debug("Generating mock projects");
        MockRepository.generateRepository();
        projectRepo = MockRepository.projects;
        logger.debug("Mockrepo: {}", projectRepo);
	}
	
	@Override
	public List<Project> getAll(Token token) throws TimecastNotFoundException, TimecastInternalServerErrorException {
		return projectRepo;
	}

	@Override
	public List<Project> getProjects(Token token, String fromDate, String toDate)
			throws TimecastNotFoundException, TimecastInternalServerErrorException {
		if (("".equals(fromDate) && "".equals(toDate)) || (fromDate == null) || (toDate == null)) {
			return projectRepo;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Project getById(Token token, long id) throws TimecastNotFoundException, TimecastInternalServerErrorException, TimecastForbiddenException {
		if (projectRepo.stream().anyMatch(p -> p.getId() == id)) {
			return projectRepo.stream().filter(p -> p.getId() == id).findFirst().get();
		} else {
			throw new TimecastNotFoundException("Project not found");
		}
	}

	@Override
	public Project create(Token token, Project newProject) throws TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		newProject.setId(nextProjectId++);
		projectRepo.add(newProject);
		return newProject;
	}

	@Override
	public Project update(Token token, Project updatedProject) throws TimecastNotFoundException, TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		if (projectRepo.stream().anyMatch(p -> p.getId() == updatedProject.getId())) {
			ProjectDTO oldProject = projectRepo.stream().filter(p -> p.getId() == updatedProject.getId()).findFirst().get();
			oldProject.setName(updatedProject.getName());
			oldProject.setFtePercentage(updatedProject.getFtePercentage());
			oldProject.setProjectManagerId(updatedProject.getProjectManagerId());
			oldProject.setStartDate(updatedProject.getStartDate());
			oldProject.setEndDate(updatedProject.getEndDate());
			return oldProject;
		} else {
			throw new TimecastNotFoundException("Project not found");
		}
	}

	@Override
	public void deleteById(Token token, long id) throws TimecastInternalServerErrorException, TimecastForbiddenException, TimecastNotFoundException {
		if (projectRepo.stream().anyMatch(p -> p.getId() == id)) {
			projectRepo.removeIf(p -> p.getId() == id);
		} else {
			throw new TimecastNotFoundException("Project not found");
		}
	}

}
