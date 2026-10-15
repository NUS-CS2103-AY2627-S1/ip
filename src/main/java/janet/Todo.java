package janet;

/**
 * Represents a <code>Task</code> that can be completed with no deadline.
 */
public class Todo extends Task {

    private static final String TASK_TYPE = "T";

    /**
     * Creates a <code>Todo</code> task type with the specified completion status, type, and label.
     *
     * @throws JanetException If the task label is empty.
     */
    public Todo(boolean isDone, String taskLabel) throws JanetException {
        super(isDone, Todo.TASK_TYPE, taskLabel);
    }

    @Override
    public Task markDone() throws JanetException {
        return new Todo(true, this.taskLabel);
    }
}
