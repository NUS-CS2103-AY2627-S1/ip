package koara.task;

/**
 * Represents the supported task categories and their display icons.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with its storage and display icon.
     *
     * @param icon Icon representing the task type.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used to identify this task type.
     *
     * @return Task type icon.
     */
    public String getIcon() {
        return icon;
    }
}
