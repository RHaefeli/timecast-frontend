package wodss.timecastfrontend.services;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Project;

@Component
public class ProjectService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	private final RestTemplate restTemplate;
	
	@Autowired
	public ProjectService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public List<Project> getProjects() {
		throw new UnsupportedOperationException();
	}
	
	public List<Project> getProjects(Date fromDate, Date toDate) {
		throw new UnsupportedOperationException();
	}
	
	public Project getProject(int id) {
		throw new UnsupportedOperationException();
	}
	
	public Project createProject(Project newProject) {
		throw new UnsupportedOperationException();
	}
	
	public Project updateProject(int id, Project updatedProject) {
		throw new UnsupportedOperationException();
	}
	
	public void deleteProject(int id) {
		throw new UnsupportedOperationException();
	}
	
	

}
