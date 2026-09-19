package bob;

/** Represents a basic todo task with a description and completion state. */

public class Task {
    static final String TODO_TYPE = "T";
    static final String DEADLINE_TYPE = "D";
    static final String EVENT_TYPE = "E";
    static final String COMPLETE_FLAG = "1";

    /** The task description shown to the user. */
    private final String item;
    /** Whether the task has been marked as completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param item Task description.
     */
    public Task(String item) {
        if (item == null || item.trim().isEmpty()) {
            throw new IllegalArgumentException("A task must have a description");
        }
        this.item = item.trim();
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
     * Returns whether this task is completed.
     *
     * @return Whether this task is completed.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns this task's description.
     *
     * @return Task description.
     */
    public String getItem() {
        return this.item;
    }

    /**
     * Returns the line format used when saving this task.
     *
     * @return Storage line.
     */
    public String toStorageString() {
        return TODO_TYPE + " | " + (isDone ? COMPLETE_FLAG : "0") + " | " + item;
    }

    /**
     * Returns the display form of this task.
     *
     * @return Display form of this task.
     */
    @Override
    public String toString() {
        return "[T][" + (isDone ? "X" : " ") + "] " + item;
    }
}
