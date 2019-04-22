package wodss.timecastfrontend.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import org.junit.Assert;

import wodss.timecastfrontend.domain.dto.AllocationDTO;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;

@RunWith(SpringRunner.class)
public class AllocationServiceTests {
	
	@Mock
	private RestTemplate restTemplateMock;
	
	private AllocationService allocationService;
	
	//TODO replace by dynamic url
	private String url = null;
	
	private List<AllocationDTO> allocations;

	
	@Before
	public void setUp() {
		Mockito.reset(restTemplateMock);
		allocations = generateAllocations();
		
		allocationService = new AllocationService(restTemplateMock, url);
	}
	
	@Test
	public void getAllAllocationsByEmployeeId() {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("employeeId", "2");
		
		List<AllocationDTO> returnAllocations = new ArrayList<>();
		returnAllocations.add(allocations.get(2));
		
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar))
		.thenReturn(new ResponseEntity(returnAllocations, HttpStatus.OK));
		
		List<AllocationDTO> fetchedAllocations = allocationService.getAllocations(2, -1, null, null);
		
		verify(restTemplateMock, times(1)).exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar);
		
		Assert.assertEquals(returnAllocations, fetchedAllocations);
		
	}
	
	@Test
	public void getAllAllocationsByProjectId() {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("projectId", "2");
		
		List<AllocationDTO> returnAllocations = new ArrayList<>();
		returnAllocations.add(allocations.get(1));
		returnAllocations.add(allocations.get(2));
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar))
		.thenReturn(new ResponseEntity(returnAllocations, HttpStatus.OK));
		
		List<AllocationDTO> fetchedAllocations = allocationService.getAllocations(-1, 2, null, null);
		
		verify(restTemplateMock, times(1)).exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar);
		
		Assert.assertEquals(returnAllocations, fetchedAllocations);
	}
	
	@Test
	public void getAllAllocationsByEmployeeIdProjectId() {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("employeeId", "2");
		uriVar.put("projectId", "2");
		
		List<AllocationDTO> returnAllocations = new ArrayList<>();
		returnAllocations.add(allocations.get(1));
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar))
		.thenReturn(new ResponseEntity(returnAllocations, HttpStatus.OK));
		
		List<AllocationDTO> fetchedAllocations = allocationService.getAllocations(2, 2, null, null);
		
		verify(restTemplateMock, times(1)).exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar);
		
		Assert.assertEquals(returnAllocations, fetchedAllocations);
		
	}
	
	@Test(expected=TimecastNotFoundException.class)
	public void getAllAllocationsNotFound() {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("employeeId", "10");
		
		List<AllocationDTO> returnAllocations = new ArrayList<>();
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar))
		.thenReturn(new ResponseEntity(HttpStatus.NOT_FOUND));
		
		allocationService.getAllocations(10, -1, null, null);
	}
	
	@Test(expected=TimecastForbiddenException.class)
	public void getAllAllocationsForbidden() {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("employeeId", "1");
		
		List<AllocationDTO> returnAllocations = new ArrayList<>();
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar))
		.thenReturn(new ResponseEntity(HttpStatus.FORBIDDEN));
		
		allocationService.getAllocations(1, -1, null, null);
	}
	
	@Test(expected=TimecastInternalServerErrorException.class)
	public void getAllAllocationsInternalServerError() {
		Map<String, String> uriVar = new HashMap<>();
		uriVar.put("employeeId", "1");
		
		List<AllocationDTO> returnAllocations = new ArrayList<>();
		
		Mockito.when(restTemplateMock.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<AllocationDTO>>(){}, uriVar))
		.thenReturn(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));
		
		allocationService.getAllocations(1, -1, null, null);
	}
	
	
	private List<AllocationDTO> generateAllocations() {
		
		//Employee: 1; 2019-01-01 - 2019-05-05; 50%; Project: 1
		AllocationDTO allocation1 = new AllocationDTO();
		allocation1.setContractId(1);
		allocation1.setStartDate("2019-01-01");
		allocation1.setEndDate("2019-05-05");
		allocation1.setPensumPercentage(50);
		allocation1.setProjectId(1);
		allocation1.setId(1);
		
		//Employee: 1; 2019-03-01 - 2019-05-05; 50%; Project: 2
		AllocationDTO allocation2 = new AllocationDTO();
		allocation2.setContractId(1);
		allocation2.setStartDate("2019-03-01");
		allocation2.setEndDate("2019-05-05");
		allocation2.setPensumPercentage(50);
		allocation2.setProjectId(2);
		allocation2.setId(2);
		
		//Employee: 2; 2019-01-01 - 2019-05-05; 50%; Project: 2
		AllocationDTO allocation3 = new AllocationDTO();
		allocation3.setContractId(2);
		allocation3.setStartDate("2019-01-01");
		allocation3.setEndDate("2019-05-05");
		allocation3.setPensumPercentage(50);
		allocation3.setProjectId(2);
		allocation3.setId(3);
		
		//Employee: 3; 2019-06-06 - 2019-12-01; 100%; Project: 3
		AllocationDTO allocation4 = new AllocationDTO();
		allocation4.setContractId(3);
		allocation4.setStartDate("2019-06-01");
		allocation4.setEndDate("2019-12-01");
		allocation4.setPensumPercentage(100);
		allocation4.setProjectId(3);
		allocation4.setId(4);
		
		List<AllocationDTO> allocations = new ArrayList<>();
		allocations.add(allocation1);
		allocations.add(allocation2);
		allocations.add(allocation3);
		allocations.add(allocation4);
		
		return allocations;
	}

}
