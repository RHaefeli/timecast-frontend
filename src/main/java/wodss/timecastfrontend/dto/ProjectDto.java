package wodss.timecastfrontend.dto;

import wodss.timecastfrontend.dto.TimecastDto;

public class ProjectDto implements TimecastDto {
	private long id;
	private String name;
	private long ftePercentage;
	private String startDate;
	private String endDate;
	private long projectManagerId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public long getProjectManagerId() {
		return projectManagerId;
	}

	public void setProjectManagerId(long projectManagerId) {
		this.projectManagerId = projectManagerId;
	}


}
