package wodss.timecastfrontend.dto;

import wodss.timecastfrontend.dto.TimecastDto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;


public class AllocationDto implements TimecastDto {
	private long id;
	
	@NotNull
	@Size(min=10, max=10)
	private String startDate;
	
	@NotNull
	@Size(min=10, max=10)
	private String endDate;
	
	@NotNull
	@Min(0)
	@Max(100)
	private int pensumPercentage;
	
	@NotNull
	private long contractId;
	
	@NotNull
	private long projectId;

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

	public long getContractId() {
		return contractId;
	}

	public void setContractId(long contractId) {
		this.contractId = contractId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
		
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AllocationDto that = (AllocationDto) o;
		return pensumPercentage == that.pensumPercentage &&
				contractId == that.contractId &&
				projectId == that.projectId &&
				Objects.equals(id, that.id) &&
				Objects.equals(startDate, that.startDate) &&
				Objects.equals(endDate, that.endDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, startDate, endDate, pensumPercentage, contractId, projectId);
	}
}
