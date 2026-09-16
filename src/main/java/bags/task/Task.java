package bags.task;

/**
 * Represents the base template for all task types in the Bags application.
 *
 * <p>This abstract class stores the common properties and behaviours
 * shared by different task types such as Todo, Deadline, and Event.</p>
 */
public abstract class Task {

    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Creates a task with the given description and type.
     *
     * @param description text describing the task
     * @param type category of the task
     */
    public Task(String description, TaskType type) {
        assert description != null : "Task description must not be null";
        assert type != null : "Task type must not be null";

        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markUndone() {
        this.isDone = false;
    }

    /**
     * Returns the type of this task.
     *
     * @return the task type
     */
    public TaskType getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the status icon for the task.
     *
     * <p>An {@code X} represents a completed task, while a blank space
     * represents a task that has not been completed.</p>
     *
     * @return the status icon of the task
     */
    protected String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns a basic string representation of the task containing
     * its completion status and description.
     *
     * @return the task status and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Parses this task into its save-file record format.
     *
     * @return the formatted task record
     */
    public String parseEvent() {
        return "";
    }
}
