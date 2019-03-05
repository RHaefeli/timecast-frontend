package wodss.timecastfrontend.domain;

import java.time.LocalDateTime;

public class Assignment {
    private User user;
    private Project project;

    private int employment;
    private LocalDateTime start;
    private LocalDateTime end;

    public Assignment(User user, Project project, int employment, LocalDateTime start, LocalDateTime end) {
        this.user = user;
        this.project = project;
        this.employment = employment;
        this.start = start;
        this.end = end;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getEmployment() {
        return employment;
    }

    public void setEmployment(int employment) {
        this.employment = employment;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
