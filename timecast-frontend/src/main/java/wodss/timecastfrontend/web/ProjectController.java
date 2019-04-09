package wodss.timecastfrontend.web;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.ProjectService;
import wodss.timecastfrontend.services.mocks.MockProjectService;

@Controller
@RequestMapping(value="/projects")
public class ProjectController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProjectService projectService;

    //TODO activate when backend is ready
//    @Autowired
//    public ProjectController(ProjectService projectService) {
//        this.projectService = projectService;
//    }
    
    //Used for early testing
    public ProjectController() {
    	projectService = new MockProjectService(null, "");
    }

    @GetMapping()
    public String getAll(@RequestParam(value = "fromDate", required = false) String fromDateString, @RequestParam(value = "toDate", required = false) String toDateString, Model model) {
        logger.debug("Get all projects");
    	List<Project> projects;
		try {
			//TODO check
			if ("".equals(fromDateString) && "".equals(toDateString)) {
				projects = projectService.getAll();
			} else {
				projects = projectService.getProjects(fromDateString, toDateString);
			}
			
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
    	try {
			project = projectService.getById(id);
			model.addAttribute("project", project);
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
		return "projects/create";
	}
    @PostMapping()
    public String createProject(@Valid @ModelAttribute("project") Project project, BindingResult bindingResult, Model model) {
    	if (bindingResult.hasErrors()) {
    		//TODO
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "projects/create";
		}
    	try {
			projectService.create(project);
		} catch (TimecastPreconditionFailedException | TimecastForbiddenException
				| TimecastInternalServerErrorException e) {
			// TODO handle error
			e.printStackTrace();
		}
    	
    	return "redirect:/projects";
    }
    
    @GetMapping(value = "/{id}", params = "form")
	public String updateProjectForm(@PathVariable long id, Model model) {
		try {
			Project project = projectService.getById(id);
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
			try {
				projectService.update(project);
			} catch (TimecastNotFoundException | TimecastPreconditionFailedException | TimecastForbiddenException
					| TimecastInternalServerErrorException e) {
				// TODO handle error
				e.printStackTrace();
			}
		return "redirect:/projects";
	}
	
	@DeleteMapping(value = "/{id}")
	public String delete(@PathVariable long id) {
		try {
			projectService.deleteById(id);
		} catch (TimecastInternalServerErrorException | TimecastForbiddenException | TimecastNotFoundException e) {
			// TODO handle error
			e.printStackTrace();
		}
		
		return "redirect:/projects";
	}
}


