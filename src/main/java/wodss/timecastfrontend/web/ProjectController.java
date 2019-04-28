package wodss.timecastfrontend.web;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wodss.timecastfrontend.domain.*;
import wodss.timecastfrontend.exceptions.*;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.ProjectService;
import wodss.timecastfrontend.security.JwtUtil;

@Controller
@RequestMapping(value="/projects")
public class ProjectController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private final AllocationService allocationService;
    private final JwtUtil jwtUtil;

    @Autowired
    public ProjectController(ProjectService projectService, AllocationService allocationService,
							 EmployeeService employeeService, JwtUtil jwtUtil) {
        this.projectService = projectService;
        this.employeeService = employeeService;
        this.allocationService = allocationService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping()
    public String getAll(@RequestParam(value = "fromDate", required = false) String fromDateString,
						 @RequestParam(value = "toDate", required = false) String toDateString, Model model) {
        logger.debug("Get all projects");
    	List<Project> projects;
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if ("".equals(fromDateString) && "".equals(toDateString)) {
			projects = projectService.getAll(token);
		} else {
			projects = projectService.getProjects(token, fromDateString, toDateString);
		}
		logger.debug("Projects: " + projects);
		model.addAttribute("projects", projects);
		
        return "projects/list";
    }
    
    @GetMapping(params = "form")
	public String createProjectForm(Model model) {
		model.addAttribute("project", new Project());
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Employee me = jwtUtil.getEmployeeFromToken(token);
		if (me.getRole() != Role.ADMINISTRATOR) {
			throw new TimecastForbiddenException("Not enough permissions to create new projects");
		}

		List<Employee> projectManagers = employeeService.getAll(token)
				.stream()
				.filter(e -> e.getRole() == Role.PROJECTMANAGER)
				.collect(Collectors.toList());
		logger.debug("managers: {}", projectManagers);
		model.addAttribute("managers", projectManagers);
		logger.debug("Init manager: " + (projectManagers.size() == 0 ? "None" : projectManagers.get(0).getId()));
		return "projects/create";
	}

    @PostMapping()
    public String createProject(@Valid @ModelAttribute("project") Project project,
								@ModelAttribute("projectManagerId") Long projectManagerId, BindingResult bindingResult,
								RedirectAttributes redirectAttributes) {
    	if (bindingResult.hasErrors()) {
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "projects/create";
		}
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		logger.debug("Manager selected: " + projectManagerId);
		Employee em = employeeService.getById(token, projectManagerId);
		project.setProjectManager(em);
		projectService.create(token, project);
		redirectAttributes.addFlashAttribute("success", "Successfully created Project.");
    	return "redirect:/projects";
    }

	@GetMapping(value = "/{id}")
	public String getProjectById(@PathVariable long id, Model model) {
		logger.debug("Get project by id: " + id);
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Project project = projectService.getById(token, id);
		Employee me = jwtUtil.getEmployeeFromToken(token);

		if (me.getRole() == Role.PROJECTMANAGER && me.getId() != project.getProjectManager().getId()
				|| me.getRole() == Role.DEVELOPER) {
			// do only show information without edit possibility
			model.addAttribute("project", project);
			return "projects/show";
		}

		List<Employee> projectManagers = employeeService.getAll(token)
				.stream()
				.filter(e -> e.getRole() == Role.PROJECTMANAGER)
				.collect(Collectors.toList());
		model.addAttribute("project", project);
		model.addAttribute("managers", projectManagers);
		return "projects/update";
	}

	@PutMapping(value = "/{id}")
	public String update(@PathVariable long id, @ModelAttribute("projectManagerId") Long projectManagerId, Model model,
						 @Valid Project project, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		// Prevent update if there exist allocations with the same contract.
		List<Allocation> allocations = allocationService.getAllocations(token, -1, id, null, null);
		if (allocations.size() > 0) {
			logger.debug("Cannot change Project when there are allocations referencing to this project.");
			model.addAttribute("exception", "Cannot change Project when there are allocations referencing to this project.");
			return "projects/update";
		}

		if (bindingResult.hasErrors()) {
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "projects/update";
		}

		project.setId(id);
		Employee projectManager = employeeService.getById(token, projectManagerId);
		project.setProjectManager(projectManager);
		projectService.update(token, project);
		redirectAttributes.addFlashAttribute("success", "Successfully deleted Project.");
		return "redirect:/projects";
	}
	
	@DeleteMapping(value = "/{id}")
	public String delete(@PathVariable long id, RedirectAttributes redirectAttributes) {
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		projectService.deleteById(token, id);
		redirectAttributes.addFlashAttribute("success", "Successfully deleted Project.");
		return "redirect:/projects";
	}
}


