package bott.task;

/**
 * Represents the kind of a task, and the single-letter icon used to
 * display it, e.g. "[T]" for {@link #TODO} or "[F]" for {@link #FIXED_DURATION}.
 */
public enum TaskType {
    TODO('T'),
    DEADLINE('D'),
    EVENT('E'),
    FIXED_DURATION('F');

    private final char icon;

    TaskType(char icon) {
        this.icon = icon;
    }

    /** Returns the single-letter icon representing this task type. */
    public char getIcon() {
        return icon;
    }
}
