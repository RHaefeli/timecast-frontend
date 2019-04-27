package wodss.timecastfrontend.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
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

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.ProjectService;
import wodss.timecastfrontend.services.mocks.MockAllocationService;
import wodss.timecastfrontend.services.mocks.MockContractService;
import wodss.timecastfrontend.services.mocks.MockEmployeeService;
import wodss.timecastfrontend.services.mocks.MockProjectService;

@Controller
@RequestMapping(value="/projects")
public class ProjectController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectService projectService;
    private final AllocationService allocationService;
    private final EmployeeService employeeService;
    private final ContractService contractService;

    @Autowired
    public ProjectController(ProjectService projectService, AllocationService allocationService, EmployeeService employeeService, ContractService contractService) {
        this.projectService = projectService;
        this.allocationService = allocationService;
        this.employeeService = employeeService;
        this.contractService = contractService;
    }

    @GetMapping()
    public String getAll(@RequestParam(value = "fromDate", required = false) String fromDateString, @RequestParam(value = "toDate", required = false) String toDateString, Model model) {
        logger.debug("Get all projects");
    	List<Project> projects;
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if ("".equals(fromDateString) && "".equals(toDateString)) {
			projects = projectService.getAll(new Token(token));
		} else {
			projects = projectService.getProjects(new Token(token), fromDateString, toDateString);
		}
		logger.debug("Projects: " + projects);
		model.addAttribute("projects", projects);
		
        return "projects/list";
    }
    
    @GetMapping(value = "/{id}")
	public String getProjectById(@PathVariable long id, Model model) {
    	logger.debug("Get project by id: " + id);
    	Project project;
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		project = projectService.getById(new Token(token), id);
		List<Allocation> allocations = allocationService.getAllocations(new Token(token), -1, project.getId(), null, null);
		model.addAttribute("project", project);
		model.addAttribute("allocations", allocations);

    	return "projects/show";
    }
    
    @GetMapping(params = "form")
	public String createProjectForm(Model model) {
		model.addAttribute("project", new Project());
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Employee> projectManagers = employeeService.getAll(new Token(token))
				.stream()
				.filter(e -> e.getRole() == Role.PROJECTMANAGER)
				.collect(Collectors.toList());
		logger.debug("managers: {}", projectManagers);
		model.addAttribute("managers", projectManagers);
		logger.debug("Init manager: " + (projectManagers.size() == 0 ? "None" : projectManagers.get(0).getId()));
		return "projects/create";
	}
    @PostMapping()
    public String createProject(@Valid @ModelAttribute("project") Project project, BindingResult bindingResult, Model model) {
    	if (bindingResult.hasErrors()) {
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "projects/create";
		}
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		logger.debug("Manager selected: " + project.getProjectManager().getId());
		Employee em = employeeService.getById(new Token(token), project.getProjectManager().getId());
		project.setProjectManager(em);
		projectService.create(new Token(token), project);
    	
    	return "redirect:/projects";
    }
    
    @GetMapping(value = "/{id}", params = "form")
	public String updateProjectForm(@PathVariable long id, Model model) {
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Project project = projectService.getById(new Token(token), id);
		model.addAttribute("project", project);
		List<Employee> projectManagers = employeeService.getAll(new Token(token)).stream().filter(e -> e.getRole() == Role.PROJECTMANAGER).collect(Collectors.toList());
		model.addAttribute("managers", projectManagers);
		return "projects/update";
	}

	@PutMapping(value = "/{id}")
	public String update(@PathVariable long id, @Valid Project project, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "project/update";
		}
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Employee projectManager = employeeService.getById(new Token(token), project.getProjectManager().getId());
		project.setProjectManager(projectManager);
		projectService.update(new Token(token), project);
		return "redirect:/projects";
	}
	
	@DeleteMapping(value = "/{id}")
	public String delete(@PathVariable long id) {
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		projectService.deleteById(new Token(token), id);
		return "redirect:/projects";
	}
}


