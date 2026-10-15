package trybot.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a task stored by TryBot.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    private final List<String> tags;

    /**
     * Creates a task that is initially not done.
     *
     * @param description text describing the task.
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A task description cannot be blank.");
        }
        this.description = description;
        this.isDone = false;
        this.tags = new ArrayList<>();
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return X for a completed task, or a space for an incomplete task
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the tags attached to this task in the order they were added.
     *
     * @return immutable list of tag names
     */
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Attaches a tag to this task unless it is already present.
     *
     * @param tagName tag name to attach.
     * @throws IllegalArgumentException if the tag name is blank
     */
    public void addTag(String tagName) {
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("A tag name cannot be blank.");
        }
        String trimmedTag = tagName.trim();
        if (!tags.contains(trimmedTag)) {
            tags.add(trimmedTag);
        }
    }

    /** Removes every tag attached to this task. */
    public void clearTags() {
        tags.clear();
    }

    /**
     * Returns tags formatted for display.
     *
     * @return a space-separated sequence of hashtag-prefixed tag names
     */
    protected String getTagsForDisplay() {
        return tags.stream().map(tag -> "#" + tag).reduce("", (left, right) -> left + " " + right);
    }

    /**
     * Formats this task for the task data file.
     *
     * @return the task type, completion status, and description
     */
    public String toStorageString() {
        return "T | " + (isDone ? "1" : "0") + " | " + escapeStorageField(description)
                + getTagsForStorage();
    }

    /**
     * Escapes characters that have a special meaning in the task data file.
     *
     * @param field field text to escape.
     * @return escaped field text
     */
    protected String escapeStorageField(String field) {
        if (field == null) {
            throw new IllegalArgumentException("Task data fields cannot be null.");
        }
        return field.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Formats this task with its completion status.
     *
     * @return status icon and task description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description + getTagsForDisplay();
    }

    /**
     * Formats the optional tags for the task data file.
     *
     * @return zero or more escaped storage fields
     */
    protected String getTagsForStorage() {
        return tags.stream().map(tag -> " | " + escapeStorageField(tag)).reduce("", String::concat);
    }
}
