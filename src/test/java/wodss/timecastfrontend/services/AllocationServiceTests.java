package wodss.timecastfrontend.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.junit.Assert;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.dto.AllocationDto;
import wodss.timecastfrontend.exceptions.RestTemplateResponseErrorHandler;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;

@RunWith(SpringRunner.class)
public class AllocationServiceTests {
	@Mock
	private RestTemplate restTemplateMock;
	@Mock
    private ContractService contractServiceMock;
	@Mock
    private ProjectService projectServiceMock;
	
	private AllocationService allocationService;

	private String url = "url";
    private Token token = new Token("any String");
	
	private List<AllocationDto> allocations;

	
	@Before
	public void setUp() {
		Mockito.reset(restTemplateMock);
		allocations = generateAllocations();

        Contract c1 = new Contract(); c1.setId(1);
        Contract c2 = new Contract(); c2.setId(2);
        Contract c3 = new Contract(); c3.setId(3);
        Mockito.when(contractServiceMock.getById(token, 1)).thenReturn(c1);
        Mockito.when(contractServiceMock.getById(token, 2)).thenReturn(c2);
        Mockito.when(contractServiceMock.getById(token, 3)).thenReturn(c3);

        Project p1 = new Project(); p1.setId(1);
        Project p2 = new Project(); p2.setId(2);
        Project p3 = new Project(); p3.setId(3);
        Mockito.when(projectServiceMock.getById(token, 1)).thenReturn(p1);
        Mockito.when(projectServiceMock.getById(token, 2)).thenReturn(p2);
        Mockito.when(projectServiceMock.getById(token, 3)).thenReturn(p3);

		allocationService = new AllocationService(restTemplateMock, url, contractServiceMock, projectServiceMock);
	}
	
	@Test
	public void getAllAllocationsByEmployeeId() {
        String paramUrl = url + "?employeeId=2&";
		List<AllocationDto> returnAllocations = new ArrayList<>();
		returnAllocations.add(allocations.get(2));

		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(returnAllocations, HttpStatus.OK));
		
		List<Allocation> fetchedAllocations = allocationService.getAllocations(token, 2, -1, null, null);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(returnAllocations, mapAllocationsToDtos(fetchedAllocations));
	}
	
	@Test
	public void getAllAllocationsByProjectId() {
        String paramUrl = url + "?projectId=2&";
		List<AllocationDto> returnAllocations = new ArrayList<>();
		returnAllocations.add(allocations.get(1));
		returnAllocations.add(allocations.get(2));

		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
                Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(returnAllocations, HttpStatus.OK));
		
		List<Allocation> fetchedAllocations = allocationService.getAllocations(token, -1, 2, null, null);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class));

		Assert.assertEquals(returnAllocations, mapAllocationsToDtos(fetchedAllocations));
	}
	
	@Test
	public void getAllAllocationsByEmployeeIdProjectId() {
        String paramUrl = url + "?employeeId=2&projectId=2&";
		List<AllocationDto> returnAllocations = new ArrayList<>();
		returnAllocations.add(allocations.get(1));
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(returnAllocations, HttpStatus.OK));
		
		List<Allocation> fetchedAllocations = allocationService.getAllocations(token, 2, 2, null, null);
		
		verify(restTemplateMock, times(1)).exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class));
		
		Assert.assertEquals(returnAllocations, mapAllocationsToDtos(fetchedAllocations));
	}
	
	@Test(expected=HttpClientErrorException.NotFound.class)
	public void getAllAllocationsNotFound() {
        String paramUrl = url + "?employeeId=10&";
		List<AllocationDto> returnAllocations = new ArrayList<>();
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "", new HttpHeaders(), new byte[0], Charset.defaultCharset()));
		allocationService.getAllocations(token, 10, -1, null, null);
	}
	
	@Test(expected=HttpClientErrorException.Forbidden.class)
	public void getAllAllocationsForbidden() {
        String paramUrl = url + "?employeeId=1&";
		List<AllocationDto> returnAllocations = new ArrayList<>();
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class)))
				.thenThrow(HttpClientErrorException.create(HttpStatus.FORBIDDEN, "", new HttpHeaders(), new byte[0], Charset.defaultCharset()));
		
		allocationService.getAllocations(token, 1, -1, null, null);
	}
	
	@Test(expected=HttpClientErrorException.class)
	public void getAllAllocationsInternalServerError() {
        String paramUrl = url + "?employeeId=1&";
		List<AllocationDto> returnAllocations = new ArrayList<>();
		
		Mockito.when(restTemplateMock.exchange(Mockito.eq(paramUrl), Mockito.eq(HttpMethod.GET),
				Mockito.any(HttpEntity.class), Mockito.any(ParameterizedTypeReference.class)))
				.thenThrow(HttpClientErrorException.create(HttpStatus.INTERNAL_SERVER_ERROR, "", new HttpHeaders(), new byte[0], Charset.defaultCharset()));
		
		allocationService.getAllocations(token, 1, -1, null, null);
	}
	
	
	private List<AllocationDto> generateAllocations() {
		
		//Employee: 1; 2019-01-01 - 2019-05-05; 50%; Project: 1
		AllocationDto allocation1 = new AllocationDto();
		allocation1.setContractId(1);
		allocation1.setStartDate("2019-01-01");
		allocation1.setEndDate("2019-05-05");
		allocation1.setPensumPercentage(50);
		allocation1.setProjectId(1);
		allocation1.setId(1);
		
		//Employee: 1; 2019-03-01 - 2019-05-05; 50%; Project: 2
		AllocationDto allocation2 = new AllocationDto();
		allocation2.setContractId(1);
		allocation2.setStartDate("2019-03-01");
		allocation2.setEndDate("2019-05-05");
		allocation2.setPensumPercentage(50);
		allocation2.setProjectId(2);
		allocation2.setId(2);
		
		//Employee: 2; 2019-01-01 - 2019-05-05; 50%; Project: 2
		AllocationDto allocation3 = new AllocationDto();
		allocation3.setContractId(2);
		allocation3.setStartDate("2019-01-01");
		allocation3.setEndDate("2019-05-05");
		allocation3.setPensumPercentage(50);
		allocation3.setProjectId(2);
		allocation3.setId(3);
		
		//Employee: 3; 2019-06-06 - 2019-12-01; 100%; Project: 3
		AllocationDto allocation4 = new AllocationDto();
		allocation4.setContractId(3);
		allocation4.setStartDate("2019-06-01");
		allocation4.setEndDate("2019-12-01");
		allocation4.setPensumPercentage(100);
		allocation4.setProjectId(3);
		allocation4.setId(4);
		
		List<AllocationDto> allocations = new ArrayList<>();
		allocations.add(allocation1);
		allocations.add(allocation2);
		allocations.add(allocation3);
		allocations.add(allocation4);
		
		return allocations;
	}

    private List<AllocationDto> mapAllocationsToDtos(List<Allocation> allocations) {
        return allocations.stream()
                .map(allocation -> allocationService.mapEntityToDto(token, allocation))
                .collect(Collectors.toList());
    }
}
