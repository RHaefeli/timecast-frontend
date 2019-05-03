package wodss.timecastfrontend.services;

import org.junit.Assert;
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
import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.dto.EmployeeDto;
import wodss.timecastfrontend.dto.ProjectDto;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class EmployeeServiceTests {

	@Mock
	private RestTemplate restTemplateMock;

    private EmployeeService employeeService;


	private String url = "https://localhost/employees";
	private Token token = new Token("any String");
	
	private List<EmployeeDto> employeeDtos;

	
	@Before
	public void setUp() {
		Mockito.reset(restTemplateMock);
		employeeDtos = generateEmployees();

		employeeService = new EmployeeService(restTemplateMock, url);
	}
	
	@Test
	public void getAllEmployeesTest() {
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(employeeDtos, HttpStatus.OK));
		
		List<Employee> fetchedEmployees = employeeService.getAll(token);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class));

		Assert.assertNotNull(fetchedEmployees);
		Assert.assertEquals(employeeDtos.size(), fetchedEmployees.size());
		for (int i = 0; i < fetchedEmployees.size(); i++) {
			validateEmployee(employeeDtos.get(i), fetchedEmployees.get(i));
		}
	}

	@Test
	public void getEmployeeByIdTest() {
		EmployeeDto returnEmployee = employeeDtos.get(2);

		Mockito.when(restTemplateMock.exchange(Mockito.eq(url + "/3"), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.eq(EmployeeDto.class)))
				.thenReturn(new ResponseEntity(returnEmployee, HttpStatus.OK));


		Employee fetchedEmployee = employeeService.getById(token, 3);

		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url + "/3"),
				Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),  Mockito.eq(EmployeeDto.class));

		Assert.assertNotNull(fetchedEmployee);
		validateEmployee(employeeDtos.get(2), fetchedEmployee);
	}

	@Test
	public void createEmployeeTest() {
		EmployeeLogin newEmployee = new EmployeeLogin(); 	EmployeeDto newEmployeeDto = new EmployeeDto();
		newEmployee.setId(99);								newEmployeeDto.setId(99);
		newEmployee.setFirstName("Tobias");					newEmployeeDto.setFirstName("Tobias");
		newEmployee.setLastName("Bucher");					newEmployeeDto.setLastName("Bucher");
		newEmployee.setRole(Role.DEVELOPER);				newEmployeeDto.setRole("DEVELOPER");
		newEmployee.setActive(true);						newEmployeeDto.setActive(true);
		newEmployee.setPassword("secret");
		newEmployee.setConfirmPassword("secret");

		String paramUrl = url + "?password=secret&role=DEVELOPER";

		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.POST),
				Mockito.any(HttpEntity.class), Mockito.eq(EmployeeDto.class)))
				.thenReturn(new ResponseEntity(newEmployeeDto, HttpStatus.CREATED));

		Employee fetchedEmployee = employeeService.create(token, newEmployee);

		verify(restTemplateMock, times(1)).exchange(Mockito.eq(paramUrl),
				Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class), Mockito.eq(EmployeeDto.class));

		Assert.assertNotNull(fetchedEmployee);
		validateEmployee(newEmployeeDto, fetchedEmployee);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void createEmployeeUnsupportedMethodTest() {
		Employee newEmployee = new Employee();
		newEmployee.setId(99);
		newEmployee.setFirstName("Tobias");
		newEmployee.setLastName("Bucher");
		newEmployee.setRole(Role.DEVELOPER);
		newEmployee.setActive(true);

		Employee fetchedEmployee = employeeService.create(token, newEmployee);
		Assert.fail();
	}

	public void updateEmployeeTest() {
		EmployeeDto updatedEmployeeDto = employeeDtos.get(2);
		updatedEmployeeDto.setFirstName("Markus");

		Employee updatedEmployee = new Employee();
		updatedEmployee.setId(updatedEmployeeDto.getId());
		updatedEmployee.setEmailAddress(updatedEmployeeDto.getEmailAddress());
		updatedEmployee.setFirstName(updatedEmployeeDto.getFirstName());
		updatedEmployee.setLastName(updatedEmployeeDto.getLastName());
		updatedEmployee.setActive(updatedEmployeeDto.isActive());
		updatedEmployee.setRole(Role.valueOf(updatedEmployeeDto.getRole()));

		Mockito.when(restTemplateMock.exchange(Mockito.eq(url + "/3"), Mockito.eq(HttpMethod.PUT),
				Mockito.any(HttpEntity.class), Mockito.eq(EmployeeDto.class)))
				.thenReturn(new ResponseEntity<>(updatedEmployeeDto, HttpStatus.OK));

		Employee responseEmployee = employeeService.update(token, updatedEmployee);

		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url + "/1"),
				Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class), Mockito.eq(ProjectDto.class));

		Assert.assertNotNull(responseEmployee);
		validateEmployee(updatedEmployeeDto, responseEmployee);
	}

	public void deleteEmployeeTest() {
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url + "/2"), Mockito.eq(HttpMethod.DELETE),
				Mockito.any(HttpEntity.class), Mockito.eq(Void.class)))
				.thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT));

		employeeService.deleteById(token, 2);

		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url + "/2"),
				Mockito.eq(HttpMethod.DELETE), Mockito.any(HttpEntity.class), Mockito.eq(Void.class));
	}

	
	private List<EmployeeDto> generateEmployees() {
		EmployeeDto emp1 = new EmployeeDto();
		emp1.setId(1);
		emp1.setLastName("Müller");
		emp1.setFirstName("Kurt");
		emp1.setActive(true);
		emp1.setEmailAddress("k.mueller@mail.com");
		emp1.setRole("ADMINISTRATOR");

		EmployeeDto emp2 = new EmployeeDto();
		emp2.setId(2);
		emp2.setLastName("Meier");
		emp2.setFirstName("Jonathan");
		emp2.setActive(true);
		emp2.setEmailAddress("j.meier@mail.com");
		emp2.setRole("DEVELOPER");

		EmployeeDto emp3 = new EmployeeDto();
		emp3.setId(3);
		emp3.setLastName("Brösmeli");
		emp3.setFirstName("Guschdi");
		emp3.setActive(true);
		emp3.setEmailAddress("g.broesmeli@mail.com");
		emp3.setRole("PROJECTMANAGER");

		EmployeeDto emp4 = new EmployeeDto();
		emp4.setId(4);
		emp4.setLastName("Heftig");
		emp4.setFirstName("Dud");
		emp4.setActive(true);
		emp4.setEmailAddress("g.Dudsmeli@mail.com");
		emp4.setRole("PROJECTMANAGER");

		List<EmployeeDto> employees = new ArrayList();
		employees.add(emp1);
		employees.add(emp2);
		employees.add(emp3);
		employees.add(emp4);

		return employees;
	}

	private void validateEmployee(EmployeeDto employeeDto, Employee employee) {
		Assert.assertEquals(employeeDto.getId(), employee.getId());
		Assert.assertEquals(employeeDto.getEmailAddress(), employee.getEmailAddress());
		Assert.assertEquals(employeeDto.getFirstName(), employee.getFirstName());
		Assert.assertEquals(employeeDto.getLastName(), employee.getLastName());
		Assert.assertEquals(employeeDto.getRole(), employee.getRole().getValue());
	}

	private List<EmployeeDto> mapEmployeesToDtos(List<Employee> employees) {
		return employees.stream()
				.map(employee -> employeeService.mapEntityToDto(token, employee))
				.collect(Collectors.toList());
	}
}
