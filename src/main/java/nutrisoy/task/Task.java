package nutrisoy.task;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private static final String TAG_NAME_PATTERN = "[A-Za-z0-9][A-Za-z0-9_-]*";

    protected String description;
    protected boolean isDone;
    private final LinkedHashSet<String> tags;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        this.description = description;
        this.isDone = false;
        this.tags = new LinkedHashSet<>();
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return {@code "X"} if complete; otherwise a space character
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the description of the task.
     *
     * @return The description of the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return {@code true} if the task is complete
     */
    public boolean isDone() {
        return isDone;
    }


    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkAsDone() {
        this.isDone = false;
    }

    /**
     * Adds a unique tag to this task.
     *
     * @param tagName name of the tag to add
     */
    public void addTag(String tagName) {
        assert tagName != null && !tagName.isBlank() : "Tag name must be valid";
        if (!hasTag(tagName)) {
            tags.add(tagName);
        }
    }

    /**
     * Removes a tag from this task if it exists.
     *
     * @param tagName name of the tag to remove
     */
    public void removeTag(String tagName) {
        assert tagName != null && !tagName.isBlank() : "Tag name must be valid";
        tags.removeIf(tag -> tag.equalsIgnoreCase(tagName));
    }

    /**
     * Returns whether this task has the supplied tag, ignoring letter case.
     *
     * @param tagName tag name to check
     * @return {@code true} if the task has the tag
     */
    public boolean hasTag(String tagName) {
        assert tagName != null : "Checked tag name must not be null";
        return tags.stream().anyMatch(tag -> tag.equalsIgnoreCase(tagName));
    }

    /**
     * Returns whether a tag name is safe to store and display.
     *
     * @param tagName tag name to validate
     * @return {@code true} if the tag name uses only supported characters
     */
    public static boolean isValidTagName(String tagName) {
        return tagName != null && tagName.matches(TAG_NAME_PATTERN);
    }

    /**
     * Returns whether another task has the same type and description.
     *
     * @param other task to compare
     * @return {@code true} if both tasks have the same identifying details
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns this task in the storage-file format.
     *
     * @return storage-file representation of this task
     */
    public String toFileFormat() {
        return getCoreFileFormat() + getTagsFileSuffix();
    }

    /**
     * Returns the fields shared by every stored task, excluding type-specific fields and tags.
     *
     * @return completion status and description in storage-file format
     */
    protected String getCoreFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns the optional tag field, including its delimiter when tags exist.
     *
     * @return tag suffix in storage-file format, or an empty string when the task has no tags
     */
    protected String getTagsFileSuffix() {
        return tags.isEmpty() ? "" : " | " + String.join(",", tags);
    }

    /**
     * Returns a display representation of this task.
     *
     * @return display representation of this task
     */
    @Override
    public String toString() {
        String formattedTags = tags.stream()
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));
        String tagSuffix = formattedTags.isEmpty() ? "" : " " + formattedTags;
        return "[" + getStatusIcon() + "] " + description + tagSuffix;
    }
}
