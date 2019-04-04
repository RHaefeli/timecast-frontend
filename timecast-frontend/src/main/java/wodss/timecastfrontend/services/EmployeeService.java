package wodss.timecastfrontend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import wodss.timecastfrontend.domain.Employee;

@Component
public class EmployeeService extends AbstractService<Employee> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EmployeeService(RestTemplate restTemplate, @Value("${wodss.timecastfrontend.api.url.employee}") String apiURL) {
        super(restTemplate, apiURL, Employee.class);
    }
}
