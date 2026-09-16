package minerva.task;

/**
 * Represents a generic task that can be completed or left incomplete.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the specified description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Checks whether the description contains the specified word.
     *
     * @param word word to search for
     * @return true if the description contains the word
     */
    public boolean containsWord(String word) {
        return this.description.contains(word);
    }
    /**
     * Returns the completion marker for this task.
     *
     * @return {@code "X"} when complete, otherwise a blank space
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
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
    public void markUnDone() {
        this.isDone = false;
    }

    /**
     * Returns the task formatted with its completion status.
     *
     * @return formatted task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the task in the format used by the storage file.
     *
     * @return serialized task representation
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}

