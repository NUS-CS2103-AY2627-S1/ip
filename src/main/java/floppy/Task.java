package floppy;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task and its completion status.
 */
public abstract class Task {
    /** Standard format used to display task dates. */
    protected static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /** Description of the task. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    private final TaskType taskType;

    /**
     * Creates a task with the specified description and type.
     *
     * @param description Description of the task.
     * @param taskType Category of the task.
     */
    protected Task(String description, TaskType taskType) {
        this.description = description;
        this.isDone = false;
        this.taskType = taskType;
    }

    /**
     * Returns an icon representing the task's completion status.
     *
     * @return {@code X} if the task is completed, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return Description of this task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if this task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the category of this task.
     *
     * @return Type of this task.
     */
    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return "[" + taskType.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
