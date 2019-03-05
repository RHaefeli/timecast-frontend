package wodss.timecastfrontend.domain;

public class Project {
    private String projectName;
    private User projectLeader;

    public Project(String projectName, User projectLeader) {
        this.projectName = projectName;
        this.projectLeader = projectLeader;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public User getProjectLeader() {
        return projectLeader;
    }

    public void setProjectLeader(User projectLeader) {
        this.projectLeader = projectLeader;
    }
}
