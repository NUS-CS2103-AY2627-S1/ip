package lynn;

/** Defines the supported task categories and their display icons. */
public enum TaskType {
    /** A task without a date or time. */
    TODO("T"),
    /** A task with a due date. */
    DEADLINE("D"),
    /** A task with start and end times. */
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the single-letter icon used when displaying this task type.
     *
     * @return the task-type display icon
     */
    public String getIcon() {
        return icon;
    }
}
