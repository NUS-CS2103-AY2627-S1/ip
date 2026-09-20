package bonbon.tasklist;

import java.time.format.DateTimeFormatter;

/**
 * Task represents a task. It represents the name of the task as a <code>String</code>,
 * and its completion status as a <code>boolean</code>.
 */
public class Task {

    /**
     * Formatter for displaying date and time values in a human-readable string format.
     */
    protected static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    private String name;
    public boolean isDone;

    /**
     * Creates a Task.
     *
     * @param name the name of the task.
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Marks task as done.
     *
     * @return true if successfully marked as done, and false if task is already marked as done.
     */
    public boolean markDone() {
        if (isDone) {
            return false;
        }
        isDone = true;
        return true;
    }

    /**
     * Marks task as undone.
     *
     * @return true if successfully marked as undone, and false if task is already marked as undone.
     */
    public boolean markUndone() {
        if (!isDone) {
            return false;
        }
        isDone = false;
        return true;
    }

    /**
     * Returns the name of the task.
     *
     * @return The task name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the string representation of the deadline task formatted for storage persistence.
     *
     * @return The formatted string suitable for saving to a file.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + name;
    }

    /**
     * Returns the string representation of the task, including its completion status.
     *
     * @return The formatted string representation of the task.
     */
    @Override
    public String toString() {
        if (isDone) {
            return "[X] " + name;
        }
        return "[ ] " + name;
    }

    /**
     * Returns whether the task has been marked as completed.
     *
     * @return True if the task is completed, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }
}