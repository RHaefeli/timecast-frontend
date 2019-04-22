package wodss.timecastfrontend.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import wodss.timecastfrontend.domain.AbstractTimecastEntity;

public class Contract extends AbstractTimecastEntity{
	
	@NotNull
	@Size(min=10, max=10)
	String startDate;
	
	@NotNull
	@Size(min=10, max=10)
	String endDate;
	
	@NotNull
	@Size(min=0, max=100)
	int pensumPercentage;
	
	Employee employee;
	
	public Contract(String startDate, String endDate, int pensumPercentage) {
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

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
