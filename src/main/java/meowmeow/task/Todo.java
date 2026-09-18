package meowmeow.task;

/**
 * Represents a task with no date or time attached to it.
 */
public class Todo extends Task {

    /**
     * Constructs a todo with the given description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }
}
