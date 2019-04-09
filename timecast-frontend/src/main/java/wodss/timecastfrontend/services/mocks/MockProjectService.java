package wodss.timecastfrontend.services.mocks;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.ProjectService;

public class MockProjectService extends ProjectService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<Project> projectRepo;

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
	public List<Project> getAll() {
		logger.debug("Get all projects without param");
		return projectRepo;
	}

	@Override
	public List<Project> getProjects(long projectManagerId, String fromDate, String toDate){
		logger.debug("Get all projects with params {} {} {}",projectManagerId, fromDate, toDate);
		if (("".equals(fromDate) && "".equals(toDate)) || (fromDate == null) || (toDate == null)) {
			return projectRepo;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Project getById(long id) {
		if (projectRepo.stream().anyMatch(p -> p.getId() == id)) {
			return projectRepo.stream().filter(p -> p.getId() == id).findFirst().get();
		} else {
			throw new TimecastNotFoundException("Project not found");
		}
	}

	@Override
	public Project create(Project newProject) throws TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		newProject.setId(MockRepository.nextProjectId++);
		projectRepo.add(newProject);
		return newProject;
	}

	@Override
	public Project update(Project updatedProject) throws TimecastNotFoundException, TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		if (projectRepo.stream().anyMatch(p -> p.getId() == updatedProject.getId())) {
			Project oldProject = projectRepo.stream().filter(p -> p.getId() == updatedProject.getId()).findFirst().get();
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
	public void deleteById(long id) throws TimecastInternalServerErrorException, TimecastForbiddenException, TimecastNotFoundException {
		if (projectRepo.stream().anyMatch(p -> p.getId() == id)) {
			projectRepo.removeIf(p -> p.getId() == id);
		} else {
			throw new TimecastNotFoundException("Project not found");
		}
	}

}
