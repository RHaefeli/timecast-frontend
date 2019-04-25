package wodss.timecastfrontend.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class Project implements TimecastEntity {
	private long id;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Project project = (Project) o;
		return id == project.id &&
				ftePercentage == project.ftePercentage &&
				projectManagerId == project.projectManagerId &&
				Objects.equals(name, project.name) &&
				Objects.equals(startDate, project.startDate) &&
				Objects.equals(endDate, project.endDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, ftePercentage, startDate, endDate, projectManagerId);
	}
}
