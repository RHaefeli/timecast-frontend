package wodss.timecastfrontend.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;

public class Project {
	
	@Id
	private int id;
	
	@NotNull
	@Size(min=1, max=50)
	private String name;
	
	@NotNull
	private int ftePercentage;
	
	@NotNull
	@Size(min=10, max=10)
	private String startDate;
	
	@NotNull
	@Size(min=10, max=10)
	private String endDate;
	
	@NotNull
	private int projectManagerId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFtePercentage() {
		return ftePercentage;
	}

	public void setFtePercentage(int ftePercentage) {
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

	public int getProjectManagerId() {
		return projectManagerId;
	}

	public void setProjectManagerId(int projectManagerId) {
		this.projectManagerId = projectManagerId;
	}
	
	
}
