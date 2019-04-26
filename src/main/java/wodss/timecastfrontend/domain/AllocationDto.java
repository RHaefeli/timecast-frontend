package wodss.timecastfrontend.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class AllocationDto implements TimecastDto {
	Long id;
	
	@NotNull
	@Size(min=10, max=10)
	String startDate;
	
	@NotNull
	@Size(min=10, max=10)
	String endDate;
	
	@NotNull
	@Min(0)
	@Max(100)
	int pensumPercentage;
	
	@NotNull
	long contractId;
	
	@NotNull
	long projectId;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getPensumPercentage() {
		return pensumPercentage;
	}

	public void setPensumPercentage(int pensumPercentage) {
		this.pensumPercentage = pensumPercentage;
	}

	public long getContractId() {
		return contractId;
	}

	public void setContractId(long contractId) {
		this.contractId = contractId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
		
	}
	
	
}
