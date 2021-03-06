package wodss.timecastfrontend.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class Employee implements TimecastEntity {
    private long id;
    private boolean active;

    @NotNull
    @Size(min=1, max=50)
    private String lastName;

    @NotNull
    @Size(min=1, max=50)
    private String firstName;

    @NotNull
    @Size(min=1, max=50)
    @Email
    private String emailAddress;

    @NotNull
    private Role role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

	public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return lastName + " " + firstName + ", " + role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
                active == employee.active &&
                Objects.equals(lastName, employee.lastName) &&
                Objects.equals(firstName, employee.firstName) &&
                Objects.equals(emailAddress, employee.emailAddress) &&
                role == employee.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, active, lastName, firstName, emailAddress, role);
    }

}
