package skynet;

/**
 * Represents the completion status of a task.
 */
public enum TaskStatus {
    NOT_DONE(" "),
    DONE("X");

    private final String icon;

    TaskStatus(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
