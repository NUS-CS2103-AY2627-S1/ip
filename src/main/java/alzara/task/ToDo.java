package alzara.task;

/**
 * A task with just a description and a done/not-done state - no date fields.
 */
public class ToDo extends Task {
    /**
     * Creates a to-do that starts out not done.
     *
     * @param description the task's description
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns this task's save-file line, prefixed with the type letter.
     *
     * @return the save-file line, prefixed with {@code "T | "}
     */
    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }

    /**
     * Returns this task's console display, prefixed with the type letter.
     *
     * @return the display line, prefixed with {@code "[T]"}
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
