package wodss.timecastfrontend.domain;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Objects;

public class Project implements TimecastEntity {
	private long id;

	@NotNull
	@Size(min=1, max=50)
	private String name;
	
	@NotNull
	private long ftePercentage;
	
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date startDate;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date endDate;
	
	private Employee projectManager;
	
	public Project() {}

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

	public Employee getProjectManager() {
		return projectManager;
	}
	
	public void setProjectManager(Employee projectManager) {
		this.projectManager = projectManager;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Project project = (Project) o;
		return id == project.id &&
				ftePercentage == project.ftePercentage &&
				projectManager.equals(project.projectManager) &&
				Objects.equals(name, project.name) &&
				Objects.equals(startDate, project.startDate) &&
				Objects.equals(endDate, project.endDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, ftePercentage, startDate, endDate, projectManager);
	}
}
