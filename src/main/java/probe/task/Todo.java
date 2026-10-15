package probe.task;

/**
 * Represents a task without a deadline or event time.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the specified description.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }
    
    /**
     * Returns the todo with its type and completion status.
     *
     * @return Display representation of the todo.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Returns the todo in storage format.
     *
     * @return Storage representation of the todo.
     */
    @Override
    public String toFileFormat() {
        String tagsText = tagsToFileFormat();
        return String.format("T | %d | %s%s", isDone ? 1 : 0, description,
                tagsText.isEmpty() ? "" : " | " + tagsText);
    }
}
