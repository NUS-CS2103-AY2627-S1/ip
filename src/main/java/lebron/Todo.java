package lebron;

/**
 * Represents a todo task without a specific date or time.
 */
public class Todo extends Task {

    /**
     * Creates a new todo task with the given description.
     *
     * @param description description of the todo task
     */
    public Todo(String description) {
        super(description);
        this.type = TaskType.TODO;
    }

    /**
     * Returns the string representation of the todo task.
     *
     * @return formatted todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
