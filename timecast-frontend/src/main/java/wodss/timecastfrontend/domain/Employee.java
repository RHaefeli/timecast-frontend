package wodss.timecastfrontend.domain;

import java.util.List;

import wodss.timecastfrontend.domain.AbstractTimecastEntity;

public class Employee extends AbstractTimecastEntity {
    private boolean active;
    private String lastName;
    private String firstName;
    private String emailAddress;
    private String role;
    
    public Employee(boolean active, String lastName, String firstName, String emailAddress, String role) {
    	this.active = active;
    	this.lastName = lastName;
    	this.firstName = firstName;
    	this.emailAddress = emailAddress;
    	this.role = role;
    }

    public Employee() {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

	@Override
    public String toString() {
        return lastName + " " + firstName + ", " + role;
    }
}
