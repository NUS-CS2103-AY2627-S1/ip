package sheppy.task;

/** The fixed set of task types supported by Sheppy. */
public enum TaskType {
    /** A task without a date or time. */
    TODO("T"),

    /** A task that must be completed by a date or time. */
    DEADLINE("D"),

    /** A task that takes place over a time range. */
    EVENT("E");

    /** The short marker shown in the task list. */
    private final String icon;

    /**
     * Creates a task type with its display marker.
     *
     * @param icon the marker shown in the task list
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns this type's display marker.
     *
     * @return the display marker
     */
    public String getIcon() {
        return icon;
    }
}
