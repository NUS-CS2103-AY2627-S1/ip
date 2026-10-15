package puke;

/** Represents a task that can be marked as complete or incomplete. */
public class Task {
    protected String desc;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param desc Task description.
     */
    public Task(String desc) {
        this.desc = desc;
        this.isDone = false;
        this.type = TaskType.TODO;
    }

    /**
     * Creates a task with the given completion state.
     *
     * @param desc Task description.
     * @param done Whether the task is complete.
     */
    public Task(String desc, boolean done) {
        this(desc);
        this.isDone = done;
    }

    @Override
    public String toString() {
        return String.format("[T][%s] %s", getStatusIcon(), desc);
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public String getDesc() {
        return desc;
    }

    /** Returns whether this task description contains the given keyword. */
    public boolean matchesKeyword(String keyword) {
        return desc.toLowerCase().contains(keyword.toLowerCase());
    }

    /** Returns whether another task has the same type and description. */
    public boolean hasSameDetails(Task other) {
        return other != null && type == other.type && desc.equals(other.desc);
    }

    public void mark() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        isDone = false;
    }

    public TaskType getType() {
        return type;
    }
}
