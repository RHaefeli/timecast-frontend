package wodss.timecastfrontend.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Allocation extends AbstractTimecastEntity {
	@NotNull
	@Size(min=10, max=10)
	String startDate;
	
	@NotNull
	@Size(min=10, max=10)
	String endDate;
	
	@NotNull
	@Size(min=0, max=100)
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
	
	
}
