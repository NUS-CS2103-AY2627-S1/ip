package bany.tasks;

/** Stores the full and abbreviated display names for task types. */
public enum TaskType {
    /** Task without a deadline or event range. */
    TODO("T", "TODO"),
    /** Task that must be completed by a date-time. */
    DEADLINE("D", "DEADLINE"),
    /** Task that occurs over a date-time range. */
    EVENT("E", "EVENT"),
    /** Generic task type used for base task display. */
    TASK("", "TASK");

    /** Abbreviated type marker used in task-list output. */
    private final String typeShort;
    /** Full type name used in commands and task-file data. */
    private final String type;

    /**
     * Creates a task type with its display names.
     *
     * @param typeShort abbreviated type marker.
     * @param type full type name.
     */
    TaskType(String typeShort, String type) {
        this.typeShort = typeShort;
        this.type = type;
    }

    /**
     * Returns the abbreviated type marker.
     *
     * @return abbreviated task type.
     */
    public String getTypeShort() {
        return typeShort;
    }

    /**
     * Returns the full task type name.
     *
     * @return full task type.
     */
    public String getType() {
        return type;
    }

}
