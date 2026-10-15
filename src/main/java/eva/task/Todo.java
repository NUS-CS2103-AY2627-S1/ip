package eva.task;

/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo with the specified description.
     *
     * @param description Description of the todo.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    @Override
    public String toFileString() {
        return "T | " + getFileStatus() + " | " + description;
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
