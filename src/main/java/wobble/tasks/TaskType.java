package wobble.tasks;

/** The kinds of tasks that Wobble can store. */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    /** Associates a display icon with the task type. */
    TaskType(String icon) {
        this.icon = icon;
    }

    /** Returns the short icon used when displaying this task type. */
    public String getIcon() {
        return icon;
    }
}
