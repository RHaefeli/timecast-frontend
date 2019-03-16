package wodss.timecastfrontend.services;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import org.junit.Assert;
import wodss.timecastfrontend.domain.Project;

@RunWith(SpringRunner.class)
public class ProjectServiceTests {
	
	@Mock
	private RestTemplate restTemplateMock;
	
	@InjectMocks
	private ProjectService projectService = new ProjectService();
	
	//TODO replace by dynamic url
	private String url = "";
	
	private List<Project> projects;
	
	@Before
	private void setUp() {
		Mockito.reset(restTemplateMock);
		List<Project> projects = generateProjects();
	}
	
	@Test
	public void getAllProjects() {
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){}))
		.thenReturn(new ResponseEntity(projects, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects();
		
		verify(restTemplateMock, atLeastOnce()).exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Project>>(){});
		
		Assert.assertEquals(projects, fetchedProjects);
		
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
