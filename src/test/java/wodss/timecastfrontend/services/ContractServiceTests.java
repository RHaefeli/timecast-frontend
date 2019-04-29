package wodss.timecastfrontend.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.dto.ContractDto;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;

@RunWith(SpringRunner.class)
public class ContractServiceTests {

	@Mock
	EmployeeService employeeServiceMock;
	
	@Mock
	private RestTemplate restTemplateMock;
	
	private ContractService contractService;

	private String url = "url";
	private Token token = new Token("any String");
	
	private List<ContractDto> contractDtos;
	private List<Contract> contracts;
	Employee e1;
	
	@Before
	public void setUp() {
		
		Mockito.reset(restTemplateMock);
		
		e1 = new Employee();
		e1.setId(1);
		e1.setActive(true);
		e1.setEmailAddress("emplyee1@mail.ch");
		e1.setFirstName("Employee");
		e1.setLastName("One");
		e1.setRole(Role.DEVELOPER);
		
		Mockito.when(employeeServiceMock.getById(token, 1)).thenReturn(e1);
		
		generateContracts(e1);
		
		contractService = new ContractService(restTemplateMock, url, employeeServiceMock);
	}
	
	@Test
	public void getContractTest() {
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(contractDtos, HttpStatus.OK));
		
		List<Contract> fetchedContracts = contractService.getAll(token);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(contracts, fetchedContracts);
	}
	
	@Test
	public void getContractByEmployeeTest() {
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(contractDtos, HttpStatus.OK));
		
		List<Contract> fetchedContracts = contractService.getByEmployee(token, e1);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(contracts, fetchedContracts);
	}
	
	@Test
	public void getNoContractByEmployeeTest() {
		Mockito.when(restTemplateMock.exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class)))
		.thenReturn(new ResponseEntity(new ArrayList<ContractDto>(), HttpStatus.OK));
		
		List<Contract> fetchedContracts = contractService.getByEmployee(token, new Employee());
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(url), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertTrue(fetchedContracts.isEmpty());
	}
	
	@Test
	public void getCurrentContractsTest() {
		Mockito.when(restTemplateMock.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.any(ParameterizedTypeReference.class), Mockito.anyMap()))
		.thenReturn(new ResponseEntity(contractDtos, HttpStatus.OK));
		
		List<Contract> fetchedContracts = contractService.getCurrentContracts(token);
		
		Assert.assertEquals(contracts, fetchedContracts);
	}
	
	@Test
	public void mapEntityToDtoTest() {
		ContractDto cDto = contractService.mapEntityToDto(token, contracts.get(0));
		
		Assert.assertEquals(contractDtos.get(0), cDto);
	}
	
	private void generateContracts(Employee e) {
		contractDtos = new ArrayList<ContractDto>();
		contracts = new ArrayList<Contract>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = normalizeDate(new Date());
		
		
		ContractDto cdto1 = new ContractDto();
		cdto1.setId(1);
		cdto1.setStartDate(sdf.format(startDate.getTime()-1L*24*60*60*1000));
		cdto1.setEndDate(sdf.format(new Date(startDate.getTime()+10*24*60*60*1000)));
		cdto1.setPensumPercentage(100);
		cdto1.setEmployeeId(e.getId());
		
		Contract c1 = new Contract();
		c1.setId(1);
		c1.setStartDate(new Date(startDate.getTime()-1L*24*60*60*1000));
		c1.setEndDate(new Date(startDate.getTime()+10L*24*60*60*1000));
		c1.setPensumPercentage(100);
		c1.setEmployee(e);
		
		ContractDto cdto2 = new ContractDto();
		cdto2.setId(2);
		cdto2.setStartDate(sdf.format(new Date(startDate.getTime()+11L*24*60*60*1000)));
		cdto2.setEndDate(sdf.format(new Date(startDate.getTime()+20L*24*60*60*1000)));
		cdto2.setPensumPercentage(50);
		cdto2.setEmployeeId(e.getId());
		
		Contract c2 = new Contract();
		c2.setId(2);
		c2.setStartDate(new Date(startDate.getTime()+11L*24*60*60*1000));
		c2.setEndDate(new Date(startDate.getTime()+20L*24*60*60*1000));
		c2.setPensumPercentage(50);
		c2.setEmployee(e);
		
		contractDtos.add(cdto1);
		contractDtos.add(cdto2);
		contracts.add(c1);
		contracts.add(c2);
		
	}
	
	private Date normalizeDate(Date date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd");
			return formatter.parse(formatter.format(date));
		} catch (ParseException e) {
			throw new TimecastInternalServerErrorException(e.getMessage());
		}
	}
}
