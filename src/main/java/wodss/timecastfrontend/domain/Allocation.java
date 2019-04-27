package wodss.timecastfrontend.domain;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;


public class Allocation implements TimecastEntity {
	
	//TODO
	Long id;
	
	@NotNull
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date startDate;
    
	@NotNull
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date endDate;
	
	@NotNull
	@Min(0)
	@Max(100)
	int pensumPercentage;
	
	Contract contract;
	
	Project project;
	
	public Allocation() {
		
	}
	
	public Allocation(Date startDate, Date endDate, int pensumPercentage) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.pensumPercentage = pensumPercentage;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
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

	@Override
	public long getId() {
		
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
		
	}
	
	
	
	
}
