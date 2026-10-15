package charliek.model;

/**
 * A task that has no date or time attached to it.
 */
public class ToDo extends Task {
    /**
     * Creates an incomplete to-do task.
     *
     * @param description the text describing the task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Returns the type of this task.
     *
     * @return {@link TaskType#TODO}.
     */
    @Override
    protected TaskType getType() {
        return TaskType.TODO;
    }
}
