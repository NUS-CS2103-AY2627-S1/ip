package sumo.task;

/**
 * A task that can be completed without a deadline or event time.
 *
 * <p>Todo is a subtype of {@link Task}, so it can be stored in the same
 * {@code Task[]} as other task types.</p>
 */
public class Todo extends Task {
    /**
     * Creates a new incomplete todo with the given description.
     *
     * @param description the todo text.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the type icon used when displaying this task.
     *
     * @return the todo type icon.
     */
    @Override
    public String getTypeIcon() {
        return TaskType.TODO.getStorageCode();
    }

    /**
     * Returns this todo in the format used by Sumo's task list.
     *
     * @return the formatted todo.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + getDescription();
    }
}
