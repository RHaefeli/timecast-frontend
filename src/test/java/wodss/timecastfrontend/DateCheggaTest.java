package wodss.timecastfrontend;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Role;
import wodss.timecastfrontend.exceptions.TimecastInternalServerErrorException;
import wodss.timecastfrontend.web.DateChegga;

public class DateCheggaTest {
	
	private DateChegga testee = new DateChegga();
	private Date startDate1;
	private List<Allocation> existingAllocations;
	private List<Contract> contracts;
	private Employee emp;
	
	@Before
	public void setUp() {
		Employee e = new Employee();
		e.setActive(true);
		e.setFirstName("Date");
		e.setLastName("Chegga");
		e.setRole(Role.DEVELOPER);
		Contract c1 = new Contract();
		c1.setEmployee(e);
		startDate1 = normalizeDate(new Date());
		c1.setStartDate(startDate1);
		c1.setEndDate(normalizeDate((new Date(startDate1.getTime() + 10L * 24 * 60 * 60 * 1000))));
		c1.setPensumPercentage(100);
		
		Contract c2 = new Contract();
		c2.setEmployee(e);
		c2.setStartDate(normalizeDate(new Date(startDate1.getTime() + 11L * 24 * 60 * 60 * 1000)));
		c2.setEndDate(normalizeDate((new Date(startDate1.getTime() + 22L * 24 * 60 * 60 * 1000))));
		c2.setPensumPercentage(50);
		
		contracts = new ArrayList<Contract>();
		contracts.add(c1);
		contracts.add(c2);
		
		Allocation a1 = new Allocation();
		a1.setStartDate(normalizeDate(new Date(startDate1.getTime())));
		a1.setEndDate(normalizeDate(new Date(startDate1.getTime() + 2L * 24*60*60*1000)));
		a1.setContract(c1);
		a1.setPensumPercentage(100);
		
		Allocation a2 = new Allocation();
		a2.setStartDate(normalizeDate(new Date(startDate1.getTime()+3L *24*60*60*1000)));
		a2.setEndDate(normalizeDate(new Date(startDate1.getTime() + 5L * 24*60*60*1000)));
		a2.setContract(c1);
		a2.setPensumPercentage(50);
		
		Allocation a3 = new Allocation();
		a3.setStartDate(normalizeDate(new Date(startDate1.getTime() + 19L*24*60*60*1000)));
		a3.setEndDate(normalizeDate(new Date(startDate1.getTime() + 25L * 24*60*60*1000)));
		a3.setContract(c1);
		a3.setPensumPercentage(100);
		
		existingAllocations = new ArrayList<Allocation>();
		existingAllocations.add(a1);
		existingAllocations.add(a2);
		existingAllocations.add(a3);
	}
	
