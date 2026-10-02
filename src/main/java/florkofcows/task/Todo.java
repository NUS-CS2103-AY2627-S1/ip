package florkofcows.task;

/**
 * Represents a task with no associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo with the given description.
     *
     * @param description the text describing the todo.
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
