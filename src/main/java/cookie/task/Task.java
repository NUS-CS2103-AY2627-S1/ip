package cookie.task;

import java.time.LocalDate;

/** Serves as a template for more specific task types. */
public abstract class Task {
    /** The status text used when persisting a completed task. */
    private static final String DONE_STATUS = "Done";

    /** The status text used when persisting an incomplete task. */
    private static final String NOT_DONE_STATUS = "Not Done";

    /** The task description. */
    private final String description;

    /** Whether or not the task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description The task description.
     */
    public Task(String description) {
        assert description != null : "A task description must not be null";
        assert !description.isBlank() : "A task description must not be blank";
        assert !description.contains("|") : "A task description must be safe for storage";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task status as a checkbox.
     *
     * @return The completed or incomplete checkbox.
     */
    public String getCheckbox() {
        return (isDone ? "[X]" : "[ ]");
    }

    /**
     * Returns the task description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether the task has been completed.
     *
     * @return {@code true} if the task is done, otherwise {@code false}.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns whether this task's description contains the requested keyword.
     *
     * @param keyword The keyword to find.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns whether this task occurs on the requested date.
     *
     * <p>Tasks without calendar information do not occur on any date. Dated task
     * subtypes override this method with their matching rules.
     *
     * @param date The date to check.
     * @return {@code true} if the task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether another task has the same type, description, and type-specific details.
     * Completion status is deliberately ignored because it does not identify a distinct task.
     *
     * @param other The task to compare.
     * @return {@code true} if the tasks have identical identifying details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equals(other.description)
                && hasSameTypeSpecificDetails(other);
    }

    /** Marks the task as done. */
    public void mark() {
        this.isDone = true;
    }

    /** Marks the task as not done. */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the formatted string representation for file storage.
     *
     * @return The task's storage representation.
     */
    public abstract String toFileFormat();

    /** Returns the shared storage fields for a task of the specified type. */
    protected String formatForStorage(TaskType taskType) {
        String status = isDone ? DONE_STATUS : NOT_DONE_STATUS;
        return taskType.getCode() + " | " + status + " | " + description;
    }

    /** Returns whether another task of the same concrete type has matching extra details. */
    protected boolean hasSameTypeSpecificDetails(Task other) {
        return true;
    }

    /** Returns the task's checkbox and description. */
    @Override
    public String toString() {
        return this.getCheckbox() + " " + this.description;
    }
}

