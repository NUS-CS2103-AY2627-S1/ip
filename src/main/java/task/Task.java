package puyo.task;

import java.time.LocalDate;

/**
 * Represents an abstract task managed by the application.
 */
public abstract class Task {

    private final String name;
    private boolean done;
    private final TaskType type;

    /**
     * Constructs a {@code Task} with the specified name and type.
     *
     * @param name The description or name of the task.
     * @param type The type of the task (e.g., TODO, DEADLINE, EVENT).
     */
    protected Task(String name, TaskType type) {
        this.name = name.trim();
        this.done = false;
        this.type = type;
    }

    /**
     * Marks the task as completed.
     */
    public void markDone() {
        this.done = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void unmark() {
        this.done = false;
    }

    /**
     * Returns whether the task has been completed.
     *
     * @return {@code true} if completed, {@code false} otherwise.
     */
    public boolean getDone() {
        return this.done;
    }

    /**
     * Returns the name or description of the task.
     *
     * @return The task name string.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns whether this task is scheduled on the given date.
     * Tasks without a date, such as todos, do not appear in a daily schedule.
     * Implemented with assistance from ChatGPT.
     *
     * @param date The date to check.
     * @return {@code false} unless a dated task overrides this method.
     */
    public boolean isScheduledOn(LocalDate date) {
        return false;
    }

    /**
     * Formats the task into a plain text representation suitable for file storage.
     *
     * @return Formatted string representation for file saving.
     */
    public String toFileString() {
        return type.getCode() + " | " + (done ? "1" : "0") + " | " + name;
    }

    /**
     * Formats the task into a human-readable representation for UI display.
     *
     * @return Formatted string representation of the task.
     */
    @Override
    public String toString() {
        return "[" + type.getCode() + "]" + (done ? "[✓]" : "[X]") + " " + name;
    }
}
