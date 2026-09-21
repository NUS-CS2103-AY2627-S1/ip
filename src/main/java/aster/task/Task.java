package aster.task;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Represents the information shared by every task type.
 */
public class Task {
    private static final Pattern TAG_PATTERN = Pattern.compile("#[A-Za-z0-9][A-Za-z0-9_-]*");

    private final String description;
    private final Set<String> tags;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        assert description != null : "Task descriptions should be provided by the caller.";
        this.description = description;
        this.tags = new LinkedHashSet<>();
        this.isDone = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the description of this task.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status character used in a task-list display.
     *
     * @return {@code X} when complete, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been marked as complete.
     *
     * @return {@code true} when complete, otherwise {@code false}
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Adds a tag to this task.
     *
     * @param tag tag text, for example {@code #fun}
     * @throws IllegalArgumentException when the tag format is invalid
     */
    public void addTag(String tag) {
        tags.add(normalizeTag(tag));
    }

    /**
     * Returns whether the supplied text is a valid tag.
     *
     * @param tag tag text to validate
     * @return {@code true} when the tag starts with {@code #} and contains only supported characters
     */
    public static boolean isValidTag(String tag) {
        return tag != null && TAG_PATTERN.matcher(tag).matches();
    }

    /**
     * Returns an ordered snapshot of tags attached to this task.
     *
     * @return immutable list of tags in insertion order
     */
    public List<String> getTags() {
        return List.copyOf(tags);
    }

    /**
     * Returns this task in the persistent storage format.
     *
     * @return storage line containing the type marker, status marker, and description
     */
    public String toStorageString() {
        return "T | " + (isDone ? "1" : "0") + " | " + getDescription() + getStorageTagSuffix();
    }

    /**
     * Returns the common status and description portion of a task display.
     * Subclasses prepend their task-type marker and append any scheduling details.
     *
     * @return status marker followed by the task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + getDescription() + getDisplayTagSuffix();
    }

    /**
     * Returns the storage suffix for this task's tags.
     *
     * @return {@code " | #tag1 #tag2"} when tags exist, otherwise an empty string
     */
    protected String getStorageTagSuffix() {
        if (tags.isEmpty()) {
            return "";
        }
        return " | " + String.join(" ", tags);
    }

    /**
     * Returns the display suffix for this task's tags.
     *
     * @return {@code " #tag1 #tag2"} when tags exist, otherwise an empty string
     */
    protected String getDisplayTagSuffix() {
        if (tags.isEmpty()) {
            return "";
        }
        return " " + String.join(" ", tags);
    }

    private static String normalizeTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }

        String trimmedTag = tag.trim();
        if (!isValidTag(trimmedTag)) {
            throw new IllegalArgumentException("Tags must start with # and use letters, digits, - or _.");
        }
        return trimmedTag;
    }
}
