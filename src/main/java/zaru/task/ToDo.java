package zaru.task;

/** Represents a task without a deadline or scheduled time. */
public class ToDo extends Task {
    private static final int TYPE_PRIORITY = 0;

    /**
     * Creates a todo task with an explicit completion state.
     *
     * @param title Task description.
     * @param isCompleted Whether the task is already complete.
     */
    public ToDo(String title, boolean isCompleted) {
        super(title, isCompleted);
    }

    /**
     * Creates an incomplete todo task.
     *
     * @param title Task description.
     */
    public ToDo(String title) {
        this(title, false);
    }

    @Override
    protected int getTypePriority() {
        return TYPE_PRIORITY;
    }

    /**
     * Returns the task text with the todo type marker.
     *
     * @return Formatted todo task text.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
