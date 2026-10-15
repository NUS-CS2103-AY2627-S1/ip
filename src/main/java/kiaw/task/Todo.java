package kiaw.task;

/**
 * Represents a todo task without an associated date.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the specified description.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the symbol representing a todo task.
     *
     * @return "T"
     */
    @Override
    public String getTypeIcon() {
        return "T";
    }
}
