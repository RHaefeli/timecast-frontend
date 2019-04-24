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
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.mocks.MockEmployeeService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/employees")
public class EmployeeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(MockEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String getAll(HttpServletRequest request, Model model) {
        logger.debug("Get all employees");
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Employee> employees = employeeService.getAll(new Token(token));
        model.addAttribute("employees", employees);

        return "employees/list";
    }

    @GetMapping(params = "form")
    public String createForm(HttpServletRequest request, Model model) {
        logger.debug("Get create employee form");
        model.addAttribute("employee", new Employee());
        return "employees/create";
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
            Employee newEmployee = employeeService.create(new Token(token), employee);
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
        Employee employee = employeeService.getById(new Token(token), id);
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
            Employee updatedEmployee = employeeService.update(new Token(token), employee);
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
        employeeService.deleteById(new Token(token), id);

        redirectAttributes.addFlashAttribute("success", "Successfully deleted Employee");
        return "employees/list";
    }
}
