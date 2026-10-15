package nova.task;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a basic task with a description and completion status.
 * Specialized task types extend this class and customize their display.
 */
public class Task {
    /** The text describing this task. */
    protected String description;

    /** The kind of this task. */
    protected final TaskType taskType;

    /** The completion state of this task. */
    protected TaskStatus status;

    /** The labels attached to this task, in insertion order. */
    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this(description, TaskType.BASIC);
    }

    /**
     * Creates an incomplete task of the given type.
     *
     * @param description the text describing the task
     * @param taskType the kind of task being created
     */
    protected Task(String description, TaskType taskType) {
        assert taskType != null : "Task type should never be null";
        this.description = description;
        this.taskType = taskType;
        this.status = TaskStatus.NOT_DONE;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        status = TaskStatus.NOT_DONE;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the type prefix for this task.
     *
     * @return the task type's display prefix
     */
    public String getTypeIcon() {
        return taskType.getDisplayIcon();
    }

    /**
     * Returns the description portion of this task's display.
     *
     * @return the task description
     */
    public String getDisplayDescription() {
        return description;
    }

    /**
     * Returns the completion marker used in the task display.
     *
     * @return the display marker for the current task status
     */
    public String getStatusIcon() {
        return status.getDisplayIcon();
    }

    /**
     * Returns whether this task has the same identity as another task.
     *
     * <p>Task identity includes the task type and description, but ignores completion status and
     * tags so those mutable properties do not permit duplicate tasks.</p>
     *
     * @param other the task to compare with this task
     * @return true if both tasks have the same base identity
     */
    public boolean hasSameIdentity(Task other) {
        return other != null && taskType == other.taskType && description.equals(other.description);
    }

    /** Adds a tag to this task, ignoring the request if the tag is already present. */
    public boolean addTag(String tag) {
        if (!isValidTag(tag)) {
            return false;
        }
        return tags.add(tag);
    }

    /** Returns whether a tag starts with a hash and contains no whitespace. */
    public static boolean isValidTag(String tag) {
        return tag != null && tag.matches("#[^|\\s]+");
    }

    /** Removes a tag from this task and reports whether it was present. */
    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }

    /** Returns the tags attached to this task without exposing the mutable set. */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns this task in the format used for persistent storage.
     *
     * @return the task type, completion state, and description separated by pipes
     */
    public String toStorageString() {
        return getBaseStorageString() + getTagsStorageSuffix();
    }

    /**
     * Returns this task in its display format.
     *
     * @return the status marker followed by the description
     */
    @Override
    public String toString() {
        return getTypeIcon() + "[" + getStatusIcon() + "] " + getDisplayDescription()
                + getTagsDisplaySuffix();
    }

    /** Returns the common task fields used at the start of a storage record. */
    protected String getBaseStorageString() {
        return taskType.name().charAt(0) + " | "
                + (status == TaskStatus.DONE ? "1" : "0") + " | " + description;
    }

    /** Returns the optional storage field containing this task's tags. */
    protected String getTagsStorageSuffix() {
        return tags.isEmpty() ? "" : " | " + String.join(" ", tags);
    }

    /** Returns the optional display suffix containing this task's tags. */
    private String getTagsDisplaySuffix() {
        return tags.isEmpty() ? "" : " " + String.join(" ", tags);
    }
}
