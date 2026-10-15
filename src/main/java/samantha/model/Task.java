package samantha.model;

import java.time.LocalDate;

import samantha.exception.TaskValidationException;
import samantha.storage.Storage;

/**
 * Common state and behavior shared by all task types.
 */
public abstract class Task {
    private static final int INCOMPLETE_STATUS_CODE = 0;
    private static final int COMPLETE_STATUS_CODE = 1;
    /**
     * Field separator used in saved task records. A description containing it
     * would be split into extra fields on the next load, so it is rejected.
     */
    private static final String FILE_FIELD_SEPARATOR = "|";
    private boolean isDone;
    private final String taskName;

    /**
     * Creates a task after checking its required description.
     *
     * @param name task description
     * @param taskType user-facing task type name
     * @throws TaskValidationException if the description is blank or contains {@code |}
     */
    public Task(String name, String taskType) throws TaskValidationException {
        assert name != null : "A task description must not be null";
        assert taskType != null : "A task type must not be null";
        if (name.isBlank()) {
            throw new TaskValidationException("The description of a " + taskType
                    + " cannot be empty.");
        }
        if (name.contains(FILE_FIELD_SEPARATOR)) {
            throw new TaskValidationException("The description of a " + taskType
                    + " cannot contain " + FILE_FIELD_SEPARATOR + ".");
        }
        this.taskName = name;
        this.isDone = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markNotDone() {
        this.isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return task description
     */
    public String getTaskName() {
        return this.taskName;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return {@code true} when this task is complete
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the completion status code used in saved task records.
     *
     * @return {@code 1} for a complete task or {@code 0} otherwise
     */
    protected int getCompletionStatusCode() {
        return isDone ? COMPLETE_STATUS_CODE : INCOMPLETE_STATUS_CODE;
    }

    /**
     * Returns the short display marker for this task type.
     *
     * @return the task type marker
     */
    public abstract String getType();

    /**
     * Returns whether this task falls on the supplied date.
     *
     * @param date date to check
     * @return {@code true} when this task applies to the date
     */
    public abstract boolean isOnDate(LocalDate date);

    /**
     * Returns the task in the format used by {@link Storage}.
     *
     * @return one serialized task record
     */
    public abstract String toFileString();

    /**
     * Returns this task in its user-facing display format.
     *
     * @return the formatted task
     */
    @Override
    public String toString() {
        String flag = isDone ? "[X] " : "[ ] ";
        return getType() + flag + this.taskName;
    }
}
