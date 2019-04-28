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
import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.mocks.MockContractService;
import wodss.timecastfrontend.services.mocks.MockEmployeeService;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(value = "/employees")
public class EmployeeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmployeeService employeeService;
    private final ContractService contractService;
    private final AllocationService allocationService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, ContractService contractService,
                              AllocationService allocationService) {
        this.employeeService = employeeService;
        this.contractService = contractService;
        this.allocationService = allocationService;
    }

    @GetMapping
    public String getAll(Model model) {
        logger.debug("Get all employees");
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<Employee> employees = employeeService.getAll(token);

        Map<Long, Contract> contractMap = contractService.getCurrentContracts(token)
                .stream()
                .collect(Collectors.toMap(c -> c.getEmployee().getId(), c -> c));

        Map<Long, Long> projectCounterMap = allocationService.getAllocations(token, -1, -1, new Date(), null)
                .stream()
                .collect(Collectors.groupingBy(a -> a.getContract().getEmployee().getId(), Collectors.counting()));

        model.addAttribute("employees", employees);
        model.addAttribute("contractMap", contractMap);
        model.addAttribute("projectCounterMap", projectCounterMap);

        return "employees/list";
    }

    @GetMapping(params = "form")
    public String createForm(Model model) {
        logger.debug("Get create employee form");
        model.addAttribute("employee", new EmployeeLogin());
        return "employees/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("employee") EmployeeLogin employeeLogin, BindingResult bindingResult, Model model,
                         RedirectAttributes redirectAttributes) {
        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.validate(employeeLogin, bindingResult);

        logger.debug("Create employee: " + employeeLogin);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/create";
        }

        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            employeeLogin.setActive(true);
            Employee newEmployee = employeeService.create(new Token(token), employeeLogin);
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
