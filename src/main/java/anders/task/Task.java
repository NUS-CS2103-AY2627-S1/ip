package anders.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Represents a task in the task list.
 */
public class Task {
    private static final Pattern TAG_PATTERN = Pattern.compile("#[A-Za-z0-9_-]+");
    private final String description;
    private final Set<String> tags;
    private boolean isDone;

    /**
     * Creates a new unfinished task.
     *
     * @param description the task description
     */
    public Task(String description) {
        this.description = description;
        this.tags = new LinkedHashSet<>();
        this.isDone = false;
    }

    /**
     * Adds one or more tags to this task.
     *
     * @param newTags tags to add
     * @return whether at least one new tag was added
     * @throws IllegalArgumentException if any supplied tag is invalid
     */
    public boolean addTags(Collection<String> newTags) {
        assert newTags != null : "Tags to add must not be null";
        List<String> normalizedTags = normalizeTags(newTags);
        return tags.addAll(normalizedTags);
    }

    /**
     * Removes one or more tags from this task.
     *
     * @param tagsToRemove tags to remove
     * @return whether at least one tag was removed
     * @throws IllegalArgumentException if any supplied tag is invalid
     */
    public boolean removeTags(Collection<String> tagsToRemove) {
        assert tagsToRemove != null : "Tags to remove must not be null";
        List<String> normalizedTags = normalizeTags(tagsToRemove);
        return tags.removeAll(normalizedTags);
    }

    /** Returns the tags attached to this task in insertion order. */
    public List<String> getTags() {
        return List.copyOf(tags);
    }

    /** Returns whether this task has the supplied tag. */
    public boolean hasTag(String tag) {
        return tags.contains(normalizeTag(tag));
    }

    /** Returns whether the supplied value is a valid tag. */
    public static boolean isValidTag(String tag) {
        return tag != null && TAG_PATTERN.matcher(tag).matches();
    }

    /** Returns the supplied tag normalized to lowercase. */
    public static String normalizeTag(String tag) {
        if (!isValidTag(tag)) {
            throw new IllegalArgumentException("Invalid tag");
        }
        return tag.toLowerCase(Locale.ROOT);
    }

    /** Returns the formatted tag suffix used in task display text. */
    public String getTagsDisplayText() {
        return tags.isEmpty() ? "" : " (tags: " + String.join(" ", tags) + ")";
    }

    private static List<String> normalizeTags(Collection<String> tagsToNormalize) {
        List<String> normalizedTags = new ArrayList<>();
        for (String tag : tagsToNormalize) {
            normalizedTags.add(normalizeTag(tag));
        }
        return normalizedTags;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** @return the task description */
    public String getDescription() {
        return description;
    }

    /** @return whether this task is completed */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description for subclasses to reuse in their display text.
     *
     * @return the task description
     */
    @Override
    public String toString() {
        return description;
    }
}
