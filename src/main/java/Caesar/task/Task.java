package caesar.task;

import caesar.exception.CaesarException;

/**
 * A task that can be completed or left pending.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /** Creates a pending task with the supplied description. */
    public Task(String description) {
        this.description = description;
    }

    /** Marks this task as complete, rejecting an already-complete task. */
    public void markAsDone() throws CaesarException {
        if (isDone) {
            throw new CaesarException("You have marked this task!");
        }
        isDone = true;
        // A successful completion transition must leave the task marked done.
        assert isDone : "A completed task must be marked done";
    }

    /** Marks this task as pending, rejecting an already-pending task. */
    public void markAsNotDone() throws CaesarException {
        if (!isDone) {
            throw new CaesarException("You have unmarked this task!");
        }
        isDone = false;
        // A successful reset transition must leave the task pending.
        assert !isDone : "A reset task must be pending";
    }

    /** Returns whether this task has been marked complete. */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task text used when saving the task.
     */
    public String getDescription() {
        return description;
    }

    /** Returns the task in the standard completion-status display format. */
    @Override
    public String toString() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }
}
