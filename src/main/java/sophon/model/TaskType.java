package sophon.model;

/**
 * Represents the type of task shown in the task list.
 */
public enum TaskType {
    /** Task without a date or time. */
    TODO("T"),

    /** Task that should be completed by a date or time. */
    DEADLINE("D"),

    /** Task that happens between a start and end date or time. */
    EVENT("E");

    /** Short label shown for this task type. */
    private final String icon;

    /**
     * Creates a task type with the icon shown to the user.
     *
     * @param icon short label for this task type.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon shown before tasks of this type.
     *
     * @return task type icon.
     */
    public String getIcon() {
        return icon;
    }
}
