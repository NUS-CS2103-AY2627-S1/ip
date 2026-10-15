package judey.task;

/** Represents a task with a description and completion status. */
public class Task {
    /**
     * Represents a task with a description and completion status
     */
    public static final String TYPE_TODO = "T";
    public static final String TYPE_DEADLINE = "D";
    public static final String TYPE_EVENT = "E";
    public static final String DONE_MARKER = "1";
    public static final String NOT_DONE_MARKER = "0";

    protected boolean isDone; //whether the task is done
    protected String description; //the task description

    /**
     * Creates a task with the description name given and is initially marked as incomplete
     * @param desc the text describing this task
     */
    public Task(String desc) {
        assert desc != null && !desc.isBlank()
                : "Task description should never be null or blank; "
                + "Parser/command classes are expected to validate and trim input before constructing a Task";
        this.isDone = false;
        this.description = desc;
    }

    /**
     * Marks the task as complete
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the status icon displayed for the task
     * @return {@code "X"} when done; otherwise a space
     */
    public String getStatusIcon(){
        return (isDone ? "X" : " ");
    }

    /**
     * Returns this task in the pipe-separated format used by judey.Judey's save file.
     *
     * @return task type, completion status, and description
     */
    public String toFileString() {
        return TYPE_TODO + " | " + (isDone ? DONE_MARKER : NOT_DONE_MARKER) + " | " + description;
    }

    /**
     * Returns the description of this task.
     *
     * @return task description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns a formatted text representation of this task
     * @return status icon and description
     */
    @Override
    public String toString() {
        String done = this.getStatusIcon();
        return "[" + done + "] " + this.description;
    }
}
