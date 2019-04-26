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

    //TODO activate when backend is ready
//    @Autowired
//    public ProjectController(ProjectService projectService) {
//        this.projectService = projectService;
//    }
    
    //Used for early testing
    public ProjectController() {
    	projectService = new MockProjectService(null, "");
    	allocationService = new MockAllocationService(null, "");
    	employeeService = new MockEmployeeService(null, "");
    	contractService = new MockContractService(null, "");
    }

    @GetMapping()
    public String getAll(@RequestParam(value = "fromDate", required = false) String fromDateString, @RequestParam(value = "toDate", required = false) String toDateString, Model model) {
        logger.debug("Get all projects");
    	List<Project> projects;
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			//TODO check
			if ("".equals(fromDateString) && "".equals(toDateString)) {
				projects = projectService.getAll(new Token(token));
			} else {
				projects = projectService.getProjects(new Token(token), fromDateString, toDateString);
			}
			logger.debug("Projects: " + projects);
			model.addAttribute("projects", projects);
		} catch (TimecastNotFoundException | TimecastInternalServerErrorException e) {
			// TODO Handle error
			e.printStackTrace();
		}
		
        return "projects/list";
    }
    
    @GetMapping(value = "/{id}")
	public String getProjectById(@PathVariable long id, Model model) {
    	logger.debug("Get project by id: " + id);
    	Project project;
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	try {
			project = projectService.getById(new Token(token), id);
			model.addAttribute("project", project);
			model.addAttribute("allocations", allocations);
		} catch (TimecastNotFoundException | TimecastInternalServerErrorException | TimecastForbiddenException e) {
			// TODO handle error
			e.printStackTrace();
		}
    	
    	//TODO check
    	return "projects/show";
    }
    
    @GetMapping(params = "form")
	public String createProjectForm(Model model) {
		model.addAttribute("project", new Project());
		List<Employee> projectManagers = getProjectManagers();
		logger.debug("managers: {}", projectManagers);
		model.addAttribute("managers", projectManagers);
		return "projects/create";
	}
    @PostMapping()
    public String createProject(@Valid @ModelAttribute("project") Project project, BindingResult bindingResult, Model model) {
    	if (bindingResult.hasErrors()) {
    		//TODO
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "projects/create";
		}
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	try {
			projectService.create(new Token(token), project);
		} catch (TimecastPreconditionFailedException | TimecastForbiddenException
				| TimecastInternalServerErrorException e) {
			// TODO handle error
			e.printStackTrace();
		}
    	
    	return "redirect:/projects";
    }
    
    @GetMapping(value = "/{id}", params = "form")
	public String updateProjectForm(@PathVariable long id, Model model) {
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			Project project = projectService.getById(new Token(token), id);
			model.addAttribute("project", project);
			return "projects/update";
		} catch (TimecastNotFoundException | TimecastInternalServerErrorException | TimecastForbiddenException e) {
			// TODO handle error
			e.printStackTrace();
		}
		//TODO
		return "404";
		
	}

	@PutMapping(value = "/{id}")
	public String update(@PathVariable long id, @Valid Project project, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "project/update";
		}
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			projectService.update(new Token(token), project);
		} catch (TimecastNotFoundException | TimecastPreconditionFailedException | TimecastForbiddenException
				| TimecastInternalServerErrorException e) {
			// TODO handle error
			e.printStackTrace();
		}
		return "redirect:/projects";
	}
	
	@DeleteMapping(value = "/{id}")
	public String delete(@PathVariable long id) {
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			projectService.deleteById(new Token(token), id);
		} catch (TimecastInternalServerErrorException | TimecastForbiddenException | TimecastNotFoundException e) {
			// TODO handle error
			e.printStackTrace();
		}
		
		return "redirect:/projects";
	}
	
	private List<Project> getProjects(List<ProjectDTO> projectDtos) {
		List<Project> projects = new ArrayList<Project>();
		for (ProjectDTO projectDTO : projectDtos) {
			projects.add(getProject(projectDTO));
		}
		
		return projects;
	}
	
	private Project getProject(ProjectDTO projectDto) {
		//TODO improve
		Project p = new Project(projectDto.getName(), projectDto.getFtePercentage(), projectDto.getStartDate(), projectDto.getEndDate());
		p.setId(projectDto.getId());
		EmployeeDTO eDto = employeeService.getById(projectDto.getProjectManagerId());
		Employee e = new Employee(eDto.isActive(), eDto.getLastName(), eDto.getFirstName(), eDto.getEmailAddress(), eDto.getRole());
		p.setProjectManager(e);
		return p;
	}
	
	private List<Allocation> getAllocations(List<AllocationDTO> allocationDtos, Project project) {
		List<Allocation> allocations = new ArrayList<Allocation>();
		for (AllocationDTO allocDto : allocationDtos) {
			Allocation alloc = new Allocation(allocDto.getStartDate(), allocDto.getEndDate(), allocDto.getPensumPercentage());
			alloc.setId(allocDto.getId());
			alloc.setProject(project);
			ContractDTO contractDto = contractService.getById(allocDto.getContractId());
			Contract contract = new Contract(contractDto.getStartDate(), contractDto.getEndDate(), contractDto.getPensumPercentage());
			contract.setId(contractDto.getId());
			EmployeeDTO eDto = employeeService.getById(contractDto.getEmployeeId());
			Employee e = new Employee(eDto.isActive(), eDto.getLastName(), eDto.getFirstName(), eDto.getEmailAddress(), eDto.getRole());
			e.setId(eDto.getId());
			contract.setEmployee(e);
			alloc.setContract(contract);
			allocations.add(alloc);
		}
		
		return allocations;
	}
	
	private ProjectDTO getProjectDto(Project project) {
		ProjectDTO projectDto = new ProjectDTO();
		projectDto.setName(project.getName());
		projectDto.setStartDate(project.getStartDate());
		projectDto.setEndDate(project.getEndDate());
		projectDto.setFtePercentage(project.getFtePercentage());
		projectDto.setProjectManagerId(project.getProjectManager().getId());
		return projectDto;
	}
	
	private List<Employee> getProjectManagers() {
		List<Employee> managers = new ArrayList<Employee>();
		List<EmployeeDTO> eDtos = employeeService.getAll().stream().filter(e -> e.getRole().equals("Project Leader")).collect(Collectors.toList());;
		for (EmployeeDTO employeeDTO : eDtos) {
			Employee e = new Employee(employeeDTO.isActive(), employeeDTO.getLastName(), employeeDTO.getFirstName(), employeeDTO.getEmailAddress(), employeeDTO.getRole());
			e.setId(employeeDTO.getId());
			managers.add(e);
		}
		
		return managers;
	}
}


