package wodss.timecastfrontend.services.mocks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.AllocationDto;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Project;
import wodss.timecastfrontend.domain.Role;

public final class MockRepository {
	
	public static List<Project> projects = new ArrayList<>();
	public static List<Employee> employees = new ArrayList<>();
	public static List<Contract> contracts = new ArrayList<>();
	public static List<Allocation> allocations = new ArrayList<>();
	
	public static int nextProjectId = 1;
	public static int nextEmployeeId = 1;
	public static int nextAllocationId = 1;
	public static int nextContractId = 1;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private MockRepository() {
		logger.debug("initializing mock repo.");
		generateRepository();
		logger.debug("finished initializing mock repo.");
	}
	
	
	public static void generateRepository() {
		generateEmployees();
		generateContracts();
		generateProjects();
//		generateAllocations();
	}
	
	private static void generateProjects() {
		Project project1 = new Project();
		project1.setId(nextProjectId++);
		project1.setName("Project1");
		project1.setFtePercentage(100);
		project1.setStartDate(normalizeDate(new Date()));
		project1.setEndDate(normalizeDate(new Date(new Date().getTime() + 5L * 24 * 60 * 60 * 1000)));
		project1.setProjectManager(employees.get(1));
		
		Project project2 = new Project();
		project2.setId(nextProjectId++);
		project2.setName("Project2");
		project2.setFtePercentage(200);
		project2.setStartDate(normalizeDate(new Date()));
		project2.setEndDate(normalizeDate(new Date(new Date().getTime() + 30L * 24 * 60 * 60 * 1000)));
		project2.setProjectManager(employees.get(1));
		
		Project project3 = new Project();
		project3.setId(nextProjectId++);
		project3.setName("Project3");
		project3.setFtePercentage(300);
		project3.setStartDate(normalizeDate(new Date()));
		project3.setEndDate(normalizeDate(new Date(new Date().getTime() + 50 * 24 * 60 * 60 * 1000)));
		project3.setProjectManager(employees.get(1));
		
		projects.add(project1);
		projects.add(project2);
		projects.add(project3);
		
	}
	
	private static void generateEmployees() {
        Employee emp1 = new Employee();
        emp1.setId(nextEmployeeId++);
        emp1.setLastName("Müller");
        emp1.setFirstName("Kurt");
        emp1.setActive(true);
        emp1.setEmailAddress("k.mueller@mail.com");
        emp1.setRole(Role.ADMINISTRATOR);

        Employee emp2 = new Employee();
        emp2.setId(nextEmployeeId++);
        emp2.setLastName("Meier");
        emp2.setFirstName("Jonathan");
        emp2.setActive(true);
        emp2.setEmailAddress("j.meier@mail.com");
        emp2.setRole(Role.DEVELOPER);

        Employee emp3 = new Employee();
        emp3.setId(nextEmployeeId++);
        emp3.setLastName("Brösmeli");
        emp3.setFirstName("Guschdi");
        emp3.setActive(true);
        emp3.setEmailAddress("g.broesmeli@mail.com");
        emp3.setRole(Role.PROJECTMANAGER);

        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
    }
	
	private static void generateContracts() {	
		Date date = new Date();
		Contract con1 = new Contract();
		con1.setId(nextContractId++);
		con1.setStartDate(normalizeDate(date));
        con1.setEndDate(normalizeDate(new Date(date.getTime() + 24L * 60 * 60 * 1000)));
		con1.setPensumPercentage(100);
		con1.setEmployee(employees.get(1));
		
		Contract con2 = new Contract();
		con2.setId(nextContractId++);
		con2.setStartDate(normalizeDate(new Date(date.getTime() + 25L * 24 * 60 * 60 * 1000)));
        con2.setEndDate(normalizeDate(new Date(date.getTime() + 40L * 24 * 60 * 60 * 1000)));
		con2.setPensumPercentage(100);
		con2.setEmployee(employees.get(1));
		
		contracts.add(con1);
		contracts.add(con2);
	}
	
	private static void generateAllocations() {
		Allocation alloc1 = new Allocation();
		Date startDate = new Date();
		Date endDate = new Date(startDate.getTime() + 30L * 24 * 60 * 60 *1000);
		alloc1.setStartDate(startDate);
		alloc1.setEndDate(endDate);
		alloc1.setPensumPercentage(25);
		alloc1.setId(nextAllocationId++);
		alloc1.setProject(projects.get(0));
		alloc1.setContract(contracts.get(0));
		
		Allocation alloc2 = new Allocation();
		startDate = new Date();
		endDate = new Date(startDate.getTime() + 60L * 24 * 60 * 60 * 1000);
		alloc2.setStartDate(startDate);
		alloc2.setEndDate(endDate);
		alloc2.setPensumPercentage(100);
		alloc2.setId(nextAllocationId++);
		alloc2.setPensumPercentage(100);
		alloc2.setProject(projects.get(0));
		alloc2.setContract(contracts.get(0));
		
		Allocation alloc3 = new Allocation();
		startDate = new Date();
		endDate = new Date(startDate.getTime() + 120L * 24 * 60 * 60 * 1000);
		alloc3.setStartDate(startDate);
		alloc3.setEndDate(endDate);
		alloc3.setPensumPercentage(50);
		alloc3.setId(nextAllocationId++);
		alloc3.setPensumPercentage(50);
		alloc3.setProject(projects.get(0));
		alloc3.setContract(contracts.get(1));
		
		allocations.add(alloc1);
		allocations.add(alloc2);
		allocations.add(alloc3);
	}
	
	private static Date normalizeDate(Date date) {
		Date newDate = date;
		newDate.setHours(0);
		newDate.setMinutes(0);
		newDate.setSeconds(0);
		return newDate;
	}

}
