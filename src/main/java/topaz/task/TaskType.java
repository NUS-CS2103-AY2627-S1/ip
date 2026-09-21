package topaz.task;

/**
 * Identifies the kind of task and its display icon.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E"),
    FIXED_DURATION("F");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used when displaying this task type.
     *
     * @return the task-type icon
     */
    public String getIcon() {
        return icon;
    }
}
