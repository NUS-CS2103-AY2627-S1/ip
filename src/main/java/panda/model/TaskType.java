package panda.model;

/**
 * The kinds of tasks supported by Panda.
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
     * Returns the short display icon for this task type.
     *
     * @return the task type icon
     */
    public String getIcon() {
        return icon;
    }
}
