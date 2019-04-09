package wodss.timecastfrontend.services.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
	private int nextProjectId = 0;

	public MockProjectService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.project}") String apiURL) {
		super(restTemplate, apiURL);
        logger.debug("Using Mock Project Service!");
        logger.debug("API URL " + apiURL + " will not be used in the mock service!");
		projectRepo = generateProjects();
	}
	
	@Override
	public List<Project> getAll() throws TimecastNotFoundException, TimecastInternalServerErrorException {
		return projectRepo;
	}

	@Override
	public List<Project> getProjects(String fromDate, String toDate)
			throws TimecastNotFoundException, TimecastInternalServerErrorException {
		if (("".equals(fromDate) && "".equals(toDate)) || (fromDate == null) || (toDate == null)) {
			return projectRepo;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Project getById(long id) throws TimecastNotFoundException, TimecastInternalServerErrorException, TimecastForbiddenException {
		if (projectRepo.stream().anyMatch(p -> p.getId() == id)) {
			return projectRepo.stream().filter(p -> p.getId() == id).findFirst().get();
		} else {
			throw new TimecastNotFoundException("Project not found");
		}
	}

	@Override
	public Project create(Project newProject) throws TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		newProject.setId(nextProjectId++);
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
	
	private List<Project> generateProjects() {
		Project project1 = new Project();
		project1.setId(nextProjectId++);
		project1.setName("Project1");
		project1.setFtePercentage(100);
		project1.setStartDate("2019-03-16");
		project1.setEndDate("2019-10-10");
		project1.setProjectManagerId(0);
		
		Project project2 = new Project();
		project2.setId(nextProjectId++);
		project2.setName("Project2");
		project2.setFtePercentage(200);
		project2.setStartDate("2018-03-16");
		project2.setEndDate("2018-10-10");
		project2.setProjectManagerId(0);
		
		Project project3 = new Project();
		project3.setId(nextProjectId++);
		project3.setName("Project3");
		project3.setFtePercentage(300);
		project3.setStartDate("2020-03-16");
		project3.setEndDate("2020-10-10");
		project3.setProjectManagerId(0);
		
		List<Project> projects = new ArrayList();
		projects.add(project1);
		projects.add(project2);
		projects.add(project3);
		
		return projects;
	}

}
