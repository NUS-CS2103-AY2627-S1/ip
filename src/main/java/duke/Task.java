package duke;

/**
 * Represents a task with a description and a completion status.
 * Acts as the base class for the specific task types (Todo, Deadline, Event).
 */
public class Task {

    private String name;
    private boolean isDone;

    /**
     * Creates a task with the given description, initially not isDone.
     *
     * @param name the task description
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the description
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns whether the task is isDone.
     *
     *
     * @return true if the task is marked isDone
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the single-letter icon for this task type.
     *
     * @return the type icon (a blank space for a plain task)
     */
    public String getTypeIcon() {
        return " ";
    }

    /**
     * Returns the status icon shown in the task list.
     *
     * @return "X" if the task is isDone, otherwise a blank space
     */
    public String getStatusIcon() {
        return (this.isDone ? "X" : " ");
    }

    /**
     * Marks this task as isDone.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not isDone.
     */
    public void unmarkDone() {
        this.isDone = false;
    }

    /**
     * Returns any extra detail shown after the description (e.g. a deadline time).
     *
     * @return the extra info, or an empty string if there is none
     */
    public String getExtraInfo() {
        return "";
    }

    /**
     * Returns the task formatted for display, e.g. "[T][X] read book".
     *
     * @return the display string
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + getName() + getExtraInfo();
    }

    /**
     * Returns the task encoded for saving to file, e.g. "T | 1 | read book".
     *
     * @return the save-format string
     */
    public String toSaveFormat() {
        return getTypeIcon() + " | " + (isDone() ? "1" : "0") + " | " + getName();
    }
}
