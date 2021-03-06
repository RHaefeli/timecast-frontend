package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controls employee web component
 *
 */
@Controller
@RequestMapping(value = "/employees")
public class EmployeeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmployeeService employeeService;
    private final ContractService contractService;
    private final AllocationService allocationService;

    /**
     * Contstructor
     * @param employeeService
     * @param contractService
     * @param allocationService
     */
    @Autowired
    public EmployeeController(EmployeeService employeeService, ContractService contractService,
                              AllocationService allocationService) {
        this.employeeService = employeeService;
        this.contractService = contractService;
        this.allocationService = allocationService;
    }

    /**
     * Fetches and shows all employees
     * @param model
     * @return pagelink
     */
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

    /**
     * Creates the form to create an employee
     * @param model
     * @return
     */
    @GetMapping(params = "form")
    public String createForm(Model model) {
        logger.debug("Get create employee form");
        model.addAttribute("employee", new EmployeeLogin());
        return "employees/create";
    }

    /**
     * Create an employee
     * @param employeeLogin
     * @param bindingResult
     * @param model
     * @param redirectAttributes
     * @return pagelink
     */
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

        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            employeeLogin.setActive(true);
            Employee newEmployee = employeeService.create(token, employeeLogin);
            redirectAttributes.addFlashAttribute("success", "Successfully created Employee.");
            return "redirect:/employees/" + newEmployee.getId();
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", "Invalid Input. Please Check all fields (The Email Address must be unique)");
            return "employees/create";
        }
    }

    /**
     * Fetch employee to show on page
     * @param id
     * @param model
     * @return pagelink
     */
    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Model model) {
        logger.debug("Get employee by id: " + id);
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Employee employee = employeeService.getById(token, id);
        List<Contract> contracts = contractService.getByEmployee(token, employee);
        model.addAttribute("employee", employee);
        model.addAttribute("contracts", contracts);

        return "employees/update";
    }

    /**
     * Updates an employee
     * @param id
     * @param employee
     * @param bindingResult
     * @param model
     * @return pagelink
     */
    @PutMapping("/{id}")
    public String updateById(@PathVariable Long id, @Valid @ModelAttribute("employee") Employee employee,
                             BindingResult bindingResult, Model model) {
        logger.debug("Update employee by id: " + id);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/update";
        }
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            employee.setId(id);
            Employee updatedEmployee = employeeService.update(token, employee);
            model.addAttribute("employee", updatedEmployee);
            model.addAttribute("success", "Successfully updated Employee.");
            return "employees/update";
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "employees/update";
        }
    }

    /**
     * Deletes an employee
     * @param id
     * @param redirectAttributes
     * @return pagelink
     */
    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Delete employee by id: " + id);
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try{
            employeeService.deleteById(token, id);
        } catch (TimecastPreconditionFailedException ex) {
            redirectAttributes.addFlashAttribute("exception", "Cannot delete employee when there are still allocations and contracts referencing to this employee.");
            return "redirect:/employees/" + id;
        }

        redirectAttributes.addFlashAttribute("success", "Successfully deleted Employee.");
        return "redirect:/employees";
    }

    /**
     * Create form to add a contract
     * @param id
     * @param model
     * @return pagelink
     */
    @GetMapping(value = "/{id}/contracts", params = "form")
    public String createContractForm(@PathVariable Long id, Model model) {
        logger.debug("Get create contract form for employee: " + id);
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Employee employee = employeeService.getById(token, id);
        Contract contract = new Contract();
        contract.setEmployee(employee);
        List<Contract> contracts = contractService.getByEmployee(token, employee);

        model.addAttribute("contract", contract);
        model.addAttribute("contracts", contracts);
        return "employees/contracts/create";
    }

    /**
     * Create a contract
     * @param id
     * @param contract
     * @param bindingResult
     * @param model
     * @param redirectAttributes
     * @return pagelink
     */
    @PostMapping("/{id}/contracts")
    public String createContract(@PathVariable Long id, @Valid @ModelAttribute("contract") Contract contract,
                                 BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("Create Contract: " + contract);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/contracts/create";
        }
        try {
            Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            Employee employee = employeeService.getById(token, id);
            contract.setEmployee(employee);
            Contract newContract = contractService.create(token, contract);

            redirectAttributes.addFlashAttribute("success", "Successfully created Contract.");
            return "redirect:/contracts/" + newContract.getId();
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", "Invalid Input. Please Check all fields.");
            return "employees/contracts/create";
        }
    }
}
