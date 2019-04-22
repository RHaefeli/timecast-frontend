package wodss.timecastfrontend.domain.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import wodss.timecastfrontend.domain.AbstractTimecastEntity;

public class ContractDTO extends AbstractTimecastEntity{
	
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
	long employeeId;

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

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

}
