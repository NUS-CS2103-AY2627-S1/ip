package summer.task;

/**
 * Represents the supported task categories and their display icons.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with the icon shown in task output.
     *
     * @param icon short display icon for this task type
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used when displaying tasks of this type.
     *
     * @return short display icon for this task type
     */
    public String getIcon() {
        return this.icon;
    }
}
