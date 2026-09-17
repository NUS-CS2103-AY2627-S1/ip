package jonathan.task;

import java.time.LocalDate;

/**
 * Represents one task in the chatbot's in-memory task list.
 */
public class Task {
    private final String description;
    private TaskStatus status = TaskStatus.NOT_DONE;
    private Priority priority = Priority.NONE;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        this.description = description;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns the icon used to show this task's completion status.
     *
     * @return {@code X} when done; otherwise a blank space
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Returns this task in the format used to save it to disk.
     *
     * @return one line containing the task type, status, and description
     */
    public String toFileString() {
        return "T | " + getStatusCode() + " | " + description + this.getPriorityFileSuffix();
    }
    /**
     * Returns whether the task falls on a particular date.
     *
     * @param date date to check
     * @return {@code true} if the task occurs on the date; otherwise {@code false}
     */
    public boolean isOccuringOn(LocalDate date) {
        return false;
    }

    /**
     * Assigns a priority to this task.
     *
     * @param priority priority to assign
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Returns this task's priority.
     *
     * @return the task priority
     */
    public Priority getPriority() {
        return this.priority;
    }

    /** Returns the priority suffix used in task display. */
    protected String getPriorityDisplay() {
        if (priority == Priority.NONE) {
            return "";
        }
        return " (priority: " + priority.getLabel() + ")";
    }

    /** Returns the optional priority field used in saved data. */
    protected String getPriorityFileSuffix() {
        if (priority == Priority.NONE) {
            return "";
        }
        return " | " + priority.getLevel();
    }





    /** Returns the description for task subtypes that need to save it. */
    protected String getDescription() {
        return description;
    }

    /** Returns {@code 1} when complete and {@code 0} otherwise. */
    protected String getStatusCode() {
        return status == TaskStatus.DONE ? "1" : "0";
    }

    /**
     * Returns this task in the format used by the chatbot.
     *
     * @return the status icon followed by the description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description + this.getPriorityDisplay();
    }
}
