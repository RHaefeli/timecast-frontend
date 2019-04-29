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

/**
 * Controls home web component
 * @author YvesSimmen
 *
 */
@Controller
@RequestMapping(value = "/")
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtUtil jwtUtil;
    private final EmployeeService employeeService;
    private final ContractService contractService;
    private final ProjectService projectService;
    private final AllocationService allocationService;

    /**
     * Constructor
     * @param jwtUtil
     * @param employeeService
     * @param contractService
     * @param projectService
     * @param allocationService
     */
    @Autowired
    public HomeController(JwtUtil jwtUtil, EmployeeService employeeService, ContractService contractService,
                          ProjectService projectService, AllocationService allocationService) {
        this.jwtUtil = jwtUtil;
        this.employeeService = employeeService;
        this.contractService = contractService;
        this.projectService = projectService;
        this.allocationService = allocationService;
    }

    /**
     * Get home page
     * @return pagelink
     */
    @GetMapping
    public String get() {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!token.equals("anonymousUser")) {
            Employee me = jwtUtil.getEmployeeFromToken(new Token(token));
            if (me.getRole() == Role.DEVELOPER) {
                return "redirect:/myprojects";
            }
        }
        return "redirect:/projects";
    }

    /**
     * Shows login form
     * @return pagelink
     */
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

    /**
     * Shows about page
     * @return page link
     */
    @GetMapping("about")
    public String getAboutPage() {
        logger.debug("Request About page");
        return "about";
    }

    /**
     * Show profile page of logged in user
     * @param model
     * @return pagelink
     */
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

    /**
     * Show projects of logged in user
     * @param model
     * @return pagelink
     */
    @GetMapping("myprojects")
    public String getMyProjects(Model model) {
        Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Employee employee = jwtUtil.getEmployeeFromToken(token);
        Employee me = employeeService.getById(token, employee.getId());
        List<Allocation> allocations = allocationService.getAllocations(token, me.getId(), -1, null, null);
        List<Project> projects = allocations.stream()
                .map(a -> a.getProject())
                .collect(Collectors.toList());
        model.addAttribute("projects", projects);
        return "myprojects";
    }
}
