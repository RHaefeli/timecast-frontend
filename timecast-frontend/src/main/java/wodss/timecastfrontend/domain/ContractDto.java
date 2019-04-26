package wodss.timecastfrontend.domain;

import java.util.Date;

public class ContractDto implements TimecastDto {
    private long id;
    private long employeeId;
    private Date startDate;
    private Date endDate;
    private int pensumPercentage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(long employeeId) {
        this.employeeId = employeeId;
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
}
