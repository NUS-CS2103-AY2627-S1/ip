package ev.task;

/**
 * Represents a task with no date or time attached to it.
 */
public class Todo extends Task {
    /**
     * Constructs a todo with the given description.
     *
     * @param description the description of the task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileString() {
        return "T | " + super.toFileString();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
