package jimbo.task;

/**
 * Represents a task with no date or time attached to it,
 * e.g. "borrow book".
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }

    @Override
    public String toSaveFormat() {
        return "T | " + (status == DoneStatus.DONE ? 1 : 0) + " | " + description;
    }
}
