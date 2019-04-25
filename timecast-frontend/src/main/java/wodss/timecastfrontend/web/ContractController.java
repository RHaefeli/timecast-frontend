package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.mocks.MockContractService;
import wodss.timecastfrontend.services.mocks.MockEmployeeService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/contracts")
public class ContractController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ContractService contractService;

    @Autowired
    public ContractController(MockContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping
    public String getAll(Model model) {
        logger.debug("Get all contracts");
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Contract> contracts = contractService.getAll(new Token(token));
        model.addAttribute("contracts", contracts);

        return "contracts/list";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, HttpServletRequest request, Model model) {
        logger.debug("Get contract by id: " + id);
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Contract contract = contractService.getById(new Token(token), id);
        List<Contract> contracts = contractService.getByEmployee(new Token(token), contract.getEmployee());

        model.addAttribute("contract", contract);
        model.addAttribute("contracts", contracts);

        return "contracts/update";
    }



    /*
    @GetMapping(params = "form")
    public String createForm(HttpServletRequest request, Model model) {
        logger.debug("Get create contract form");
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Employee> employees = employeeService.getAll(new Token(token));

        model.addAttribute("contract", new Contract());
        model.addAttribute("employees", employees);
        return "contracts/create";
    }

    @PostMapping
    public String create(HttpServletRequest request, @Valid @ModelAttribute("employee") Employee employee,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("Create employee: " + employee);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/create";
        }
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            employee.setActive(true);
            Employee newEmployee = contractService.create(new Token(token), employee);
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "employees/create";
        }

        redirectAttributes.addFlashAttribute("success", "Success");
        return "redirect:/employees";
    }

    @GetMapping("/{id}")
    public String getById(HttpServletRequest request, @PathVariable long id, Model model) {
        logger.debug("Get employee by id: " + id);
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee employee = contractService.getById(new Token(token), id);
        model.addAttribute("employee", employee);

        return "employees/update";
    }

    @PutMapping("/{id}")
    public String updateById(HttpServletRequest request, @PathVariable Long id,
                             @Valid @ModelAttribute("employee") Employee employee, BindingResult bindingResult,
                             Model model, RedirectAttributes redirectAttributes) {
        logger.debug("Update employee by id: " + id);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/update";
        }
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Employee updatedEmployee = contractService.update(new Token(token), employee);
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "employees/update";
        }

        redirectAttributes.addFlashAttribute("success", "Successfully updated Employee");
        return "employees/list";
    }

    @DeleteMapping("/{id}")
    public String deleteById(HttpServletRequest request, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Delete employee by id: " + id);
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        contractService.deleteById(new Token(token), id);

        redirectAttributes.addFlashAttribute("success", "Successfully deleted Employee");
        return "employees/list";
    }
    */
}
