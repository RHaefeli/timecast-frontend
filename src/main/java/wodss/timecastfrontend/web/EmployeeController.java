package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.PasswordValidator;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.mocks.MockContractService;
import wodss.timecastfrontend.services.mocks.MockEmployeeService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/employees")
public class EmployeeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmployeeService employeeService;
    private final ContractService contractService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, ContractService contractService) {
        this.employeeService = employeeService;
        this.contractService = contractService;
    }

    @GetMapping
    public String getAll(Model model) {
        logger.debug("Get all employees");
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Employee> employees = employeeService.getAll(new Token(token));
        model.addAttribute("employees", employees);

        return "employees/list";
    }

    @GetMapping(params = "form")
    public String createForm(Model model) {
        logger.debug("Get create employee form");
        model.addAttribute("employee", new Employee());
        return "employees/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("employee") Employee employee, BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.validate(employee, bindingResult);

        logger.debug("Create employee: " + employee);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/create";
        }
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            employee.setActive(true);
            Employee newEmployee = employeeService.create(new Token(token), employee);
            redirectAttributes.addFlashAttribute("success", "Successfully created Employee.");
            return "redirect:/employees/" + newEmployee.getId();
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", "Invalid Input. Please Check all fields (The Email Address must be unique)");
            return "employees/create";
        }
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Model model) {
        logger.debug("Get employee by id: " + id);
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee employee = employeeService.getById(new Token(token), id);
        List<Contract> contracts = contractService.getByEmployee(new Token(token), employee);
        model.addAttribute("employee", employee);
        model.addAttribute("contracts", contracts);

        return "employees/update";
    }

    @PutMapping("/{id}")
    public String updateById(@PathVariable Long id, @Valid @ModelAttribute("employee") Employee employee,
                             BindingResult bindingResult, Model model) {
        logger.debug("Update employee by id: " + id);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/update";
        }
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Employee updatedEmployee = employeeService.update(new Token(token), employee);
            model.addAttribute("employee", updatedEmployee);
            model.addAttribute("success", "Successfully updated Employee.");
            return "employees/update";
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "employees/update";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Delete employee by id: " + id);
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        employeeService.deleteById(new Token(token), id);

        redirectAttributes.addFlashAttribute("success", "Successfully deleted Employee.");
        return "redirect:/employees";
    }

    @GetMapping(value = "/{id}/contracts", params = "form")
    public String createContractForm(@PathVariable Long id, Model model) {
        logger.debug("Get create contract form for employee: " + id);
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee employee = employeeService.getById(new Token(token), id);
        Contract contract = new Contract();
        contract.setEmployee(employee);
        List<Contract> contracts = contractService.getByEmployee(new Token(token), employee);

        model.addAttribute("contract", contract);
        model.addAttribute("contracts", contracts);
        return "employees/contracts/create";
    }

    @PostMapping("/{id}/contracts")
    public String createContract(@PathVariable Long id, @Valid @ModelAttribute("contract") Contract contract,
                                 BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("Create Contract: " + contract);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/contracts/create";
        }
        try {
            String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Employee employee = employeeService.getById(new Token(token), id);
            contract.setEmployee(employee);
            Contract newContract = contractService.create(new Token(token), contract);

            redirectAttributes.addFlashAttribute("success", "Successfully created Contract.");
            return "redirect:/contracts/" + newContract.getId();
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "employees/contracts/create";
        }
    }
}
