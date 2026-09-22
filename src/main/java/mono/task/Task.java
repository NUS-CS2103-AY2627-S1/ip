package mono.task;

/**
 * Represents a named task whose completion state can be changed.
 */
public class Task {
    /** Description displayed to the user and written to storage. */
    protected final String name;

    /** Whether this task has been completed. */
    protected Boolean isDone;

    /**
     * Creates an incomplete task with the specified name.
     *
     * @param name task description
     */
    public Task(String name) {
        assert name != null : "A task must always have a non-null description";
        this.name = name;
        this.isDone = false;
        assert !this.isDone : "A newly created task must be incomplete";
    }

    /** Marks this task as completed. */
    public void markDone() {
        this.isDone = true;
        assert this.isDone : "markDone must leave the task completed";
    }

    /** Marks this task as incomplete. */
    public void unmarkDone() {
        this.isDone = false;
        assert !this.isDone : "unmarkDone must leave the task incomplete";
    }

    /**
     * Returns the stable, pipe-delimited representation used in Mono's save file.
     *
     * @return task type, completion status, and task name
     */
    public String toFileString() {
        return String.format("T | %d | %s", this.isDone ? 1 : 0, this.name);
    }

    /**
     * Returns this task's user-facing representation.
     *
     * @return completion status followed by the task name
     */
    @Override
    public String toString() {
        return String.format("[%s] %s", this.isDone ? "X" : " ", this.name);
    }
}
