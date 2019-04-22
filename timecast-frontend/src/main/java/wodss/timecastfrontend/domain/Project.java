package wodss.timecastfrontend.domain;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

import wodss.timecastfrontend.domain.AbstractTimecastEntity;

public class Project extends AbstractTimecastEntity {
	
	@NotNull
	@Size(min=1, max=50)
	private String name;
	
	@NotNull
	private long ftePercentage;
	
	@NotNull
	@Size(min=10, max=10)
	private String startDate;
	
	@NotNull
	@Size(min=10, max=10)
	private String endDate;
	
	private Employee projectManager;
	
	public Project() {}
	
	public Project(String name, long ftePercentage, String startDate, String endDate) {
		this.name = name;
		this.ftePercentage = ftePercentage;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getFtePercentage() {
		return ftePercentage;
	}

	public void setFtePercentage(long ftePercentage) {
		this.ftePercentage = ftePercentage;
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

	public Employee getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(Employee projectManager) {
		this.projectManager = projectManager;
	}	
	
}
