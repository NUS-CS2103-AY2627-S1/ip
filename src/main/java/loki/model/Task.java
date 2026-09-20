package loki.model;

import loki.parser.TaskUpdate;

/**
 * Represents one task entered by the user.
 */
public abstract class Task {
    private final String title;
    private boolean done;

    /**
     * Creates a new unfinished task.
     *
     * @param title the task description.
     */
    public Task(String title) {
        this.title = validateStorageField(title, "Task title");
        this.done = false;
    }

    /**
     * Creates a task with the supplied completion status.
     *
     * @param title the task description
     * @param done whether the task has been completed
     */
    public Task(String title, boolean done) {
        this.title = validateStorageField(title, "Task title");
        this.done = done;
    }

    /**
     * Creates a task from the numeric completion status used in storage.
     *
     * @param title the task description
     * @param done the completion status, which must be {@code 0} or {@code 1}
     * @throws IllegalArgumentException if {@code done} is not {@code 0} or {@code 1}
     */
    public Task(String title, int done) {
        this.title = validateStorageField(title, "Task title");
        if (done != 0 && done != 1) {
            throw new IllegalArgumentException("Task status must be 0 or 1");
        }
        this.done = done == 1;
    }

    /**
     * Validates a value that will be stored in the pipe-delimited file.
     *
     * @param value the value to validate.
     * @param fieldName the name used in the error message.
     * @return the unchanged valid value.
     */
    protected static String validateStorageField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        if (value.indexOf('|') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(fieldName + " contains an invalid storage character");
        }
        return value;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is done.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        done = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkDone() {
        done = false;
    }

    /**
     * Returns the task title for use by subclasses when serializing the task.
     *
     * @return the task title.
     */
    protected String getTitle() {
        return title;
    }

    /**
     * Formats the task with a completion indicator.
     *
     * @return the task's display text.
     */
    @Override
    public String toString() {
        String status = done ? "X" : " ";
        return "[" + status + "] " + title;
    }

    /**
     * Converts this task into its storage format.
     *
     * @return the serialized task.
     */
    public abstract String saveString();

    /**
     * Creates a replacement task using the supplied update fields.
     *
     * @param update the validated field values.
     * @return a replacement task of the same concrete type.
     * @throws IllegalArgumentException if the update does not apply to this task type.
     */
    public abstract Task updatedWith(TaskUpdate update);
}
