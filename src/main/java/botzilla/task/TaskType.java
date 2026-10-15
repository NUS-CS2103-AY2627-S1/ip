package botzilla.task;

/**
 * The kind of task a Task represents, each associated with a
 * single-character icon used in display and file output.
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
     * Returns the single-character icon for this task type,
     * e.g. "T" for TODO.
     *
     * @return the type icon
     */
    public String getIcon() {
        return icon;
    }
}
