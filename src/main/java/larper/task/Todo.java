package larper.task;

/**
 * Represents a task without any date or time information.
 */
public class Todo extends Task {
    /**
     * Creates an unmarked todo task with the specified description.
     *
     * @param description Text that describes the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the type icon for todo tasks.
     */
    @Override
    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns a detached copy of this todo task.
     */
    @Override
    public Task copy() {
        Todo taskCopy = new Todo(getDescription());
        taskCopy.addTags(getTags());
        taskCopy.setDone(isDone());
        return taskCopy;
    }
}
