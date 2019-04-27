package wodss.timecastfrontend.services.mocks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.services.AllocationService;

@Component
public class MockAllocationService extends AllocationService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	public MockAllocationService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.allocation}") String apiURL,
			MockContractService contractService, MockProjectService projectService) {
		super(restTemplate, apiURL, contractService, projectService);
        logger.debug("Using Mock Allocation Service!");
        logger.debug("API URL " + apiURL + " will not be used in the mock service!");
	}

	public List<Allocation> getAll(Token token) {
		return MockRepository.allocations;
	}
	
	@Override
	public List<Allocation> getAllocations(Token token, long employeeId, long projectId, Date fromDateString, Date toDateString) {
		Stream<Allocation> stream = MockRepository.allocations.stream();
		if (projectId >= 0 ) {
			stream = stream.filter(a -> a.getProject().getId() == projectId);
		}
		if (employeeId >= 0) {
			List<Long> contractIds = MockRepository.contracts.stream().filter(c -> c.getEmployee().getId() == employeeId).map(c -> c.getId()).collect(Collectors.toList());
			stream = stream.filter(a -> contractIds.contains(a.getContract().getId()));
		}
		if (fromDateString != null && !fromDateString.equals("")) {
			logger.debug("Filter allocations");
			stream = stream.filter(a -> a.getStartDate().after(fromDateString));
		}
		if (toDateString != null && !toDateString.equals("")) {
			stream = stream.filter(a -> a.getEndDate().before(toDateString));
		}
		logger.debug("Returning allocations");
		return stream.collect(Collectors.toList());
	}

	@Override
	public Allocation getById(Token token, long id) {
		if (MockRepository.allocations.stream().anyMatch(a -> a.getId() == id)) {
			return MockRepository.allocations.stream().filter(a -> a.getId() == id).findFirst().get();
		} else {
			throw new TimecastNotFoundException("Allocation not found");
		}
	}

	@Override
	public Allocation create(Token token, Allocation newAllocation) {
		newAllocation.setId(MockRepository.nextAllocationId++);
		MockRepository.allocations.add(newAllocation);
		return newAllocation;
	}

	@Override
	public Allocation update(Token token, Allocation updatedAllocation) {
		if (MockRepository.allocations.stream().anyMatch(a -> a.getId() == updatedAllocation.getId())) {
			Allocation oldAllocation = MockRepository.allocations.stream().filter(a -> a.getId() == updatedAllocation.getId()).findFirst().get();
			oldAllocation.setContract(updatedAllocation.getContract());
			oldAllocation.setStartDate(updatedAllocation.getStartDate());
			oldAllocation.setEndDate(updatedAllocation.getEndDate());
			oldAllocation.setPensumPercentage(updatedAllocation.getPensumPercentage());
			oldAllocation.setProject(updatedAllocation.getProject());
			return oldAllocation;
		} else {
			throw new TimecastNotFoundException("Allocation not found");
		}
	}

	@Override
	public void deleteById(Token token, long id) {
		if (MockRepository.allocations.stream().anyMatch(a -> a.getId() == id)) {
			MockRepository.allocations.removeIf(a -> a.getId() == id);
		} else {
			throw new TimecastNotFoundException("Allocation not found");
		}
	}

}
