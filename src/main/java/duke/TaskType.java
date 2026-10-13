package duke;

/**
 * The supported kinds of tasks in Wangsa.
 */
public enum TaskType {
    /** A general todo task. */ TODO("T"),
    /** A task with a completion date. */ DEADLINE("D"),
    /** A task with a start and end value. */ EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the one-letter marker used when displaying this task type.
     *
     * @return The display marker.
     */
    public String getIcon() {
        return icon;
    }
}
