package computah.task;

/**
 * Represents a task without any date or time attached to it.
 */
public class ToDo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description description of the todo task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the todo task type icon.
     *
     * @return "T".
     */
    @Override
    public String getTaskType() {
        return "T";
    }
}
