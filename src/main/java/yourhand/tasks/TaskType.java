package yourhand.tasks;

/**
 * Represents the supported categories of tasks and their display icons.
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
     * Returns the icon used to display this task type.
     *
     * @return The task-type display icon.
     */
    public String getIcon() {
        return icon;
    }
}
