package wodss.timecastfrontend.domain;

import javax.validation.constraints.NotNull;

public class EmployeeLogin extends Employee {
    @NotNull
    // See separate PasswordValidator
    private String password;

    @NotNull
    // See separate PasswordValidator
    private String confirmPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Employee convertToEmployee() {
        Employee emp = new Employee();
        emp.setId(getId());
        emp.setActive(isActive());
        emp.setEmailAddress(getEmailAddress());
        emp.setFirstName(getFirstName());
        emp.setLastName(getLastName());
        emp.setRole(getRole());
        return emp;
    }

    @Override
    public String toString() {
        return getLastName() + " " + getFirstName() + ", " + getRole();
    }
}
