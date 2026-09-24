package hampster.task;

import java.util.Locale;

/**
 * Represents a general task with a description and completion status.
 *
 * <p>Concrete task types must provide their own storage format.</p>
 */
public abstract class Task {

    /** The task description. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean done;

    /** The optional task tag. */
    protected String tag;

    /**
     * Creates a task.
     *
     * @param done whether the task is initially completed
     * @param description the task description
     */
    protected Task(boolean done, String description) {
        this(done, description, "");
    }

    /**
     * Creates a task with a tag.
     *
     * @param done whether the task is initially completed
     * @param description the task description
     * @param tag the optional task tag
     */
    protected Task(boolean done, String description, String tag) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description must not be blank");
        }
        this.description = description;
        this.done = done;
        this.tag = tag == null ? "" : tag;
    }

    /**
     * Toggles the completion status of the task.
     *
     * @return the new completion status
     */
    public boolean toggleState() {
        done = !done;
        return done;
    }

    /** Sets this task's tag. */
    public void setTag(String tag) {
        this.tag = tag == null ? "" : tag.toLowerCase(Locale.ROOT);
    }

    /** Removes this task's tag. */
    public void removeTag() {
        tag = "";
    }

    /** Returns this task's tag, or an empty string when untagged. */
    public String getTag() {
        return tag;
    }

    /** Returns the tag suffix used in user-facing task displays. */
    protected String tagDisplay() {
        return tag.isEmpty() ? "" : " " + tag;
    }

    /**
     * Converts the task into its storage format.
     *
     * @return a serialized representation of the task
     */
    public abstract String saveString();

    /**
     * Returns a user-readable representation of the task.
     *
     * @return the task status and description
     */
    @Override
    public String toString() {
        return (done ? "[X]" : "[ ]") + " " + description + tagDisplay();
    }
}
