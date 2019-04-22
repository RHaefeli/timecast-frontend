package wodss.timecastfrontend.services.mocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import wodss.timecastfrontend.domain.dto.EmployeeDTO;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.services.EmployeeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class MockEmployeeService extends EmployeeService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<EmployeeDTO> employeeRepo;
    private int nextProjectId = 0;

    public MockEmployeeService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.employee}") String apiURL) {
        super(restTemplate, apiURL);
        logger.debug("Using Mock Employee Service!");
        logger.debug("API URL " + apiURL + " will not be used in the mock service!");
        employeeRepo = generateEmployees();
    }

    @Override
    public List<EmployeeDTO> getAll() {
        return employeeRepo;
    }

    @Override
    public EmployeeDTO getById(long id) {
        Optional<EmployeeDTO> employee = employeeRepo.stream().filter(e -> e.getId() == id).findFirst();
        if (employee.isPresent()) {
            return employee.get();
        } else {
            throw new TimecastNotFoundException("Employee not found");
        }
    }

    @Override
    public EmployeeDTO create(EmployeeDTO newEmployee) {
        newEmployee.setId(nextProjectId++);
        employeeRepo.add(newEmployee);
        return newEmployee;
    }

    @Override
    public EmployeeDTO update(EmployeeDTO updatedEmployee) {
        Optional<EmployeeDTO> result = employeeRepo.stream().filter(e -> e.getId() == updatedEmployee.getId()).findFirst();
        if (result.isPresent()) {
            EmployeeDTO oldEmployee = result.get();
            oldEmployee.setLastName(updatedEmployee.getLastName());
            oldEmployee.setFirstName(updatedEmployee.getEmailAddress());
            oldEmployee.setActive(updatedEmployee.isActive());
            oldEmployee.setEmailAddress(updatedEmployee.getEmailAddress());
            oldEmployee.setRole(updatedEmployee.getRole());
            return oldEmployee;
        } else {
            throw new TimecastNotFoundException("Employee not found");
        }
    }

    @Override
    public void deleteById(long id) {
        if (employeeRepo.stream().anyMatch(e -> e.getId() == id)) {
            employeeRepo.removeIf(e -> e.getId() == id);
        } else {
            throw new TimecastNotFoundException("Employee not found");
        }
    }


    private List<EmployeeDTO> generateEmployees() {
        EmployeeDTO emp1 = new EmployeeDTO();
        emp1.setId(nextProjectId++);
        emp1.setLastName("Müller");
        emp1.setFirstName("Kurt");
        emp1.setActive(true);
        emp1.setEmailAddress("k.mueller@mail.com");
        emp1.setRole("Admin");

        EmployeeDTO emp2 = new EmployeeDTO();
        emp2.setId(nextProjectId++);
        emp2.setLastName("Meier");
        emp2.setFirstName("Jonathan");
        emp2.setActive(true);
        emp2.setEmailAddress("j.meier@mail.com");
        emp2.setRole("Developer");

        EmployeeDTO emp3 = new EmployeeDTO();
        emp3.setId(nextProjectId++);
        emp3.setLastName("Brösmeli");
        emp3.setFirstName("Guschdi");
        emp3.setActive(true);
        emp3.setEmailAddress("g.broesmeli@mail.com");
        emp3.setRole("Project Leader");

        List<EmployeeDTO> employees = new ArrayList<>();
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);

        return employees;
    }
}