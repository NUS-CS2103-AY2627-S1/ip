package alice;

/**
 * Represents a basic task with a description and a completion status.
 * Serves as the base class for more specific task types such as Todo,
 * Deadline, and Event.
 *
 * <p>The class is abstract because a task with no type is not something the
 * program has any use for: every task the user can create is a Todo, a
 * Deadline or an Event. Enforcing that in the type system also makes it
 * impossible to reintroduce the bug where unrecognised input was stored as
 * a bare Task, which then silently came back from the data file as a Todo.
 */
public abstract class Task {
    private String description;
    private boolean isDone;

    /**
     * Constructs a Task with the given description. The task is initially
     * marked as not done.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon representing whether this task is done.
     *
     * @return "X" if the task is done, or " " (a space) if not.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task is done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the description of this task.
     *
     * @return the task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the human-readable name of this kind of task, for display
     * somewhere the "[T]"-style marker would be too cryptic.
     *
     * @return the name of this task type.
     */
    public String getTypeName() {
        return "Task";
    }

    /**
     * Returns a short description of when this task is scheduled, or an
     * empty string for a task that has no date attached.
     *
     * <p>Each subclass formats its own dates, so a caller wanting to show
     * task details never has to test what type it is holding.
     *
     * @return the schedule summary, or an empty string if there is none.
     */
    public String getScheduleSummary() {
        return "";
    }

    /**
     * Returns a string representation of this task suitable for saving to
     * the data file.
     *
     * <p>Abstract rather than defaulted, so that adding a new kind of task
     * without deciding how it is persisted is a compile error, instead of a
     * task that quietly saves itself under the wrong type marker.
     *
     * @return formatted string for file storage.
     */
    public abstract String toFileFormat();

    /**
     * Returns a string representation of this task, including its
     * completion status and description.
     *
     * @return formatted string for display to the user.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
