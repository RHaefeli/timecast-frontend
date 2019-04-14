package wodss.timecastfrontend.domain;

public enum  Role {
    ADMINISTRATOR("administrator"),
    DEVELOPER("developer"),
    PROJECTMANAGER("projectmanager");

    private String value;
    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
