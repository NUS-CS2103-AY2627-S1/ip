package crack.task;

/**
 * A task with no date attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a todo.
     *
     * @param description What the user wants to get done.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
