package friday.task;

/**
 * Represents a task without any date or time.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the given description.
     *
     * @param description description of the todo task
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the icon that shows this task is a todo.
     *
     * @return todo type icon
     */
    @Override
    public String getTaskTypeIcon() {
        return "T";
    }
}
