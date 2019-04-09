package wodss.timecastfrontend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class ProjectServiceFake extends ProjectService {
	
	private List<Project> projectRepo;
	private int nextProjectId = 0;

	public ProjectServiceFake(RestTemplate restTemplate) {
		super(restTemplate);
		projectRepo = generateProjects();
	}
	
	@Override
	public List<Project> getProjects() throws TimecastNotFoundException, TimecastInternalServerErrorException {
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
	public Project getProject(int id) throws TimecastNotFoundException, TimecastInternalServerErrorException, TimecastForbiddenException {
		if (projectRepo.stream().anyMatch(p -> p.getId() == id)) {
			return projectRepo.stream().filter(p -> p.getId() == id).findFirst().get();
		} else {
			throw new TimecastNotFoundException("Project not found");
		}
	}

	@Override
	public Project createProject(Project newProject) throws TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		newProject.setId(nextProjectId++);
		projectRepo.add(newProject);
		return newProject;
	}

	@Override
	public Project updateProject(int id, Project updatedProject) throws TimecastNotFoundException, TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		if (projectRepo.stream().anyMatch(p -> p.getId() == id)) {
			Project oldProject = projectRepo.stream().filter(p -> p.getId() == id).findFirst().get();
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
	public void deleteProject(int id) throws TimecastInternalServerErrorException, TimecastForbiddenException, TimecastNotFoundException {
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