	@Test
	public void filterRelevantContractsOneContractTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(startDate1.getTime() + 24 * 60 * 60 * 1000));
		alloc.setEndDate(new Date(startDate1.getTime() + 3 * 24 * 60 * 60 * 1000));
		List<Contract> cs = testee.filterRelevantContracts(alloc, contracts);
		
		assertTrue(cs.size() == 1);
		assertTrue(cs.get(0) == contracts.get(0));
	}
	
	@Test
	public void filterRelevantContractsTwoContractTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(startDate1.getTime() + 24 * 60 * 60 * 1000));
		alloc.setEndDate(new Date(startDate1.getTime() + 21 * 24 * 60 * 60 * 1000));
		List<Contract> cs = testee.filterRelevantContracts(alloc, contracts);
		
		assertTrue(cs.size() == 2);
	}
	
	@Test
	public void filterRelevantAllocationsOneTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(startDate1.getTime() - 3 * 60 * 60 * 1000));
		alloc.setEndDate(new Date(startDate1.getTime() + 1 * 24 * 60 * 60 * 1000));
		List<Allocation> as = testee.filterRelevantAllocations(alloc, existingAllocations);
		
		assertTrue(as.size() == 1);
	}
	
	@Test
	public void filterRelevantAllocationsTwoTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(startDate1.getTime() +3L *24* 60 * 60 * 1000));
		alloc.setEndDate(new Date(startDate1.getTime() + 5L * 24 * 60 * 60 * 1000));
		List<Allocation> as = testee.filterRelevantAllocations(alloc, existingAllocations);
		
		assertTrue(as.size() == 1);
	}
	
	@Test
	public void filterRelevantAllocationsThreeTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(startDate1.getTime() - 3 * 60 * 60 * 1000));
		alloc.setEndDate(new Date(startDate1.getTime() + 20 * 24 * 60 * 60 * 1000));
		List<Allocation> as = testee.filterRelevantAllocations(alloc, existingAllocations);
		
		assertTrue(as.size() == 3);
	}
	
	
	@Test
	public void filterRelevantAllocationsSameDatesTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(startDate1.getTime() - 3 * 60 * 60 * 1000));
		alloc.setEndDate(new Date(startDate1.getTime() + 20 * 24 * 60 * 60 * 1000));
		List<Allocation> as = testee.filterRelevantAllocations(alloc, existingAllocations);
		
		assertTrue(as.size() == 3);
	}
	
	@Test
	public void computeAllocationsNoOtherOneContractTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(contracts.get(0).getStartDate().getTime()));
		alloc.setEndDate(new Date(contracts.get(0).getEndDate().getTime()));
		alloc.setPensumPercentage(contracts.get(0).getPensumPercentage());
		alloc.setContract(contracts.get(0));
		
		
		List<Allocation> createdAlloc = testee.computeAllocations(alloc, contracts, new ArrayList<Allocation>());
		assertTrue(createdAlloc.size() == 1);
	}
	
	@Test
	public void computeAllocationsNoOtherHalfContractTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(contracts.get(0).getStartDate().getTime()));
		alloc.setEndDate(new Date(contracts.get(0).getStartDate().getTime() + 3L*24*60*60*1000));
		alloc.setPensumPercentage(contracts.get(0).getPensumPercentage());
		alloc.setContract(contracts.get(0));
		
		
		List<Allocation> createdAlloc = testee.computeAllocations(alloc, contracts, new ArrayList<Allocation>());
		assertTrue(createdAlloc.size() == 1);
	}
	
	@Test(expected = IllegalStateException.class)
	public void computeAllocationsNoOtherTwoContractOverPensumTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(contracts.get(0).getStartDate().getTime()));
		alloc.setEndDate(new Date(contracts.get(0).getStartDate().getTime() + 13*24*60*60*1000));
		alloc.setPensumPercentage(contracts.get(0).getPensumPercentage());
		alloc.setContract(contracts.get(0));
		
		
		List<Allocation> createdAlloc = testee.computeAllocations(alloc, contracts, new ArrayList<Allocation>());
	}
	
	@Test()
	public void computeAllocationsNoOtherTwoContractSuccessTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(contracts.get(0).getStartDate().getTime()));
		alloc.setEndDate(new Date(contracts.get(0).getStartDate().getTime() + 13*24*60*60*1000));
		alloc.setPensumPercentage(contracts.get(1).getPensumPercentage());
		alloc.setContract(contracts.get(0));
		
		
		List<Allocation> createdAlloc = testee.computeAllocations(alloc, contracts, new ArrayList<Allocation>());
		assertTrue(createdAlloc.size()==2);
	}
	
	@Test()
	public void computeAllocationsOneOtherOneContractSuccessTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(contracts.get(0).getStartDate().getTime() + 3L*24*60*60*1000));
		alloc.setEndDate(new Date(contracts.get(0).getStartDate().getTime() + 5L*24*60*60*1000));
		alloc.setPensumPercentage(50);
		alloc.setContract(contracts.get(0));
		
		
		List<Allocation> createdAlloc = testee.computeAllocations(alloc, contracts, existingAllocations);
		assertTrue(createdAlloc.size()==1);
	}
	
	@Test(expected = IllegalStateException.class)
	public void computeAllocationsOneOtherOneContractOverPensumTest() {
		Allocation alloc = new Allocation();
		alloc.setStartDate(new Date(contracts.get(0).getStartDate().getTime() + 3L*24*60*60*1000));
		alloc.setEndDate(new Date(contracts.get(0).getStartDate().getTime() + 5L*24*60*60*1000));
		alloc.setPensumPercentage(55);
		alloc.setContract(contracts.get(0));
		
		
		List<Allocation> createdAlloc = testee.computeAllocations(alloc, contracts, existingAllocations);
		assertTrue(createdAlloc.size()==1);
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
