package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.ProjectService;
import wodss.timecastfrontend.security.JwtUtil;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtUtil jwtUtil;
    private final EmployeeService employeeService;
    private final ContractService contractService;
    private final ProjectService projectService;
    private final AllocationService allocationService;

    @Autowired
    public HomeController(JwtUtil jwtUtil, EmployeeService employeeService, ContractService contractService,
                          ProjectService projectService, AllocationService allocationService) {
        this.jwtUtil = jwtUtil;
        this.employeeService = employeeService;
        this.contractService = contractService;
        this.projectService = projectService;
        this.allocationService = allocationService;
    }

    @GetMapping
    public String get() {
        return "redirect:/projects";
    }

    @GetMapping("login")
    public String getLoginForm() {
        logger.debug("Request Login form");
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        try {
            Employee employee = jwtUtil.getEmployeeFromToken(token);
            if (employee != null) {
                // already logged in
                return "redirect:/";
            }
        } catch (Exception ex) {
            // ignore exception since it's only for checking if already logged in.
        }
        return "login";
    }

    @GetMapping("about")
    public String getAboutPage() {
        logger.debug("Request About page");
        return "about";
    }

    @GetMapping("myprofile")
    public String getProfile(Model model) {
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Employee employee = jwtUtil.getEmployeeFromToken(token);
        Employee me = employeeService.getById(token, employee.getId());
        List<Contract> contracts = contractService.getByEmployee(token, me);

        model.addAttribute("employee", me);
        model.addAttribute("contracts", contracts);

        return "myprofile";
    }

    @GetMapping("myprojects")
    public String getMyProjects(Model model) {
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Employee employee = jwtUtil.getEmployeeFromToken(token);
        Employee me = employeeService.getById(token, employee.getId());
        List<Allocation> allocations = allocationService.getAllocations(token, me.getId(), -1, null, null);
        List<Project> projects = allocations.stream()
                .map(a -> projectService.getById(token, a.getProject().getId()))
                .collect(Collectors.toList());
        model.addAttribute("projects", projects);
        return "projects/list";
    }
}
