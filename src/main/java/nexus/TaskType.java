package nexus;

/** The fixed categories of tasks supported by Nexus. */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /** Returns the one-letter icon used in task output. */
    public String getIcon() {
        return icon;
    }
}
