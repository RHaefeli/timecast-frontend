package wodss.timecastfrontend.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wodss.timecastfrontend.domain.Allocation;
import wodss.timecastfrontend.domain.Contract;

public class AllocationChecker {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Filters all contracts and returns only the ones where the new Allocation is in between the concatenated time span of all contracts.
	 * @param newAllocation The new Allocation.
	 * @param contracts The Contract list which should be filtered.
	 * @return A list of all Contracts where the new Allocation would be in between the concatenated time span.
	 */
	public List<Contract> filterRelevantContracts(Allocation newAllocation, List<Contract> contracts) {
		List<Contract> relevantContracts = new ArrayList<Contract>();
		Optional<Contract> firstContract = contracts.stream()
				.filter(c -> c.getStartDate().getTime() <= newAllocation.getStartDate().getTime()) // contract starts before allocation
				.filter(c -> c.getEndDate().getTime() >= newAllocation.getStartDate().getTime()) // contract ends before allocation starts
				.findFirst();
		if (firstContract.isPresent()) {
			relevantContracts.add(firstContract.get());
			if (firstContract.get().getEndDate().after(newAllocation.getEndDate())) {
				// the allocation is completely in between one contract.
				return relevantContracts;
			}
			Date currentDate = firstContract.get().getEndDate();
			Optional<Contract> nextContract;
			while (currentDate.before(newAllocation.getEndDate())) {
				// get next contract less than a day from the previous
				long time = currentDate.getTime();
				nextContract = contracts.stream()
						.filter(c -> Math.abs(c.getStartDate().getTime() - time) <= 1000 * 60 * 60 * 24)
						.findFirst();
				if (!nextContract.isPresent()) {
					logger.debug("Contracts are not covering whole allocation time span");
					throw new IllegalStateException("Contracts are not covering whole allocation time span.");
				}
				
				relevantContracts.add(nextContract.get());
				currentDate = nextContract.get().getEndDate();
			}
			
			logger.debug("found {} relevant contracts.", relevantContracts.size());
		}
		
		return relevantContracts;
	}
	
	public List<Allocation> filterRelevantAllocations(Allocation newAllocation, List<Allocation> allAllocations) {
		List<Allocation> relevantAllocations = new ArrayList<Allocation>();
		for (Allocation allocation : allAllocations) {
			if (allocation.getStartDate().getTime() <= newAllocation.getStartDate().getTime()
					&& allocation.getEndDate().getTime() >= newAllocation.getStartDate().getTime()) {
				// allocation starts before new allocation, but ends within
				relevantAllocations.add(allocation);
			} else if (allocation.getEndDate().getTime() >= newAllocation.getEndDate().getTime()
					&& allocation.getStartDate().getTime() <= newAllocation.getEndDate().getTime()) {
				// allocation ends after new allocation, but starts within
				relevantAllocations.add(allocation);
			} else if (allocation.getStartDate().getTime() >= newAllocation.getStartDate().getTime()
					&& allocation.getEndDate().getTime() <= newAllocation.getEndDate().getTime()) {
				// allocation both starts and ends within new allocation
				relevantAllocations.add(allocation);
			}
		}
		
		return relevantAllocations;
	}

	/**
	 * Splits Allocations by the respective Contracts and check the validity of the pensum in the respective Contracts.
	 * @param newAllocation The new Allocation.
	 * @param contracts The list of the relevant Contracts.
	 * @param existingAllocations The list of all existing allocations.
	 * @return The list of Allocations which are splitted by the respective Contracts.
	 */
	public List<Allocation> computeAllocations(Allocation newAllocation, List<Contract> contracts, List<Allocation> existingAllocations) {
		List<Allocation> creationAllocations = new ArrayList<Allocation>();
		Date currentDate = newAllocation.getStartDate();
		boolean firstAllocation = true;
		while (currentDate.before(newAllocation.getEndDate())) {
			List<Allocation> relevantAllocations = getAllocationsOnDate(currentDate, existingAllocations);
			Date checkDate = currentDate;
			Optional<Contract> currentContract = contracts.stream()
					.filter(c -> c.getStartDate().getTime() <= checkDate.getTime())
					.filter(c -> c.getEndDate().getTime() >= checkDate.getTime())
					.findFirst();
			if (!currentContract.isPresent()) {
				// Should not happen
				throw new IllegalStateException();
			}
			
			int sumFte = relevantAllocations.stream()
					.filter(a -> a.getId() != newAllocation.getId()) // ignore the previous FTE value in this allocation
					.map(a -> a.getPensumPercentage())
					.collect(Collectors.summingInt(Integer::intValue));
			if (currentContract.get().getPensumPercentage() < sumFte + newAllocation.getPensumPercentage()) {
				logger.debug("Pensum to big for contract with id: {}", currentContract.get().getId());
				throw new IllegalStateException("Pensum to big for contract with id: " + currentContract.get().getId());
			}

			Date contractEndDate = currentContract.get().getEndDate();
			Optional<Allocation> nextAllocation = relevantAllocations.stream()
					.filter(a -> a.getStartDate().getTime() > checkDate.getTime())
					.sorted((d1, d2) -> d1.getStartDate().compareTo(d2.getStartDate()))
					.findFirst();
			if (nextAllocation.isPresent() && nextAllocation.get().getStartDate().before(contractEndDate)) {
				currentDate = nextAllocation.get().getStartDate();
			} else {
				Allocation allocation = new Allocation();
				if (firstAllocation) {
					allocation.setStartDate(newAllocation.getStartDate());
					firstAllocation = false;
				} else {
					allocation.setStartDate(currentContract.get().getStartDate());
				}
				if (currentContract.get().getEndDate().before(newAllocation.getEndDate())) {
					allocation.setEndDate(currentContract.get().getEndDate());
				} else {
					allocation.setEndDate(newAllocation.getEndDate());
				}
				
				allocation.setContract(currentContract.get());
				allocation.setPensumPercentage(newAllocation.getPensumPercentage());
				allocation.setProject(newAllocation.getProject());

				logger.debug("Adding new allocation to alloc list: {} ", allocation);
				
				creationAllocations.add(allocation);
				
				//set current date to day after current contract ends
				currentDate = new Date(currentContract.get().getEndDate().getTime() + 24L * 60 * 60 * 1000);
			}
		}
		
		return creationAllocations;
	}
	
	private List<Allocation> getAllocationsOnDate(Date date, List<Allocation> allocations) {
		return allocations.stream()
				.filter(a -> a.getStartDate().getTime() <= date.getTime())
				.filter(a -> a.getEndDate().getTime() >= date.getTime())
				.collect(Collectors.toList());
	}
}
