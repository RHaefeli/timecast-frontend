package wodss.timecastfrontend.domain;

import java.util.List;

public class User {
    private String name;
    private String firstName;
    private Role role;
    private int employment;
    private List<Project> projectList;

    /*
    public User(String name, String firstName, Role role, int employment, List<Project> projectList) {
        this.name = name;
        this.firstName = firstName;
        this.role = role;
        this.employment = employment;
        this.projectList = projectList;
    }
    */

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public int getEmployment() {
        return employment;
    }
    public void setEmployment(int employment) {
        this.employment = employment;
    }

    public List<Project> getProjectList() {
        return projectList;
    }
    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }
}
