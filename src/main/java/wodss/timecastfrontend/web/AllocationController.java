package wodss.timecastfrontend.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.exceptions.TimecastNotFoundException;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.AllocationService;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.EmployeeService;
import wodss.timecastfrontend.services.ProjectService;
import wodss.timecastfrontend.util.AllocationChecker;

@Controller
@RequestMapping(value = "/allocations")
public class AllocationController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final AllocationService allocationService;
	private final ProjectService projectService;
	private final EmployeeService employeeService;
	private final ContractService contractService;

	@Autowired
	public AllocationController(AllocationService allocationService, ProjectService projectService,
			EmployeeService employeeService, ContractService contractService) {
		this.allocationService = allocationService;
		this.projectService = projectService;
		this.employeeService = employeeService;
		this.contractService = contractService;
	}

	@GetMapping()
	public String getAll(@RequestParam(value = "employeeId", required = false) Long employeeId,
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value = "fromDate", required = false) String fromDateString,
			@RequestParam(value = "toDate", required = false) String toDateString, Model model) {
		logger.debug("Get allocations with params {} {} {} {}", employeeId, projectId, fromDateString, toDateString);
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		List<Project> projects = projectService.getAll(token);
		List<Allocation> allocations;
		long projId = projectId == null ? projects.get(0).getId() : projectId;
		long emplId = employeeId == null ? -1 : employeeId;
		Date fromDate = null;
		Date toDate = null;
		DateFormat domainFormat = new SimpleDateFormat("dd.MM.yyyy");

		try {
			if (null != fromDateString && !"".equals(fromDateString)) {
				fromDate = domainFormat.parse(fromDateString);
			}
			if (null != toDateString && !"".equals(toDateString)) {
				toDate = domainFormat.parse(toDateString);
			}
		} catch (ParseException ex) {
			logger.debug("wrong date format");
			throw new IllegalStateException();
		}

		allocations = allocationService.getAllocations(token, emplId, projId, fromDate, toDate);
		Project selectedProject = projectService.getById(token, projId);

		long selectedProjectAssignedFtes = allocations.stream()
				.map(Allocation::getPensumPercentage)
				.reduce(0, (p1, p2) -> p1 + p2);

		model.addAttribute("projects", projects);
		model.addAttribute("projectIdFilter", projId);
		model.addAttribute("fromDateFilter", fromDateString);
		model.addAttribute("employeeIdFilter", employeeId);
		model.addAttribute("toDateFilter", toDateString);
		model.addAttribute("selectedProject", selectedProject);
		model.addAttribute("selectedProjectAssignedFtes", selectedProjectAssignedFtes);
		model.addAttribute("allocations", allocations);

		return "allocations/list";
	}

	@GetMapping(params = "form")
	public String createAllocationForm(@RequestParam(value = "projectId", required = true) Long projectId,
			Model model) {
		Allocation allocation = new Allocation();
		long selectedProjectAssignedFtes = 0;
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (projectId != null) {
			allocation.setProject(projectService.getById(token, projectId));
			List<Allocation> allocations = allocationService.getAllocations(token, -1, projectId, null, null);
			selectedProjectAssignedFtes = allocations.stream()
					.map(Allocation::getPensumPercentage)
					.reduce(0, (p1, p2) -> p1 + p2);
		}

		this.setDevelopersIntoModel(model, token);
		model.addAttribute("allocation", allocation);
		model.addAttribute("selectedProjectAssignedFtes", selectedProjectAssignedFtes);
		return "allocations/create";
	}

	@PostMapping()
	public String createAllocation(@Valid @ModelAttribute("allocation") Allocation allocation,
								   @ModelAttribute("employeeId") Long employeeId, BindingResult bindingResult,
								   Model model, RedirectAttributes redirectAttributes) {
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if (bindingResult.hasErrors()) {
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			this.setDevelopersIntoModel(model, token);
			return "allocations/create";
		}

		try {
			Project p = projectService.getById(token, allocation.getProject().getId());
			allocation.setStartDate(normalizeDate(allocation.getStartDate()));
			allocation.setEndDate(normalizeDate(allocation.getEndDate()));
			if (allocation.getStartDate().getTime() > allocation.getEndDate().getTime()) {
				// allocation startDate is after endDate
				logger.debug("Allocation Start Date after End Date");
				this.setDevelopersIntoModel(model, token);
				model.addAttribute("exception", "Invalid Input. Start Date must be before End Date.");
				return "allocations/create";
			}
			if (allocation.getStartDate().getTime() < p.getStartDate().getTime() // allocation starts before project
					|| allocation.getEndDate().getTime() > p.getEndDate().getTime()) { // allocation ends after project
				logger.debug("Allocation not in project time span");
				this.setDevelopersIntoModel(model, token);
				model.addAttribute("exception", "Invalid Input. Allocation must be in between the project time span.");
				return "allocations/create";
			}

			Employee e = employeeService.getById(token, employeeId);
			List<Allocation> allocations = allocationService.getAllocations(token, e.getId(), p.getId()	, null, null);
			List<Contract> contracts = contractService.getByEmployee(token, e);
			allocation.setProject(p);

			try {
				this.createAllocations(token, allocation, contracts, allocations);
			} catch (IllegalStateException ex) {
				this.setDevelopersIntoModel(model, token);
				model.addAttribute("exception", ex.getMessage());
				return "allocations/create";
			}
		} catch (TimecastPreconditionFailedException e) {
			this.setDevelopersIntoModel(model, token);
			model.addAttribute("exception", "Invalid Input. Please Check all fields.");
			return "allocations/create";
		}

		redirectAttributes.addFlashAttribute("success", "Successfully created Allocation.");
		return "redirect:/allocations";
	}

	@GetMapping(value = "/{id}")
	public String updateAllocationForm(@PathVariable long id, Model model) {
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Allocation allocation = allocationService.getById(token, id);

		List<Allocation> allocations = allocationService.getAllocations(token, -1, allocation.getProject().getId(), null, null);
		long selectedProjectAssignedFtes = allocations.stream()
				.map(Allocation::getPensumPercentage)
				.reduce(0, (p1, p2) -> p1 + p2);

		this.setDevelopersIntoModel(model, token);
		model.addAttribute("allocation", allocation);
		model.addAttribute("selectedProjectAssignedFtes", selectedProjectAssignedFtes);
		return "allocations/update";
	}

	@PutMapping(value = "/{id}")
	public String update(@PathVariable long id, @ModelAttribute("employeeId") Long employeeId,
						 @Valid Allocation allocation, BindingResult bindingResult, Model model,
						 RedirectAttributes redirectAttributes) {
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		List<Allocation> projectAllocations = allocationService.getAllocations(token, -1, allocation.getProject().getId(), null, null);
		long selectedProjectAssignedFtes = projectAllocations.stream()
				.map(Allocation::getPensumPercentage)
				.reduce(0, (p1, p2) -> p1 + p2);

		if (bindingResult.hasErrors()) {
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			this.setDevelopersIntoModel(model, token);
			model.addAttribute("selectedProjectAssignedFtes", selectedProjectAssignedFtes);
			return "allocations/update";
		}

		try {
			Project p = projectService.getById(token, allocation.getProject().getId());
			allocation.setStartDate(normalizeDate(allocation.getStartDate()));
			allocation.setEndDate(normalizeDate(allocation.getEndDate()));
			if (allocation.getStartDate().getTime() > allocation.getEndDate().getTime()) {
				// allocation startDate is after endDate
				logger.debug("Allocation Start Date after End Date");
				this.setDevelopersIntoModel(model, token);
				model.addAttribute("selectedProjectAssignedFtes", selectedProjectAssignedFtes);
				model.addAttribute("exception", "Invalid Input. Start Date must be before End Date.");
				return "allocations/update";
			}
			if (allocation.getStartDate().getTime() < p.getStartDate().getTime() // allocation starts before project
					|| allocation.getEndDate().getTime() > p.getEndDate().getTime()) { // allocation ends after project
				logger.debug("Allocation not in project time span");
				this.setDevelopersIntoModel(model, token);
				model.addAttribute("selectedProjectAssignedFtes", selectedProjectAssignedFtes);
				model.addAttribute("exception", "Invalid Input. Allocation must be in between the project time span.");
				return "allocations/update";
			}

			Employee e = employeeService.getById(token, employeeId);

			List<Allocation> allocations = allocationService.getAllocations(token, e.getId(), p.getId()	, null, null);
			List<Contract> contracts = contractService.getByEmployee(token, e);
			allocation.setProject(p);

			try {
				this.updateAllocations(token, allocation, contracts, allocations, id);
			} catch (IllegalStateException ex) {
				this.setDevelopersIntoModel(model, token);
				model.addAttribute("selectedProjectAssignedFtes", selectedProjectAssignedFtes);
				model.addAttribute("exception", ex.getMessage());
				return "allocations/update";
			}
		} catch (TimecastPreconditionFailedException e) {
			this.setDevelopersIntoModel(model, token);
			model.addAttribute("selectedProjectAssignedFtes", selectedProjectAssignedFtes);
			model.addAttribute("exception", "Invalid Input. Please Check all fields.");
			return "allocations/update";
		}

		redirectAttributes.addFlashAttribute("success", "Successfully updated Allocation.");
		return "redirect:/allocations/" + id;
	}

	@DeleteMapping(value = "/{id}")
	public String delete(@PathVariable long id, RedirectAttributes redirectAttributes) {
		Token token = new Token((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		allocationService.deleteById(token, id);

		redirectAttributes.addFlashAttribute("success", "Successfully deleted Allocation.");
		return "redirect:/allocations";
	}
	
	private void createAllocations(Token token, Allocation newAllocation, List<Contract> contracts, List<Allocation> existingAllocations) {
		if (contracts.size() == 0) {
			throw new IllegalStateException("Cannot create an allocation for an employee with no available contract");
		}

		AllocationChecker checker = new AllocationChecker();
		List<Contract> relevantContracts = checker.filterRelevantContracts(newAllocation, contracts);
		if (relevantContracts.isEmpty()) {
			logger.debug("No relevant contracts or allocation is invalid");
			throw new IllegalStateException("No relevant contracts or allocation is invalid");
		}
		
		List<Allocation> newAllocations = checker.computeAllocations(newAllocation, relevantContracts, existingAllocations);
		
		for (Allocation alloc : newAllocations) {
			allocationService.create(token, alloc);
		}
	}

	private void updateAllocations(Token token, Allocation newAllocation, List<Contract> contracts,
								   List<Allocation> existingAllocations, long allocationId) {
		if (contracts.size() == 0) {
			throw new IllegalStateException("Cannot create an allocation for an employee with no available contract");
		}
		AllocationChecker checker = new AllocationChecker();
		List<Contract> relevantContracts = checker.filterRelevantContracts(newAllocation, contracts);
		if (relevantContracts.isEmpty()) {
			logger.debug("No relevant contracts or allocation is invalid");
			throw new IllegalStateException("No relevant contracts or allocation is invalid");
		}

		List<Allocation> newAllocations = checker.computeAllocations(newAllocation, relevantContracts, existingAllocations);

		if (newAllocations == null || newAllocations.size() == 0) {
			throw new IllegalStateException("");
		}
		if (newAllocations.size() == 1) {
			Allocation allocation = newAllocations.get(0);
			allocation.setId(allocationId);
			allocationService.update(token, allocation);
		} else {
			Allocation first = newAllocations.get(0);
			first.setId(allocationId);
			allocationService.update(token, first);
			for (int i = 1; i < newAllocations.size(); i++) {
				allocationService.create(token, newAllocations.get(i));
			}
		}
	}
	
	private Date normalizeDate(Date date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd");
			return formatter.parse(formatter.format(date));
		} catch (ParseException e) {
			throw new TimecastInternalServerErrorException(e.getMessage());
		}
	}

	private void setDevelopersIntoModel(Model model, Token token) {
		List<Employee> developers = employeeService.getAll(token).stream()
				.filter(emp -> emp.getRole() == Role.DEVELOPER)
				.collect(Collectors.toList());
		model.addAttribute("developers", developers);
	}
}
