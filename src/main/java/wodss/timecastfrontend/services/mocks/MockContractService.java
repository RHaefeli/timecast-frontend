package wodss.timecastfrontend.services.mocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class MockContractService extends ContractService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Contract> contractRepo;
    private int nextContractId = 0;
    private EmployeeService employeeService;
    private Token token = new Token("any String");

    @Autowired
    public MockContractService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.contract}") String apiURL,
                               MockEmployeeService employeeService) {
        super(restTemplate, apiURL, employeeService);
        this.employeeService = employeeService;
        contractRepo = generateContracts();
    }

    @Override
    public List<Contract> getAll(Token token) {
        logger.debug("Request list of contracts in MockContractService");
        return contractRepo;
    }

    @Override
    public Contract getById(Token token, long id) {
        logger.debug("Request contract with id " + id + " in MockContractService");
        Optional<Contract> contract = contractRepo.stream().filter(c -> c.getId() == id).findFirst();
        if (contract.isPresent()) {
            return contract.get();
        } else {
            throw new TimecastNotFoundException("Contract not found");
        }
    }

    @Override
    public Contract create(Token token, Contract newContract) {
        logger.debug("Create new contract " + newContract + " in MockContractService");
        newContract.setId(nextContractId++);
        contractRepo.add(newContract);
        return newContract;
    }

    @Override
    public Contract update(Token token, Contract updatedContract) {
        logger.debug("Update contract with id " + updatedContract.getId() + " in MockContractService");
        Optional<Contract> result = contractRepo.stream().filter(e -> e.getId() == updatedContract.getId()).findFirst();
        if (result.isPresent()) {
            Contract oldContract = result.get();
            oldContract.setEmployee(updatedContract.getEmployee());
            oldContract.setStartDate(updatedContract.getStartDate());
            oldContract.setEndDate(updatedContract.getEndDate());
            oldContract.setPensumPercentage(updatedContract.getPensumPercentage());
            return oldContract;
        } else {
            throw new TimecastNotFoundException("Contract not found");
        }
    }

    @Override
    public void deleteById(Token token, long id) {
        logger.debug("Delete contract with id " + id + " in MockContractService");
        if (contractRepo.stream().anyMatch(e -> e.getId() == id)) {
            contractRepo.removeIf(e -> e.getId() == id);
        } else {
            throw new TimecastNotFoundException("Contract not found");
        }
    }

    @Override
    public List<Contract> getByEmployee(Token token, Employee employee) {
        logger.debug("Request list for ContractDtos by Employee " + employee.getId() + " from api: " + apiURL);
        return contractRepo.stream()
                .filter(c -> c.getEmployee().getId() == employee.getId())
                .collect(Collectors.toList());
    }


    private List<Contract> generateContracts() {
        Contract cont = new Contract();
        Date date = new Date();
        cont.setId(nextContractId++);
        cont.setEmployee(employeeService.getById(token, 0));
        cont.setStartDate(date);
        cont.setEndDate(new Date(date.getTime() + 24 * 60 * 60 * 1000));
        cont.setPensumPercentage(50);

        List<Contract> contracts = new ArrayList<>();
        contracts.add(cont);

        return contracts;
    }
}