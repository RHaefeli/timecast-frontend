package wodss.timecastfrontend.services.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ProjectService;

public class MockAllocationService extends AllocationService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public MockAllocationService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.allocation}") String apiURL) {
		super(restTemplate, apiURL);
        logger.debug("Using Mock Allocation Service!");
        logger.debug("API URL " + apiURL + " will not be used in the mock service!");
	}
	
	@Override
	public List<Allocation> getAll() {
		return MockRepository.allocations;
	}

	@Override
	public List<Allocation> getAllocations(long employeeId, long projectId) {
		Stream<Allocation> stream = MockRepository.allocations.stream();
		if (projectId >= 0 ) {
			stream = stream.filter(a -> a.getProjectId() != projectId);
		}
		if (employeeId >= 0) {
			List<Long> contractIds = MockRepository.contracts.stream().filter(c -> c.getEmployeeId() == employeeId).map(c -> c.getId()).collect(Collectors.toList());
			stream = stream.filter(a -> contractIds.contains(a.getContractId()));
		}
		
		return stream.collect(Collectors.toList());
	}

	@Override
	public Allocation getById(long id) {
		if (MockRepository.allocations.stream().anyMatch(a -> a.getId() == id)) {
			return MockRepository.allocations.stream().filter(a -> a.getId() == id).findFirst().get();
		} else {
			throw new TimecastNotFoundException("Allocation not found");
		}
	}

	@Override
	public Allocation create(Allocation newAllocation) {
		newAllocation.setId(MockRepository.nextAllocationId++);
		MockRepository.allocations.add(newAllocation);
		return newAllocation;
	}

	@Override
	public Allocation update(Allocation updatedAllocation) {
		if (MockRepository.allocations.stream().anyMatch(a -> a.getId() == updatedAllocation.getId())) {
			Allocation oldAllocation = MockRepository.allocations.stream().filter(a -> a.getId() == updatedAllocation.getId()).findFirst().get();
			oldAllocation.setContractId(updatedAllocation.getContractId());
			oldAllocation.setStartDate(updatedAllocation.getStartDate());
			oldAllocation.setEndDate(updatedAllocation.getEndDate());
			oldAllocation.setPensumPercentage(updatedAllocation.getPensumPercentage());
			oldAllocation.setProjectId(updatedAllocation.getProjectId());
			return oldAllocation;
		} else {
			throw new TimecastNotFoundException("Allocation not found");
		}
	}

	@Override
	public void deleteById(long id) {
		if (MockRepository.allocations.stream().anyMatch(a -> a.getId() == id)) {
			MockRepository.allocations.removeIf(a -> a.getId() == id);
		} else {
			throw new TimecastNotFoundException("Allocation not found");
		}
	}

}
