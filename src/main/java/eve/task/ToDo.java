package eve.task;

/** A task without any date/time attached to it. */
public class ToDo extends Task {
    /**
     * Creates a to-do task with the given description.
     *
     * @param description what the task is.
     */
    public ToDo(String description) {
        super(description);
    }

    /** Returns this task's display text, prefixed with the "[T]" type icon. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }
}
