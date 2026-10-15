package sioet.task;

/**
 * Represents a task that has no date or time information.
 */
public class Todo extends Task {
    /**
     * Creates a todo task with the given description.
     *
     * @param description the todo description
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTaskType() {
        return "T";
    }
}
