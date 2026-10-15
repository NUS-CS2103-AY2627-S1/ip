package gnaix.task;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private String description;
    private boolean isCompleted;
    private final Set<String> tags;

    /**
     * Creates a task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
        this.tags = new LinkedHashSet<>();
    }

    @Override
    public String toString() {
        String status = isCompleted ? "[X] " : "[ ] ";

        if (tags.isEmpty()) {
            return status + this.description;
        }

        String tagText = tags.stream()
                .map(tag -> "#" + tag)
                .reduce("", (result, tag) -> result + " " + tag);

        return status + this.description + tagText;
    }

    /**
     * Returns the task description.
     *
     * @return Description of the task.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Marks the task as complete.
     */
    public void markAsComplete() {
        this.isCompleted = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void markAsIncomplete() {
        this.isCompleted = false;
    }

    /**
     * Returns whether the task is complete.
     *
     * @return True if the task is complete.
     */
    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Returns whether the task occurs on the specified date.
     *
     * @param date Date to check.
     * @return False because a generic task has no associated date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Adds a tag to this task.
     *
     * @param tag Tag to add, with or without the leading '#'.
     */
    public void addTag(String tag) {
        tags.add(normaliseTag(tag));
    }

    /**
     * Returns the tags attached to this task.
     *
     * @return Read-only set of tags.
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns whether this task has the specified tag.
     *
     * @param tag Tag to check.
     * @return True if the task contains the tag.
     */
    public boolean hasTag(String tag) {
        return tags.contains(normaliseTag(tag));
    }

    /**
     * Normalises a tag for storage and comparison.
     *
     * @param tag Tag to normalise.
     * @return Lowercase tag without the leading '#'.
     */
    private String normaliseTag(String tag) {
        String normalised = tag.trim();

        if (normalised.startsWith("#")) {
            normalised = normalised.substring(1);
        }

        return normalised.toLowerCase(Locale.ROOT);
    }
}
