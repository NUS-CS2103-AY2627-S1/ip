package dukey.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the given description.
     *
     * @param description description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileString() {
        return String.format("T | %d | %s |%s", this.isDone ? 1 : 0, this.description, getDoneAtStorageText());
    }
}
