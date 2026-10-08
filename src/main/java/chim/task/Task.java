package chim.task;

/**
 * Represents a task with a description and a completion status.
 * Serves as the base class for Todo, Deadline, and Event.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;
    protected String priority;

    /**
     * Creates a task with the given description and type. The task is
     * not done by default.
     *
     * @param description Description of the task.
     * @param type Type of the task (TODO, DEADLINE, or EVENT).
     */
    public Task(String description, TaskType type) {
        assert description != null : "Task description should not be null";
        assert type != null : "Task type should not be null";

        this.description = description;
        this.isDone = false;
        this.type = type;
        this.priority = "none";
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Returns this task formatted as a single line for saving to disk.
     * Subclasses append any extra fields they need (e.g. by/from/to).
     *
     * @return Pipe-delimited representation of this task.
     */
    public String toFileFormat() {
        return getTypeSymbol() + " | " + getStatusDigit() + " | " + priority + " | " + description;
    }

    /**
     * Returns the single-character digit used in the file format to
     * represent this task's completion status.
     *
     * @return "1" if the task is done, "0" otherwise.
     */
    protected String getStatusDigit() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns the single-letter symbol used in the file format to
     * represent this task's type.
     *
     * @return "T" for a todo, "D" for a deadline, "E" for an event.
     */
    protected String getTypeSymbol() {
        switch (type) {
            case TODO:
                return "T";
            case DEADLINE:
                return "D";
            case EVENT:
                return "E";
            default:
                return "?";
        }
    }

    /**
     * Returns a key uniquely identifying this task's content, used to
     * detect duplicate tasks. Subclasses append their extra fields.
     *
     * @return A string combining the task's type and description.
     */
    protected String getDetailsKey() {
        return getTypeSymbol() + "|" + description;
    }

    private String getPriorityLabel() {
        if (priority.equals("none")) {
            return "";
        }
        return "[P:" + priority + "]";
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "]" + getPriorityLabel() + " " + description;
    }
}
