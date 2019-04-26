package wodss.timecastfrontend.domain;

public enum  Role {
    ADMINISTRATOR("ADMINISTRATOR"),
    DEVELOPER("DEVELOPER"),
    PROJECTMANAGER("PROJECTMANAGER");

    private String value;
    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
