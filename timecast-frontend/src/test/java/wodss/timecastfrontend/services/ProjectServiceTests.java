package wodss.timecastfrontend.services;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import org.junit.Assert;
import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;

@RunWith(SpringRunner.class)
public class ProjectServiceTests {
	
	@Mock
	private RestTemplate restTemplateMock;
	
	private ProjectService projectService;
	
	//TODO replace by dynamic url
	private String url = null;
	
	private List<Project> projects;

	
	@Before
	public void setUp() {
		Mockito.reset(restTemplateMock);
		projects = generateProjects();
		
		projectService = new ProjectService(restTemplateMock);
	}
	
	@Test
	public void getAllProjectsTest() throws TimecastNotFoundException, TimecastInternalServerErrorException {
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){}))
		.thenReturn(new ResponseEntity(projects, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects();
		
		verify(restTemplateMock, times(1)).exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){});
		
		Assert.assertEquals(projects, fetchedProjects);
		
	}
	
	@Test
	public void getAllProjectsByFromDateTest() throws ParseException, TimecastNotFoundException, TimecastInternalServerErrorException {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("fromDate", "2019-03-16");
		
		List<Project> returnProjects = new ArrayList<>();
		returnProjects.add(projects.get(0));
		returnProjects.add(projects.get(2));
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){}, uriVar))
		.thenReturn(new ResponseEntity(returnProjects, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects("2019-03-16", null);
		
		verify(restTemplateMock, times(1)).exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){}, uriVar);
		
		Assert.assertEquals(returnProjects, fetchedProjects);
		
	}
	
	@Test
	public void getAllProjectsByToDateTest() throws ParseException, TimecastNotFoundException, TimecastInternalServerErrorException {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("toDate", "2020-01-01");
		
		List<Project> returnProjects = new ArrayList<>();
		returnProjects.add(projects.get(0));
		returnProjects.add(projects.get(1));
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){}, uriVar))
		.thenReturn(new ResponseEntity(returnProjects, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects(null, "2020-01-01");
		
		verify(restTemplateMock, times(1)).exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){}, uriVar);
		
		Assert.assertEquals(returnProjects, fetchedProjects);
		
	}
	
	@Test
	public void getAllProjectsByFromAndToDateTest() throws ParseException, TimecastNotFoundException, TimecastInternalServerErrorException {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("fromDate", "2019-01-01");
		uriVar.put("toDate", "2020-01-01");
		
		List<Project> returnProjects = new ArrayList<>();
		returnProjects.add(projects.get(0));
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){}, uriVar))
		.thenReturn(new ResponseEntity(returnProjects, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects("2019-01-01", "2020-01-01");
		
		verify(restTemplateMock, times(1)).exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){}, uriVar);
		
		Assert.assertEquals(returnProjects, fetchedProjects);
		
	}
	
	@Test
	public void getProjectByIdTest() throws TimecastNotFoundException, TimecastInternalServerErrorException, TimecastForbiddenException {
		Mockito.when(restTemplateMock.getForEntity(url + "/1" , Project.class)).thenReturn(new ResponseEntity<Project>(projects.get(0), HttpStatus.OK));
		
		Project fetchedProject = projectService.getProject(1);
		
		verify(restTemplateMock, times(1)).getForEntity(url + "/1", Project.class);
		
		Assert.assertEquals(projects.get(0), fetchedProject);
	}
	
	@Test
	public void createProjectTest() throws TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		Project newProject = new Project();
		newProject.setId(99);
		newProject.setName("ProjectNew");
		newProject.setFtePercentage(100);
		newProject.setStartDate("2019-03-16");
		newProject.setEndDate("2019-10-10");
		newProject.setProjectManagerId(0);
		Mockito.when(restTemplateMock.postForEntity(url, newProject, Project.class)).thenReturn(new ResponseEntity<Project>(newProject, HttpStatus.OK));
		
		Project createdProject = projectService.createProject(newProject);
		
		verify(restTemplateMock, times(1)).postForEntity(url, newProject, Project.class);
		
		Assert.assertEquals(newProject, createdProject);
	}
	
	@Test
	public void updateProjectTest() throws TimecastNotFoundException, TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		Project updatedProject = projects.get(0);
		updatedProject.setName("ProjectnameNew");
		HttpEntity<Project> requestEntity = new HttpEntity<Project>(updatedProject);
		
		Mockito.when(restTemplateMock.exchange(url + "/1", HttpMethod.PUT, requestEntity, Project.class)).thenReturn(new ResponseEntity<Project>(updatedProject, HttpStatus.OK));
		
		Project responseProject = projectService.updateProject(updatedProject.getId(), updatedProject);
		
		verify(restTemplateMock, times(1)).exchange(url+ "/1", HttpMethod.PUT, requestEntity, Project.class);
		
		Assert.assertEquals(updatedProject, responseProject);
	}
	
	@Test
	public void deleteProjectTest() throws TimecastInternalServerErrorException, TimecastForbiddenException, TimecastNotFoundException {
			
		Mockito.when(restTemplateMock.exchange(url + "/1", HttpMethod.DELETE,
				null, Void.class)).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
		
		projectService.deleteProject(1);
		
		verify(restTemplateMock, times(1)).exchange(url + "/1", HttpMethod.DELETE, null, Void.class);
		
	}
	
	
	private List<Project> generateProjects() {
		Project project1 = new Project();
		project1.setId(1);
		project1.setName("Project1");
		project1.setFtePercentage(100);
		project1.setStartDate("2019-03-16");
		project1.setEndDate("2019-10-10");
		project1.setProjectManagerId(0);
		
		Project project2 = new Project();
		project2.setId(1);
		project2.setName("Project2");
		project2.setFtePercentage(200);
		project2.setStartDate("2018-03-16");
		project2.setEndDate("2018-10-10");
		project2.setProjectManagerId(0);
		
		Project project3 = new Project();
		project3.setId(1);
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
