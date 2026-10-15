package lumine.task;

import java.time.LocalDate;

import lumine.LumineException;

/**
 * Represents a single item in the user's task list.
 *
 * <p>Concrete subclasses ({@link Todo}, {@link Deadline}, {@link Event})
 * extend this base to add type-specific fields such as due dates and
 * time ranges.</p>
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType taskType;

    /**
     * Constructs a new to-do task with the given description.
     * Delegates to {@link #Task(String, TaskType)} with {@link TaskType#TODO}.
     *
     * @param description the task description (must not be blank).
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Constructs a task with the given description and type.
     *
     * @param description the task description (must not be blank).
     * @param taskType    the type of this task (must not be {@code null}).
     */
    public Task(String description, TaskType taskType) {
        this.description = requireText(description, "description");
        this.isDone = false;
        if (taskType == null) {
            throw new LumineException("Sorry, task type cannot be empty. :C");
        }
        this.taskType = taskType;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not yet completed.
     */
    public void markUndone() {
        this.isDone = false;
    }

    /**
     * Returns {@code "X"} if the task is done, or a blank space otherwise.
     * Used inside the bracketed status indicator shown to the user.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task is due on the given date.
     * Tasks without a due date return {@code false}.
     *
     * @param date Date to compare against.
     * @return {@code true} if this task is due on the given date.
     */
    public boolean isDueOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the pipe-delimited representation used to persist this task.
     * Special characters in fields are escaped so the format can be parsed
     * back unambiguously; see {@link #escapeStorageField}.
     */
    public String toStorageString() {
        return taskType.getSymbol() + " | " + (isDone ? "1" : "0") + " | "
                + escapeStorageField(description);
    }

    /**
     * Escapes pipe ({@code |}), backslash ({@code \}), and newline characters in a
     * storage field so the pipe-delimited format can be parsed back unambiguously.
     *
     * @param value the raw field value.
     * @return the escaped value safe for embedding in a storage line.
     */
    protected static String escapeStorageField(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Validates that a field value is non-null and non-blank, throwing a
     * {@link LumineException} with a user-friendly message if it is not.
     *
     * @param value     the value to check.
     * @param fieldName the human-readable name of the field (used in the error message).
     * @return {@code value} unchanged, if it passes validation.
     * @throws LumineException if {@code value} is {@code null} or blank.
     */
    protected static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new LumineException("Sorry, task " + fieldName + " cannot be empty. :C");
        }
        return value;
    }

    /**
     * {@inheritDoc}
     * Returns the bracketed display string, e.g. {@code [T][ ] buy milk}.
     */
    @Override
    public String toString() {
        return "[" + taskType.getSymbol() + "][" + this.getStatusIcon() + "] " + description;
    }
}
