package wodss.timecastfrontend.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.security.CustomAuthenticationProvider;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private WebApplicationContext context;

    @MockBean private EmployeeService mockEmployeeService;
    @MockBean private ContractService mockContractService;
    @MockBean private AllocationService mockAllocationService;
    @MockBean private CustomAuthenticationProvider mockAuthenticationProvider;
    @MockBean private SessionHandlerInterceptor mockSessionHandlerInterceptor;

    private String url = "https://localhost/employees";
    private Token token = new Token("any String");

    private MockMvc mvc;

    private List<Employee> employees;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        employees = generateEmployees();

        Mockito.when(mockEmployeeService.getById(token, 0))
                .thenReturn(employees.get(0));

    }

    @Test
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    public void testGetAllEmployees() throws Exception {
        // TODO: fix test
        mvc.perform(get("/employees"))
                .andExpect(status().isOk());
                //.andExpect(view().name("employees/list"));
                //.andExpect(model().attribute("employees", employees));
    }

    private List<Employee> generateEmployees() {
        Employee emp1 = new Employee();
        emp1.setId(1);
        emp1.setLastName("Müller");
        emp1.setFirstName("Kurt");
        emp1.setActive(true);
        emp1.setEmailAddress("k.mueller@mail.com");
        emp1.setRole(Role.ADMINISTRATOR);

        Employee emp2 = new Employee();
        emp2.setId(2);
        emp2.setLastName("Meier");
        emp2.setFirstName("Jonathan");
        emp2.setActive(true);
        emp2.setEmailAddress("j.meier@mail.com");
        emp2.setRole(Role.DEVELOPER);

        Employee emp3 = new Employee();
        emp3.setId(3);
        emp3.setLastName("Brösmeli");
        emp3.setFirstName("Guschdi");
        emp3.setActive(true);
        emp3.setEmailAddress("g.broesmeli@mail.com");
        emp3.setRole(Role.PROJECTMANAGER);

        Employee emp4 = new Employee();
        emp4.setId(4);
        emp4.setLastName("Heftig");
        emp4.setFirstName("Dud");
        emp4.setActive(true);
        emp4.setEmailAddress("g.Dudsmeli@mail.com");
        emp4.setRole(Role.PROJECTMANAGER);

        List<Employee> employees = new ArrayList<>();
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp4);

        return employees;
    }
}
