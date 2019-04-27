package wodss.timecastfrontend.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import wodss.timecastfrontend.dto.ProjectDto;
import wodss.timecastfrontend.domain.Token;
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
	private String url = "url";
	private Token token = new Token("any String");
	
	private List<ProjectDTO> projects;

	
	@Before
	public void setUp() {
		Mockito.reset(restTemplateMock);
		projects = generateProjects();
		
		projectService = new ProjectService(restTemplateMock, url);
	}
	
	@Test
	public void getAllProjectsTest() throws TimecastNotFoundException, TimecastInternalServerErrorException {
		List<ProjectDto> projectDtos = mapProjectToDtos(projects);
		System.out.println(projectDtos);
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(projectDtos, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getAll(token);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(projects, fetchedProjects);
	}
	
	@Test
	public void getAllProjectsByFromDateTest() {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("fromDate", "2019-03-16");
		
		List<ProjectDTO> returnProjects = new ArrayList<>();
		returnProjects.add(projects.get(0));
		returnProjects.add(projects.get(2));
		List<ProjectDto> returnProjectDtos = mapProjectToDtos(returnProjects);
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
				Mockito.any(ParameterizedTypeReference.class), Mockito.eq(uriVar)))
		.thenReturn(new ResponseEntity(returnProjectDtos, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects(token,"2019-03-16", null);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class), Mockito.eq(uriVar));
		
		Assert.assertEquals(returnProjects, fetchedProjects);
		
	}
	
	@Test
	public void getAllProjectsByToDateTest() throws ParseException, TimecastNotFoundException, TimecastInternalServerErrorException {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("toDate", "2020-01-01");
		
		List<ProjectDTO> returnProjects = new ArrayList<>();
		returnProjects.add(projects.get(0));
		returnProjects.add(projects.get(1));
		List<ProjectDto> returnProjectDtos = mapProjectToDtos(returnProjects);
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class), Mockito.eq(uriVar)))
		.thenReturn(new ResponseEntity(returnProjectDtos, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects(token,null, "2020-01-01");
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class), Mockito.eq(uriVar));
		
		Assert.assertEquals(returnProjects, fetchedProjects);
	}
	
	@Test
	public void getAllProjectsByFromAndToDateTest() throws ParseException, TimecastNotFoundException, TimecastInternalServerErrorException {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("fromDate", "2019-01-01");
		uriVar.put("toDate", "2020-01-01");
		
		List<ProjectDTO> returnProjects = new ArrayList<>();
		returnProjects.add(projects.get(0));
		List<ProjectDto> returnProjectDtos = mapProjectToDtos(returnProjects);
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class), Mockito.eq(uriVar)))
		.thenReturn(new ResponseEntity(returnProjectDtos, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects(token,"2019-01-01", "2020-01-01");
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class), Mockito.eq(uriVar));
		
		Assert.assertEquals(returnProjects, fetchedProjects);
	}
	
	@Test
	public void getProjectByIdTest() throws TimecastNotFoundException, TimecastInternalServerErrorException, TimecastForbiddenException {
		List<Project> returnProjects = new ArrayList<>();
		returnProjects.add(projects.get(0));
		List<ProjectDto> returnProjectDtos = mapProjectToDtos(returnProjects);

		Mockito.when(restTemplateMock.exchange(Mockito.eq(url + "/1"), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class)))
				.thenReturn(new ResponseEntity(returnProjectDtos.get(0), HttpStatus.OK));
		
		Project fetchedProject = projectService.getById(token, 1);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url + "/1"), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class));
		
		Assert.assertEquals(projects.get(0), fetchedProject);
	}
	
	@Test
	public void createProjectTest() throws TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		ProjectDTO newProject = new ProjectDTO();
		newProject.setId(99);
		newProject.setName("ProjectNew");
		newProject.setFtePercentage(100);
		newProject.setStartDate("2019-03-16");
		newProject.setEndDate("2019-10-10");
		newProject.setProjectManagerId(0);
		HttpEntity<ProjectDTO> request = new HttpEntity<>(newProject);
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.POST, request, ProjectDTO.class)).thenReturn(new ResponseEntity<ProjectDTO>(newProject, HttpStatus.CREATED));
		
		Project createdProject = projectService.create(token, newProject);
		
		verify(restTemplateMock, times(1)).exchange(url, HttpMethod.POST, request, ProjectDTO.class);
		
		Assert.assertEquals(newProject, createdProject);
	}
	
	@Test
	public void updateProjectTest() throws TimecastNotFoundException, TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		ProjectDTO updatedProject = projects.get(0);
		updatedProject.setName("ProjectnameNew");
		HttpEntity<ProjectDTO> requestEntity = new HttpEntity<ProjectDTO>(updatedProject);
		
		Mockito.when(restTemplateMock.exchange(url + "/1", HttpMethod.PUT, requestEntity, ProjectDTO.class)).thenReturn(new ResponseEntity<ProjectDTO>(updatedProject, HttpStatus.OK));
		
		Project responseProject = projectService.update(token, updatedProject);
		
		verify(restTemplateMock, times(1)).exchange(url+ "/1", HttpMethod.PUT, requestEntity, ProjectDTO.class);
		
		Assert.assertEquals(updatedProject, responseProject);
	}
	
	@Test
	public void deleteProjectTest() throws TimecastInternalServerErrorException, TimecastForbiddenException, TimecastNotFoundException {
					
		Mockito.when(restTemplateMock.exchange(url + "/1", HttpMethod.DELETE,
		null, Void.class)).thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT));
		
		projectService.deleteById(token, 1);
		
		verify(restTemplateMock, times(1)).exchange(url + "/1", HttpMethod.DELETE, null, Void.class);
		
	}
	
	
	private List<ProjectDTO> generateProjects() {
		ProjectDTO project1 = new ProjectDTO();
		project1.setId(1);
		project1.setName("Project1");
		project1.setFtePercentage(100);
		project1.setStartDate("2019-03-16");
		project1.setEndDate("2019-10-10");
		project1.setProjectManagerId(0);
		
		ProjectDTO project2 = new ProjectDTO();
		project2.setId(1);
		project2.setName("Project2");
		project2.setFtePercentage(200);
		project2.setStartDate("2018-03-16");
		project2.setEndDate("2018-10-10");
		project2.setProjectManagerId(0);
		
		ProjectDTO project3 = new ProjectDTO();
		project3.setId(1);
		project3.setName("Project3");
		project3.setFtePercentage(300);
		project3.setStartDate("2020-03-16");
		project3.setEndDate("2020-10-10");
		project3.setProjectManagerId(0);
		
		List<ProjectDTO> projects = new ArrayList();
		projects.add(project1);
		projects.add(project2);
		projects.add(project3);
		
		return projects;
	}

	private List<ProjectDto> mapProjectToDtos(List<Project> projects) {
		return projects.stream()
				.map(project -> projectService.mapEntityToDto(token, project))
				.collect(Collectors.toList());
	}
}
