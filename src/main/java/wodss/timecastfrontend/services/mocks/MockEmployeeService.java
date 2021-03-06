package wodss.timecastfrontend.services.mocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.services.EmployeeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class MockEmployeeService extends EmployeeService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Employee> employeeRepo;
    private int nextEmployeeId = 0;

    public MockEmployeeService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.employee}") String apiURL) {
        super(restTemplate, apiURL);
        employeeRepo = generateEmployees();
    }

    @Override
    public List<Employee> getAll(Token token) {
        logger.debug("Request list of employees in MockEmployeeService");
        return employeeRepo;
    }

    @Override
    public Employee getById(Token token, long id) {
        logger.debug("Request employee with id " + id + " in MockEmployeeService");
        Optional<Employee> employee = employeeRepo.stream().filter(e -> e.getId() == id).findFirst();
        if (employee.isPresent()) {
            return employee.get();
        } else {
            throw new TimecastNotFoundException("Employee not found");
        }
    }

    @Override
    public Employee create(Token token, Employee newEmployee) {
        logger.debug("Create new employee " + newEmployee + " in MockEmployeeService");
        newEmployee.setId(nextEmployeeId++);
        employeeRepo.add(newEmployee);
        return newEmployee;
    }

    @Override
    public Employee update(Token token, Employee updatedEmployee) {
        logger.debug("Update employee with id " + updatedEmployee.getId() + " in MockEmployeeService");
        Optional<Employee> result = employeeRepo.stream().filter(e -> e.getId() == updatedEmployee.getId()).findFirst();
        if (result.isPresent()) {
            Employee oldEmployee = result.get();
            oldEmployee.setLastName(updatedEmployee.getLastName());
            oldEmployee.setFirstName(updatedEmployee.getFirstName());
            oldEmployee.setActive(updatedEmployee.isActive());
            oldEmployee.setEmailAddress(updatedEmployee.getEmailAddress());
            oldEmployee.setRole(updatedEmployee.getRole());
            return oldEmployee;
        } else {
            throw new TimecastNotFoundException("Employee not found");
        }
    }

    @Override
    public void deleteById(Token token, long id) {
        logger.debug("Delete employee with id " + id + " in MockEmployeeService");
        if (employeeRepo.stream().anyMatch(e -> e.getId() == id)) {
            employeeRepo.removeIf(e -> e.getId() == id);
        } else {
            throw new TimecastNotFoundException("Employee not found");
        }
    }


    private List<Employee> generateEmployees() {
        Employee emp1 = new Employee();
        emp1.setId(nextEmployeeId++);
        emp1.setLastName("Müller");
        emp1.setFirstName("Kurt");
        emp1.setActive(true);
        emp1.setEmailAddress("k.mueller@mail.com");
        emp1.setRole(Role.ADMINISTRATOR);

        Employee emp2 = new Employee();
        emp2.setId(nextEmployeeId++);
        emp2.setLastName("Meier");
        emp2.setFirstName("Jonathan");
        emp2.setActive(true);
        emp2.setEmailAddress("j.meier@mail.com");
        emp2.setRole(Role.DEVELOPER);

        Employee emp3 = new Employee();
        emp3.setId(nextEmployeeId++);
        emp3.setLastName("Brösmeli");
        emp3.setFirstName("Guschdi");
        emp3.setActive(true);
        emp3.setEmailAddress("g.broesmeli@mail.com");
        emp3.setRole(Role.PROJECTMANAGER);
        
        Employee emp4 = new Employee();
        emp4.setId(nextEmployeeId++);
        emp4.setLastName("Heftig");
        emp4.setFirstName("Dud");
        emp4.setActive(true);
        emp4.setEmailAddress("g.Dudsmeli@mail.com");
        emp4.setRole(Role.PROJECTMANAGER);

        List<Employee> employees = new ArrayList<>();
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp4);

        return employees;
    }
}