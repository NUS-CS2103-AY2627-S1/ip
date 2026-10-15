package piplupbot.task;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents a task with no date or time attached to it, e.g. {@code visit new theme park}.
 * It adds nothing to {@link Task} except its own label, because a todo really
 * is just a description and a done status.
 */
public class Todo extends Task {
    /**
     * Creates a todo that is not done yet.
     *
     * @param description What the task is.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TaskType#TODO}.
     */
    @Override
    protected TaskType getType() {
        return TaskType.TODO;
    }
}
