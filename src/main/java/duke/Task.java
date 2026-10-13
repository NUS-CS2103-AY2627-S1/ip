package duke;

/**
 * A task entered into the Wangsa task list.
 */
public class Task {
    /**
     * The text entered by the user for this task.
     */
    private final String description;

    /**
     * Whether the task has been marked as done.
     */
    private boolean isDone;

    /**
     * The kind of task, used to select its display marker.
     */
    private final TaskType type;

    /**
     * Creates an unfinished task with the given description.
     *
     * @param description The task text.
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Creates an unfinished task of the supplied type.
     *
     * @param description Task text.
     * @param type Task category.
     */
    protected Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task text entered by the user.
     *
     * @return The task text entered by the user.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return Whether this task has been completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the kind of this task.
     *
     * @return The kind of this task.
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns the one-character status marker used in task output.
     *
     * @return {@code X} for a completed task, otherwise a blank space.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the one-letter type marker used by specialized task classes.
     *
     * @return The task type's display marker.
     */
    public String getTypeIcon() {
        return type.getIcon();
    }

    /**
     * Returns task-specific details appended to the description.
     *
     * @return An empty string for a task without extra details.
     */
    protected String getDetails() {
        return "";
    }

    /**
     * Returns the task in the format shown to the user.
     *
     * @return The status marker and task description.
     */
    @Override
    public String toString() {
        String typeMarker = getTypeIcon().isEmpty() ? "" : "[" + getTypeIcon() + "]";
        return typeMarker + "[" + getStatusIcon() + "] " + description + getDetails();
    }
}
