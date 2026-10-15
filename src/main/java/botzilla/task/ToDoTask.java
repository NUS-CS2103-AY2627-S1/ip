package botzilla.task;

/**
 * Represents a simple task with no associated date, e.g. "read book".
 */
public class ToDoTask extends Task {

    /**
     * Constructs a new ToDoTask with the given name.
     *
     * @param name display name/description of the task
     */
    public ToDoTask(String name) {
        super(name, TaskType.TODO);
    }

    /**
     * Returns this task, including any tags, serialized for saving to disk.
     */
    @Override
    public String toFileString() {
        return super.toFileString() + tagsFileSuffix();
    }

    /**
     * Returns a human-readable representation including any tags,
     * e.g. "[T][ ] read book #fun".
     */
    @Override
    public String toString() {
        return super.toString() + tagsDisplaySuffix();
    }
}
