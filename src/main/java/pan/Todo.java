package pan;

/**
 * Represents a todo: a task with only a description and no date attached.
 */
public class Todo extends Task {

    /** Creates a to-do with the given description. */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /** Returns the save-file line for a todo, e.g. {@code "T | 1 | read book"}. */
    @Override
    public String toFileString() {
        return "T | " + super.toFileString();
    }
}
