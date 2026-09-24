package dobby.task;

/** A task without a date or time. */
public class ToDo extends Task {
    /**
     * Creates a todo task with a description.
     *
     * @param description todo description
     */
    public ToDo(String description) {
        super(description, "T");
    }
}
