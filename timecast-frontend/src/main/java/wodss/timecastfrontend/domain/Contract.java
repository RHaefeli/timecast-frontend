package wodss.timecastfrontend.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

public class Contract implements TimecastEntity {
    private long id;
    private Employee employee;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date startDate;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date endDate;
    private int pensumPercentage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return id == contract.id &&
                pensumPercentage == contract.pensumPercentage &&
                Objects.equals(employee, contract.employee) &&
                Objects.equals(startDate, contract.startDate) &&
                Objects.equals(endDate, contract.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employee, startDate, endDate, pensumPercentage);
    }
}
