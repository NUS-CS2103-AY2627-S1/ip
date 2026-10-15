package augustus.task;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;
    private String tag = "";

    /**
     * Creates a task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return description of this task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is completed, false otherwise
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Sets the tag of this task.
     *
     * @param tag Tag to assign to the task.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Returns the tag of this task.
     *
     * @return Tag assigned to the task.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Returns the tag in storage format.
     *
     * @return Tag field to append when saving the task.
     */
    protected String getTagForStorage() {
        return tag.isEmpty() ? "" : " | " + tag;
    }

    /**
     * Returns the display representation of this task.
     *
     * @return String containing the completion status and task description.
     */
    @Override
    public String toString() {
        String icon = isDone ? "X" : " ";
        String taskString = "[" + icon + "] " + description;

        if (!tag.isEmpty()) {
            taskString += " [#" + tag + "]";
        }

        return taskString;
    }

    /**
     * Returns the representation of this task used for storage.
     *
     * @return String representation of the task for saving to a file.
     */
    public abstract String toFileString();

}
