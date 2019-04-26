package wodss.timecastfrontend.web;

import java.util.List;

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
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastForbiddenException;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ProjectService;
import wodss.timecastfrontend.services.mocks.MockAllocationService;
import wodss.timecastfrontend.services.mocks.MockProjectService;

@Controller
@RequestMapping(value="/allocations")
public class AllocationController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AllocationService allocationService;
    private final ProjectService projectService;
    private String filterStartDate;
    private String filterEndDate;

    //TODO activate when backend is ready
    @Autowired
    public AllocationController(MockAllocationService allocationService, MockProjectService projectService) {
        this.allocationService = allocationService;
        this.projectService = projectService;
    }
    
    @GetMapping()
    public String getAll(@RequestParam(value = "employeeId", required = false) Long employeeId, @RequestParam(value = "projectId", required = false) Long projectId, @RequestParam(value = "fromDate", required = false) String fromDateString, @RequestParam(value = "toDate", required = false) String toDateString, Model model) {
    	logger.debug("Get allocations with params {} {} {} {}", employeeId, projectId, fromDateString, toDateString);
		model.addAttribute("projectIdFilter", projectId);
		model.addAttribute("fromDateFilter", fromDateString);
		model.addAttribute("employeeIdFilter", employeeId);
		model.addAttribute("toDateFilter", toDateString);
    	List<Allocation> allocations;
    	try {
    		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			//TODO check
			if ("".equals(fromDateString) && "".equals(toDateString) && null == employeeId && null == projectId) {
				allocations = allocationService.getAll(new Token(token));
			} else {
				//TODO fix
				if (employeeId == null) {
					employeeId = (long) -1;
				}
				if (projectId == null) {
					projectId = (long) -1;
				}
 				allocations = allocationService.getAllocations(new Token(token), Long.valueOf(employeeId), Long.valueOf(projectId), fromDateString, toDateString);
			}
			
			model.addAttribute("allocations", allocations);
		} catch (TimecastNotFoundException | TimecastInternalServerErrorException e) {
			// TODO Handle error
			e.printStackTrace();
		}
		return "allocations/list";
    }
    
    @GetMapping(params = "form")
	public String createAllocationForm(@RequestParam(value = "projectId", required = false) Long projectId, Model model) {
    	Allocation allocation = new Allocation();
    	if (projectId != null) {
    		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    		allocation.setProject(projectService.getById(new Token(token), projectId));
    	}
		model.addAttribute("allocation", allocation);
		return "allocations/create";
	}
    
    @PostMapping()
    public String createAllocation(@Valid @ModelAttribute("allocation") Allocation allocation, BindingResult bindingResult, Model model) {
    	if (bindingResult.hasErrors()) {
    		//TODO
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "allocations/create";
		}
    	try {
    		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			allocationService.create(new Token(token), allocation);
		} catch (TimecastPreconditionFailedException | TimecastForbiddenException
				| TimecastInternalServerErrorException e) {
			// TODO handle error
			e.printStackTrace();
		}
    	
    	return "redirect:/allocations";
    }
    
    @GetMapping(value = "/{id}", params = "form")
	public String updateAllocationForm(@PathVariable long id, Model model) {
		try {
			String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Allocation allocation = allocationService.getById(new Token(token), id);
			model.addAttribute("allocation", allocation);
			return "allocations/update";
		} catch (TimecastNotFoundException | TimecastInternalServerErrorException | TimecastForbiddenException e) {
			// TODO handle error
			e.printStackTrace();
		}
		//TODO
		return "404";
		
	}

	@PutMapping(value = "/{id}")
	public String update(@PathVariable long id, @Valid Allocation allocation, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "allocations/update";
		}
			try {
				String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				allocationService.update(new Token(token), allocation);
			} catch (TimecastNotFoundException | TimecastPreconditionFailedException | TimecastForbiddenException
					| TimecastInternalServerErrorException e) {
				// TODO handle error
				e.printStackTrace();
			}
		return "redirect:/allocations";
	}
	
	@DeleteMapping(value = "/{id}")
	public String delete(@PathVariable long id) {
		try {
			String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			allocationService.deleteById(new Token(token), id);
		} catch (TimecastInternalServerErrorException | TimecastForbiddenException | TimecastNotFoundException e) {
			// TODO handle error
			e.printStackTrace();
		}
		
		return "redirect:/allocations";
	}

}