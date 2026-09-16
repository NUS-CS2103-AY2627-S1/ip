package olaf;

import java.util.List;

/**
 * Represents a task stored by Olaf.
 */
abstract class Task {
    private final String description;
    private boolean isDone = false;

    protected Task(String description) {
        assert description != null && !description.isBlank()
                : "Tasks must have a nonblank description";
        this.description = description;
    }

    /** Copies the common fields without sharing mutable task state. */
    protected Task(Task original) {
        description = original.description;
        isDone = original.isDone;
    }

    /**
     * Returns an independent copy, including the task's type, dates, and completion status.
     *
     * @return task whose mutable state can be changed independently
     */
    abstract Task copy();

    /**
     * Marks this task as completed.
     */
    void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return task description
     */
    String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if this task is completed
     */
    boolean isDone() {
        return isDone;
    }

    /**
     * Returns the one-character code used to identify this task type in storage.
     *
     * @return storage type code
     */
    abstract String getTypeCode();

    /**
     * Returns task-type-specific fields that follow the description in storage.
     *
     * @return immutable list of additional storage fields
     */
    abstract List<String> getAdditionalStorageFields();

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[" + getTypeCode() + "][" + getStatusIcon() + "] " + description;
    }

    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }
}
