package larry;

/**
 * Represents the supported types of tasks in Larry.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used to represent this task type.
     *
     * @return Icon for this task type.
     */
    public String getIcon() {
        return icon;
    }
}
