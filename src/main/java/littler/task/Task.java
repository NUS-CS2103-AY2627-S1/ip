package littler.task;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import littler.exception.LittleRException;
import littler.task.attribute.PriorityLevel;
import littler.task.attribute.TagSet;

/**
 * Represents an abstract task containing a description name and a completion status.
 * Serves as the base class for specific task types such as Todo, Deadline, and Event.
 */
public abstract class Task {
    public static final String NAME_DELIMITER = "/name";

    private String name;
    private boolean isTaskMarked;
    private final TagSet tags = new TagSet();
    private PriorityLevel priority;

    /**
     * Constructs a new Task with the specified name and initializes its completion status to false.
     *
     * @param name the description or name of the task
     */
    public Task(String name) {
        this.name = name;
        this.isTaskMarked = false;
    }

    /**
     * Sets the priority level of this task.
     *
     * @param priority the new priority level to set
     */
    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    /**
     * Clears the priority level of this task, setting it to null.
     */
    public void clearPriority() {
        this.priority = null;
    }

    /**
     * Returns the priority level of this task.
     *
     * @return the current priority level, or null if no priority is set
     */
    public PriorityLevel getPriority() {
        return priority;
    }

    /**
     * Copies the priority level from this task to another task.
     *
     * @param other the task to copy the priority level to
     */
    protected void copyPriorityTo(Task other) {
        other.priority = this.priority;
    }

    /**
     * Returns a string representation of the task's priority for storage purposes.
     *
     * @return a formatted string of the priority, or an empty string if no priority is set
     */
    protected String getPriorityStorageSuffix() {
        return priority == null ? "" : " | " + PriorityLevel.toStorageField(priority);
    }

    /**
     * Returns a string representation of the task's priority for display purposes.
     *
     * @return a formatted string of the priority, or an empty string if no priority is set
     */
    protected String getPriorityDisplaySuffix() {
        return priority == null ? "" : " [" + priority.getKeyword() + "]";
    }

    /**
     * Returns the first tag associated with this task, if any.
     *
     * @return an Optional containing the first tag, or empty if no tags are present
     */
    public Optional<String> getFirstTag() {
        return tags.getFirstTag();
    }

    /**
     * Adds a tag to the task.
     *
     * @param tag the tag to add
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Removes a tag from the task.
     *
     * @param tag the tag to remove
     */
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    /**
     * Returns the set of tags associated with the task.
     *
     * @return an unmodifiable set of tags
     */
    public Set<String> getTags() {
        return tags.asUnmodifiableSet();
    }

    /**
     * Copies the tags from this task to another task.
     *
     * @param other the task to copy tags to
     */
    protected void copyTagsTo(Task other) {
        other.tags.addAll(this.tags);
    }

    /**
     * Returns a string representation of the task's tags for storage purposes.
     *
     * @return a formatted string of tags, or an empty string if no tags are present
     */
    protected String getTagsStorageSuffix() {
        return tags.toStorageString();
    }

    /**
     * Returns a string representation of the task's tags for display purposes.
     *
     * @return a formatted string of tags, or an empty string if no tags are present
     */
    protected String getTagsDisplaySuffix() {
        return tags.toDisplayString();
    }

    /**
     * Returns a new task of the same concrete type as this one, with any fields present in
     * the given updates map replaced, and all other fields (including completion status)
     * carried over unchanged.
     *
     * @param updates a map from field delimiter (e.g. "/name") to the new raw value for that field
     * @return the updated task instance
     * @throws LittleRException if updates contains a field not applicable to this task type,
     *     or a date field's value cannot be parsed
     */
    public abstract Task withUpdates(Map<String, String> updates) throws LittleRException;

    /**
     * Copies this task's completion status onto another task instance, so building an
     * updated or duplicated task never silently resets whether it was marked done.
     *
     * @param other the task to copy this task's completion status onto
     */
    protected void copyMarkedStatusTo(Task other) {
        if (this.isTaskMarked) {
            other.mark();
        }
    }

    /**
     * Encodes this task into a formatted single-line string representation for persistent storage.
     *
     * @return the formatted data string representing this task
     */
    public abstract String toFileString();

    /**
     * Marks the task as completed.
     */
    public void mark() {
        this.isTaskMarked = true;
    }

    /**
     * Unmarks the task, setting its state to incomplete.
     */
    public void unmark() {
        this.isTaskMarked = false;
    }

    /**
     * Returns the description or name of the task.
     *
     * @return the task name
     */
    protected String getName() {
        return name;
    }

    /**
     * Returns the completion state of the task.
     *
     * @return true if the task is marked as completed; false otherwise
     */
    protected boolean isMarked() {
        return isTaskMarked;
    }

    /**
     * Returns a string representation of the task including its completion status indicator.
     *
     * @return the formatted status string of the task
     */
    @Override
    public String toString() {
        return (isMarked() ? "[X] " : "[ ] ") + name;
    }
}
