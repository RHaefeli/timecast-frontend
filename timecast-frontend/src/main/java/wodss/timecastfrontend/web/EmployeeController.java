package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import wodss.timecastfrontend.domain.dto.EmployeeDTO;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.mocks.MockEmployeeService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value="/employees")
public class EmployeeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(MockEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String getAll(Model model) {
        logger.debug("Get all employees");
        List<EmployeeDTO> employees = employeeService.getAll();
        model.addAttribute("employees", employees);

        return "employees/list";
    }

    @GetMapping(params = "form")
    public String createForm(Model model) {
        logger.debug("Get create employee form");
        model.addAttribute("employee", new EmployeeDTO());
        return "employees/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("employee") EmployeeDTO employee, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("Create employee: " + employee);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/create";
        }
        try {
            EmployeeDTO newEmployee = employeeService.create(employee);
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "employees/create";
        }

        redirectAttributes.addFlashAttribute("success", "Success");
        return "redirect:/employees";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Model model) {
        logger.debug("Get employee by id: " + id);
        EmployeeDTO employee = employeeService.getById(id);
        model.addAttribute("employee", employee);

        return "employees/update";
    }

    @PutMapping("/{id}")
    public String updateById(@PathVariable Long id, @Valid @ModelAttribute("employee") EmployeeDTO employee, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        logger.debug("Update employee by id: " + id);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "employees/update";
        }
        try {
            EmployeeDTO updatedEmployee = employeeService.update(employee);
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "employees/update";
        }

        redirectAttributes.addFlashAttribute("success", "Successfully updated Employee");
        return "employees/list";
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Delete employee by id: " + id);
        employeeService.deleteById(id);

        redirectAttributes.addFlashAttribute("success", "Successfully deleted Employee");
        return "employees/list";
    }
}
