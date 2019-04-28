package wodss.timecastfrontend.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping(value = "/allocations")
public class AllocationController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final AllocationService allocationService;
	private final ProjectService projectService;
	private final EmployeeService employeeService;
	private final ContractService contractService;
	private String filterStartDate;
	private String filterEndDate;

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
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (projectId != null) {
			allocation.setProject(projectService.getById(new Token(token), projectId));
		}
		List<Employee> employees = employeeService.getAll(new Token(token));
		List<Employee> developers = employees.stream().filter(e -> e.getRole() == Role.DEVELOPER)
				.collect(Collectors.toList());
		model.addAttribute("developers", developers);
		model.addAttribute("allocation", allocation);
		return "allocations/create";
	}

	@PostMapping()
	public String createAllocation(@Valid @ModelAttribute("allocation") Allocation allocation,
			@ModelAttribute("employeeId") Long employeeId, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			// TODO
			logger.debug("Binding error: " + bindingResult.getAllErrors());
			return "allocations/create";
		}
		try {
			String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Project p = projectService.getById(new Token(token), allocation.getProject().getId());
			allocation.setStartDate(normalizeDate(allocation.getStartDate()));
			allocation.setEndDate(normalizeDate(allocation.getEndDate()));
			if (allocation.getStartDate().getTime()<(p.getStartDate().getTime()) || allocation.getEndDate().getTime()>(p.getEndDate().getTime())
					|| allocation.getStartDate().getTime()>(p.getEndDate().getTime()) || allocation.getEndDate().getTime()<(p.getStartDate().getTime())) {
				logger.debug("Allocation not in project time span");
				//TODO error handling
				throw new IllegalStateException();
			}
			Employee e = employeeService.getById(new Token(token), employeeId);
			List<Allocation> allocations = allocationService.getAllocations(new Token(token), e.getId(), p.getId()	, null, null);
			List<Contract> contracts = contractService.getByEmployee(new Token(token), e);
			allocation.setProject(p);
			// TODO get fitting contract
			createAllocations(new Token(token), allocation, contracts, allocations);
			//allocation.setContract(c);
//			allocationService.create(new Token(token), allocation);
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
		// TODO
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
		String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		allocationService.deleteById(new Token(token), id);

		return "redirect:/allocations";
	}
	
	private void createAllocations(Token token, Allocation newAllocation, List<Contract> contracts, List<Allocation> existingAllocations) {
		DateChegga dC = new DateChegga();
		List<Contract> relevantContracts = dC.filterRelevantContracts(newAllocation, contracts);
		if (relevantContracts.isEmpty()) {
			logger.debug("no relevant contracts, allocation invalid");
			throw new IllegalStateException();
		}
		
		List<Allocation> newAllocations = dC.computeAllocations(newAllocation, relevantContracts, existingAllocations);
		
		for (Allocation alloc : newAllocations) {
			allocationService.create(token, alloc);
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
}
