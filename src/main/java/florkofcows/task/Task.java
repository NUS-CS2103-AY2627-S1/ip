package florkofcows.task;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Represents a generic task with a description and a completion status.
 * Serves as the base class for {@link Todo}, {@link Deadline}, and {@link Event}.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected final ArrayList<String> tags = new ArrayList<>();

    /**
     * Creates a task with the given description, initially marked as not done.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        String normalizedDescription = description == null ? "" : description.trim();
        if (normalizedDescription.isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be blank.");
        }
        this.description = normalizedDescription;
        this.isDone = false;
    }

    /**
     * Creates a task with the given description and completion status.
     *
     * @param description the text describing the task.
     * @param isDone whether the task is already marked as done.
     */
    public Task(String description, boolean isDone) {
        this(description);
        this.isDone = isDone;
    }

    /**
     * Returns the task's description.
     *
     * @return the description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Adds a single tag to the task.
     *
     * @param tag the tag to add, such as "#fun" or "fun".
     */
    public void addTag(String tag) {
        String normalizedTag = normalizeTag(tag);
        if (!normalizedTag.isEmpty() && !tags.contains(normalizedTag)) {
            tags.add(normalizedTag);
        }
    }

    /**
     * Adds multiple tags to the task.
     *
     * @param newTags the tags to add.
     */
    public void addTags(String... newTags) {
        if (newTags == null) {
            return;
        }
        for (String tag : newTags) {
            addTag(tag);
        }
    }

    /**
     * Returns the task's tags.
     *
     * @return a copy of the task's tags.
     */
    public ArrayList<String> getTags() {
        return new ArrayList<>(tags);
    }

    /**
     * Checks whether the task has a given tag.
     *
     * @param tag the tag to search for.
     * @return true if present, false otherwise.
     */
    public boolean hasTag(String tag) {
        return tags.contains(normalizeTag(tag));
    }

    /**
     * Returns whether this task occurs on the given date.
     * The base implementation always returns false, since a plain task
     * has no associated date; subclasses with dates override this.
     *
     * @param queryDate the date to check against.
     * @return true if this task occurs on queryDate, false otherwise.
     */
    public boolean isOccurringOn(LocalDate queryDate) {
        return false; // Default implementation for tasks without specific dates
    }

    /**
     * Reads a tag list from saved data and updates the task.
     *
     * @param rawTagString the tags serialized in the save file.
     */
    public void setTagsFromSaveFormat(String rawTagString) {
        if (rawTagString == null || rawTagString.trim().isEmpty()) {
            return;
        }
        String[] tagValues = rawTagString.trim().split("\\s+");
        addTags(tagValues);
    }

    /**
     * Returns a string representation of this task suitable for saving to a file.
     *
     * @return a string in the format "isDone | description" with optional tags appended.
     */
    public String toSaveFormat() {
        // The save format relies on a real description; otherwise it would serialize an invalid task.
        assert description != null : "Task description must be present before saving";
        String formatted = (isDone ? "1" : "0") + " | " + description;
        if (tags.isEmpty()) {
            return formatted;
        }
        return formatted + " | " + String.join(" ", tags);
    }

    @Override
    public String toString() {
        assert description != null : "Task description must be present before display";
        String formatted = "[" + (isDone ? "X" : " ") + "] " + description;
        if (tags.isEmpty()) {
            return formatted;
        }
        return formatted + " " + String.join(" ", tags);
    }

    private String normalizeTag(String tag) {
        if (tag == null) {
            return "";
        }
        String normalized = tag.trim();
        if (normalized.isEmpty()) {
            return "";
        }
        if (!normalized.startsWith("#")) {
            normalized = "#" + normalized;
        }
        return normalized;
    }
}

