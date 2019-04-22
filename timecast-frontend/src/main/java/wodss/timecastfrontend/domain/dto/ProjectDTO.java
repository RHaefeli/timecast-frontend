package wodss.timecastfrontend.domain.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

import wodss.timecastfrontend.domain.AbstractTimecastEntity;

public class ProjectDTO extends AbstractTimecastEntity {
	
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
	
	@NotNull
	private long projectManagerId;

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

	public long getProjectManagerId() {
		return projectManagerId;
	}

	public void setProjectManagerId(long projectManagerId) {
		this.projectManagerId = projectManagerId;
	}
	
	
}
