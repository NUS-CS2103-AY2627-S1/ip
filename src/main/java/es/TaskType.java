package es;

/**
 * The kinds of tasks Es can store, each identified by a one-letter icon.
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
     * Returns the letter shown in square brackets for this task type.
     *
     * @return the type icon, such as {@code T} for a to-do
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the task type that uses the given storage icon.
     *
     * @param icon the one-letter type icon from a saved line
     * @return the matching task type
     * @throws EsException if the icon is not a known task type
     */
    public static TaskType fromIcon(String icon) throws EsException {
        for (TaskType type : values()) {
            if (type.icon.equals(icon)) {
                return type;
            }
        }
        throw new EsException("The saved task list is corrupted.");
    }
}
