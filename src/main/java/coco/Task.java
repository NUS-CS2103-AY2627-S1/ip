package coco;

/**
 * A task the user wants to track, with a description and a done/not-done
 * status. Todo, Deadline, and Event extend this with their own extra
 * fields and their own [T]/[D]/[E] display markers.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a new, not-yet-done task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        // Every caller (Parser) always passes a trimmed, non-empty String;
        // this documents that assumption rather than silently accepting null.
        assert description != null : "Task description should never be null";
        this.description = description;
        this.isDone = false;
    }

    /** Returns "X" if this task is done, otherwise a blank space. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns this task's description. */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Encodes this task as a single line for saving to disk. Subclasses
     * prepend their type letter and append any extra fields.
     *
     * @return Save-file line for this task.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
