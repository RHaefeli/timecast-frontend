package wodss.timecastfrontend.services.mocks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wodss.timecastfrontend.domain.dto.AllocationDTO;
import wodss.timecastfrontend.domain.dto.ContractDTO;
import wodss.timecastfrontend.domain.dto.EmployeeDTO;
import wodss.timecastfrontend.domain.dto.ProjectDTO;

public final class MockRepository {
	
	public static List<ProjectDTO> projects = new ArrayList<>();
	public static List<EmployeeDTO> employees = new ArrayList<>();
	public static List<ContractDTO> contracts = new ArrayList<>();
	public static List<AllocationDTO> allocations = new ArrayList<>();
	
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
		generateAllocations();
	}
	
	private static void generateProjects() {
		ProjectDTO project1 = new ProjectDTO();
		project1.setId(nextProjectId++);
		project1.setName("Project1");
		project1.setFtePercentage(100);
		project1.setStartDate("2019-03-16");
		project1.setEndDate("2019-10-10");
		project1.setProjectManagerId(2);
		
		ProjectDTO project2 = new ProjectDTO();
		project2.setId(nextProjectId++);
		project2.setName("Project2");
		project2.setFtePercentage(200);
		project2.setStartDate("2018-03-16");
		project2.setEndDate("2018-10-10");
		project2.setProjectManagerId(2);
		
		ProjectDTO project3 = new ProjectDTO();
		project3.setId(nextProjectId++);
		project3.setName("Project3");
		project3.setFtePercentage(300);
		project3.setStartDate("2020-03-16");
		project3.setEndDate("2020-10-10");
		project3.setProjectManagerId(2);
		
		projects.add(project1);
		projects.add(project2);
		projects.add(project3);
		
	}
	
	private static void generateEmployees() {
        EmployeeDTO emp1 = new EmployeeDTO();
        emp1.setId(nextEmployeeId++);
        emp1.setLastName("Müller");
        emp1.setFirstName("Kurt");
        emp1.setActive(true);
        emp1.setEmailAddress("k.mueller@mail.com");
        emp1.setRole("Admin");

        EmployeeDTO emp2 = new EmployeeDTO();
        emp2.setId(nextEmployeeId++);
        emp2.setLastName("Meier");
        emp2.setFirstName("Jonathan");
        emp2.setActive(true);
        emp2.setEmailAddress("j.meier@mail.com");
        emp2.setRole("Developer");

        EmployeeDTO emp3 = new EmployeeDTO();
        emp3.setId(nextEmployeeId++);
        emp3.setLastName("Brösmeli");
        emp3.setFirstName("Guschdi");
        emp3.setActive(true);
        emp3.setEmailAddress("g.broesmeli@mail.com");
        emp3.setRole("Project Leader");

        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
    }
	
	private static void generateContracts() {		
		ContractDTO con1 = new ContractDTO();
		con1.setId(nextContractId++);
		con1.setStartDate("2019-03-16");
		con1.setEndDate("2019-10-10");
		con1.setPensumPercentage(100);
		con1.setEmployeeId(2);
		
		ContractDTO con2 = new ContractDTO();
		con2.setId(nextContractId++);
		con2.setStartDate("2019-03-16");
		con2.setEndDate("2019-10-31");
		con2.setPensumPercentage(100);
		con2.setEmployeeId(1);
		
		contracts.add(con1);
		contracts.add(con2);
	}
	
	private static void generateAllocations() {
		AllocationDTO alloc1 = new AllocationDTO();
		alloc1.setId(nextAllocationId++);
		alloc1.setStartDate("2019-03-16");
		alloc1.setEndDate("2019-10-10");
		alloc1.setPensumPercentage(50);
		alloc1.setProjectId(1);
		alloc1.setContractId(1);
		
		AllocationDTO alloc2 = new AllocationDTO();
		alloc2.setId(nextAllocationId++);
		alloc2.setStartDate("2019-03-16");
		alloc2.setEndDate("2019-05-20");
		alloc2.setPensumPercentage(100);
		alloc2.setProjectId(1);
		alloc2.setContractId(2);
		
		AllocationDTO alloc3 = new AllocationDTO();
		alloc3.setId(nextAllocationId++);
		alloc3.setStartDate("2019-06-01");
		alloc3.setEndDate("2019-10-10");
		alloc3.setPensumPercentage(50);
		alloc3.setProjectId(1);
		alloc3.setContractId(2);
		
		allocations.add(alloc1);
		allocations.add(alloc2);
		allocations.add(alloc3);
	}

}
