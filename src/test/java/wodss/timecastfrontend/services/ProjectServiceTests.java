package wodss.timecastfrontend.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.dto.ProjectDto;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;

@RunWith(SpringRunner.class)
public class ProjectServiceTests {

	@Mock
	private RestTemplate restTemplateMock;
	@Mock
    private EmployeeService employeeServiceMock;
	
	private ProjectService projectService;

	private String url = "url";
	private Token token = new Token("any String");
	
	private List<ProjectDto> projectsDtos;

	
	@Before
	public void setUp() {
		Mockito.reset(restTemplateMock);
		projectsDtos = generateProjects();

		// Always return the same projectManager
		Employee projectManager = new Employee();
		projectManager.setId(0);
        Mockito.when(employeeServiceMock.getById(token, 0)).thenReturn(projectManager);

		projectService = new ProjectService(restTemplateMock, url, employeeServiceMock);
	}
	
	@Test
	public void getAllProjectsTest() {
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(projectsDtos, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getAll(token);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(projectsDtos, mapProjectsToDtos(fetchedProjects));
	}
	
	@Test
	public void getAllProjectsByFromDateTest() {
		String paramUrl = url + "?fromDate=2019-03-16&";
		List<ProjectDto> returnProjects = new ArrayList<>();
		returnProjects.add(projectsDtos.get(0));
		returnProjects.add(projectsDtos.get(2));
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
				Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(returnProjects, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects(token,"2019-03-16", null);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(returnProjects, mapProjectsToDtos(fetchedProjects));
		
	}
	
	@Test
	public void getAllProjectsByToDateTest() {
		String paramUrl = url + "?toDate=2020-01-01";
		List<ProjectDto> returnProjects = new ArrayList<>();
		returnProjects.add(projectsDtos.get(0));
		returnProjects.add(projectsDtos.get(1));
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(returnProjects, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects(token,null, "2020-01-01");
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(returnProjects, mapProjectsToDtos(fetchedProjects));
	}
	
	@Test
	public void getAllProjectsByFromAndToDateTest() {
		String paramUrl = url + "?fromDate=2019-01-01&toDate=2020-01-01";
		List<ProjectDto> returnProjects = new ArrayList<>();
		returnProjects.add(projectsDtos.get(0));
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(returnProjects, HttpStatus.OK));
		
		List<Project> fetchedProjects = projectService.getProjects(token,"2019-01-01", "2020-01-01");
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(returnProjects, mapProjectsToDtos(fetchedProjects));
	}
	
	@Test
	public void getProjectByIdTest() {
		List<ProjectDto> returnProjects = new ArrayList<>();
		returnProjects.add(projectsDtos.get(0));

		Mockito.when(restTemplateMock.exchange(Mockito.eq(url + "/1"), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class)))
				.thenReturn(new ResponseEntity(returnProjects.get(0), HttpStatus.OK));
		
		Project fetchedProject = projectService.getById(token, 1);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url + "/1"), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class));
		
		Assert.assertEquals(projectsDtos.get(0), projectService.mapEntityToDto(token, fetchedProject));
	}
	
	@Test
	public void createProjectTest() throws ParseException {
        DateFormat dtoFormat = new SimpleDateFormat("yyyy-MM-dd");
        Employee projectManager = new Employee();
        projectManager.setId(0);
	    Project newProject = new Project();
		newProject.setId(99);
		newProject.setName("ProjectNew");
		newProject.setFtePercentage(100);
        newProject.setStartDate(dtoFormat.parse("2019-03-16"));
        newProject.setEndDate(dtoFormat.parse("2019-10-10"));
        newProject.setProjectManager(projectManager);

		ProjectDto returnProject = projectService.mapEntityToDto(token, newProject);
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.POST),
				Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class)))
                .thenReturn(new ResponseEntity<>(returnProject, HttpStatus.CREATED));

		Project createdProject = projectService.create(token, newProject);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.POST),
				Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class));
		
		Assert.assertEquals(newProject, createdProject);
	}
	
	@Test
	public void updateProjectTest() throws ParseException {
		ProjectDto updatedProjectDto = projectsDtos.get(0);
		updatedProjectDto.setName("ProjectnameNew");
        DateFormat dtoFormat = new SimpleDateFormat("yyyy-MM-dd");
        Employee projectManager = new Employee();
        projectManager.setId(updatedProjectDto.getProjectManagerId());
        Project updatedProject = new Project();
        updatedProject.setId(updatedProjectDto.getId());
        updatedProject.setName(updatedProjectDto.getName());
        updatedProject.setFtePercentage(updatedProjectDto.getFtePercentage());
        updatedProject.setStartDate(dtoFormat.parse(updatedProjectDto.getStartDate()));
        updatedProject.setEndDate(dtoFormat.parse(updatedProjectDto.getEndDate()));
        updatedProject.setProjectManager(projectManager);

		Mockito.when(restTemplateMock.exchange(Mockito.eq(url + "/1"), Mockito.eq(HttpMethod.PUT),
				Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class))
        ).thenReturn(new ResponseEntity<>(updatedProjectDto, HttpStatus.OK));
		
		Project responseProject = projectService.update(token, updatedProject);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url + "/1"), Mockito.eq(HttpMethod.PUT),
				Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class));
		Assert.assertEquals(updatedProject, responseProject);
	}
	
	@Test
	public void deleteProjectTest() throws TimecastInternalServerErrorException, TimecastForbiddenException, TimecastNotFoundException {
					
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url + "/1"), Mockito.eq(HttpMethod.DELETE),
				Mockito.any(HttpEntity.class), Mockito.eq(Void.class)))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT));
		
		projectService.deleteById(token, 1);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url + "/1"), Mockito.eq(HttpMethod.DELETE),
				Mockito.any(HttpEntity.class), Mockito.eq(Void.class));
	}
	
	
	private List<ProjectDto> generateProjects() {
		ProjectDto project1 = new ProjectDto();
		project1.setId(1);
		project1.setName("Project1");
		project1.setFtePercentage(100);
		project1.setStartDate("2019-03-16");
		project1.setEndDate("2019-10-10");
		project1.setProjectManagerId(0);

		ProjectDto project2 = new ProjectDto();
		project2.setId(1);
		project2.setName("Project2");
		project2.setFtePercentage(200);
		project2.setStartDate("2018-03-16");
		project2.setEndDate("2018-10-10");
		project2.setProjectManagerId(0);

		ProjectDto project3 = new ProjectDto();
		project3.setId(1);
		project3.setName("Project3");
		project3.setFtePercentage(300);
		project3.setStartDate("2020-03-16");
		project3.setEndDate("2020-10-10");
		project3.setProjectManagerId(0);
		
		List<ProjectDto> projects = new ArrayList();
		projects.add(project1);
		projects.add(project2);
		projects.add(project3);
		
		return projects;
	}

	private List<ProjectDto> mapProjectsToDtos(List<Project> projects) {
		return projects.stream()
				.map(project -> projectService.mapEntityToDto(token, project))
				.collect(Collectors.toList());
	}
}
