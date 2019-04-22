package wodss.timecastfrontend.services.mocks;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.dto.ContractDTO;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.ContractService;

public class MockContractService extends ContractService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<ContractDTO> contractRepo;

	public MockContractService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.contract}") String apiURL) {
		super(restTemplate, apiURL);
        logger.debug("Using Mock contract Service!");
        logger.debug("API URL " + apiURL + " will not be used in the mock service!");
        
        //TODO fix workaround
        contractRepo = MockRepository.contracts;
        logger.debug("Mockrepo: {}", contractRepo);
	}
	
	@Override
	public List<ContractDTO> getAll() {
		logger.debug("Get all contracts without param");
		return contractRepo;
	}

	@Override
	public List<ContractDTO> getContracts(String fromDate, String toDate) {
		logger.debug("Get all contracts with params {} {}",fromDate, toDate);
		if (("".equals(fromDate) && "".equals(toDate)) || (fromDate == null) || (toDate == null)) {
			return contractRepo;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public ContractDTO getById(long id) {
		if (contractRepo.stream().anyMatch(p -> p.getId() == id)) {
			return contractRepo.stream().filter(p -> p.getId() == id).findFirst().get();
		} else {
			throw new TimecastNotFoundException("contract not found");
		}
	}

	@Override
	public ContractDTO create(ContractDTO newcontract) throws TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		newcontract.setId(MockRepository.nextContractId++);
		contractRepo.add(newcontract);
		return newcontract;
	}

	@Override
	public ContractDTO update(ContractDTO updatedcontract) throws TimecastNotFoundException, TimecastPreconditionFailedException, TimecastForbiddenException, TimecastInternalServerErrorException {
		if (contractRepo.stream().anyMatch(p -> p.getId() == updatedcontract.getId())) {
			ContractDTO oldcontract = contractRepo.stream().filter(p -> p.getId() == updatedcontract.getId()).findFirst().get();
			oldcontract.setStartDate(updatedcontract.getStartDate());
			oldcontract.setEndDate(updatedcontract.getEndDate());
			oldcontract.setEmployeeId(updatedcontract.getEmployeeId());
			oldcontract.setPensumPercentage(updatedcontract.getPensumPercentage());
			return oldcontract;
		} else {
			throw new TimecastNotFoundException("contract not found");
		}
	}

	@Override
	public void deleteById(long id) throws TimecastInternalServerErrorException, TimecastForbiddenException, TimecastNotFoundException {
		if (contractRepo.stream().anyMatch(p -> p.getId() == id)) {
			contractRepo.removeIf(p -> p.getId() == id);
		} else {
			throw new TimecastNotFoundException("contract not found");
		}
	}

}
