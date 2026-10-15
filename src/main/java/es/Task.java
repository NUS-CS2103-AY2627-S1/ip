package es;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a task entered by the user and whether it has been completed.
 */
public abstract class Task {
    private final String description;
    private final TaskType type;
    private boolean isDone;
    private final List<String> tags = new ArrayList<>();

    /**
     * Creates an incomplete task with the given description and type.
     *
     * @param description the text describing the task
     * @param type the kind of task being created
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the description entered by the user
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the symbol used to display the completion status.
     *
     * @return {@code X} when complete, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
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

    /** Adds a case-sensitive tag, rejecting duplicates. */
    public void addTag(String tag) throws EsException {
        if (tag == null || !tag.matches("#[^\\s|]+") || tags.contains(tag)) {
            throw new EsException("Tags must be unique and contain no spaces or '|'.");
        }
        tags.add(tag);
    }

    /** Removes a tag if present. */
    public void removeTag(String tag) { tags.remove(tag); }

    /** Returns an immutable view of this task's tags. */
    public List<String> getTags() { return Collections.unmodifiableList(tags); }

    /**
     * Returns the encoded line used to save this task to disk.
     *
     * @return the storage line for this task
     */
    public abstract String toStorageLine();

    /**
     * Returns the shared type, completion flag, and description used in storage lines.
     *
     * @return the prefix {@code T | 0 | description} (with the matching type icon and flag)
     */
    protected String toStoragePrefix() {
        String tagField = tags.isEmpty() ? "" : " | " + String.join(",", tags);
        return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description + tagField;
    }

    /**
     * Returns the type icon, completion status, and description for all task types.
     *
     * @return the formatted task without type-specific extra details
     */
    @Override
    public String toString() {
        String tagText = tags.isEmpty() ? "" : " " + String.join(" ", tags);
        return "[" + type.getIcon() + "][" + getStatusIcon() + "] " + getDescription() + tagText;
    }
}
