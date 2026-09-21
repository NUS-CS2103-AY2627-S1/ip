package sheppy.task;

import sheppy.SheppyException;

/** Represents a task without an attached date or time. */
public class Todo extends Task {
    /**
     * Creates a to-do task.
     *
     * @param description the text describing the task
     * @throws SheppyException if the description is empty
     */
    public Todo(String description) throws SheppyException {
        super(description);
    }

    /** @return the to-do task type */
    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }
}
