package bingbong.task;

/**
 * Represents a generic task. It stores the main description and tracks if the task is done or not done.
 */
public abstract class Task {

    protected String description;
    protected boolean isDone;

    /**
     * Constructs a task entity initialized with its text summary details.
     *
     * @param description The textual statement description details of the tracking item.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the text description of the task.
     *
     * @return The task description string.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Generates a status icon character based on completion state.
     *
     * @return "X" if the tracking state evaluates as done, otherwise a blank space " ".
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Modifies the internal object state metadata to mark this item as completed.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Modifies the internal object state metadata to restore this item as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Converts the completion status boolean state into a binary string representation.
     *
     * @return "1" if the tracking state evaluates as true, otherwise a "0".
     */
    protected String getStatusBinary() {
        return isDone ? "1" : "0";
    }

    /**
     * Translates object property details into a file transaction database record entry line.
     *
     * @return A clean pipe-separated data format serialisation string record layout.
     */
    public abstract String formatToFile();
}
