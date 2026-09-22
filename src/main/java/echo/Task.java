package echo;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDesc() {
        return description;
    }

    /**
     * Returns whether this task is complete in storage-friendly form.
     *
     * @return {@code "true"} when complete; otherwise {@code "false"}
     */
    public String getStatus() {
        return String.valueOf(isDone);
    }

    /**
     * Returns the visual marker for this task's completion status.
     *
     * @return {@code "[X]"} when complete, otherwise {@code "[ ]"}
     */
    public String getStatusMarker() {
        return this.isDone ? "[X]" : "[ ]";
    }

    /**
     * Marks this task as complete.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Converts this task to the format used when saving it to storage.
     *
     * @return this task's storage representation
     */
    public String toStorageFormat() {
        return String.join(" // ",
                new String[]{
                        "T",
                        String.valueOf(isDone),
                        description
                });
    }

    /**
     * Returns the formatted representation shown to the user.
     *
     * @return this task's display representation
     */
    @Override
    public String toString() {
        return this.getStatusMarker() + " " + this.description;
    }
}
