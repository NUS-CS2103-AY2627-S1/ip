package alfred.task;

/**
 * Identifies the supported kinds of tasks.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /** Returns the icon used when displaying this task type. */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the task type for a save-file icon, or {@code null} if unrecognized.
     *
     * @param icon Type icon from a save line, such as {@code T}.
     * @return Matching type, or {@code null} if {@code icon} is not a known type.
     */
    public static TaskType fromIcon(String icon) {
        for (TaskType type : values()) {
            if (type.icon.equals(icon)) {
                return type;
            }
        }
        return null;
    }
}
