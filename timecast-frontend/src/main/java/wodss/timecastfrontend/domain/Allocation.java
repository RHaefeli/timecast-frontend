package wodss.timecastfrontend.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import wodss.timecastfrontend.domain.AbstractTimecastEntity;

public class Allocation extends AbstractTimecastEntity {
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
	
	Contract contract;
	
	Project project;
	
	public Allocation(String startDate, String endDate, int pensumPercentage) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.pensumPercentage = pensumPercentage;
	}

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

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	
	
	
}
