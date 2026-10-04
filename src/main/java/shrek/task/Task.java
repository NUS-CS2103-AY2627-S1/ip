package shrek.task;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Represents a task with a description and a done status.
 */
public class Task {
    /** The maximum number of tags allowed on one task. */
    public static final int MAX_TAG_COUNT = 3;

    private static final Pattern TAG_PATTERN = Pattern.compile("#[A-Za-z0-9_-]{1,10}");

    protected String description;
    protected boolean isDone;
    private final SortedSet<String> tags;

    /**
     * Creates a new task with the given description. The task starts as not done.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        this(description, List.of());
    }

    /**
     * Creates a task with an initial collection of tags.
     *
     * @param description the text describing the task.
     * @param tags        the initial tags for the task.
     */
    public Task(String description, Collection<String> tags) {
        // Parser validates descriptions before constructing tasks, so a blank
        // description here indicates a programming error rather than bad user input.
        assert description != null && !description.isBlank()
                : "A task must have a non-blank description.";
        assert tags != null : "A task's tags cannot be null.";
        this.description = description;
        this.isDone = false;
        this.tags = new TreeSet<>();
        addTags(tags);
    }

    /**
     * Returns whether a value has valid tag syntax.
     *
     * @param tag the tag to validate.
     * @return true if the tag has the required format.
     */
    public static boolean isValidTag(String tag) {
        return tag != null && TAG_PATTERN.matcher(tag).matches();
    }

    /**
     * Normalizes a tag for storage and comparison.
     *
     * @param tag the tag to normalize.
     * @return the lowercase tag.
     * @throws IllegalArgumentException if the tag has invalid syntax.
     */
    public static String normalizeTag(String tag) {
        if (!isValidTag(tag)) {
            throw new IllegalArgumentException("Invalid tag: " + tag);
        }
        return tag.toLowerCase(Locale.ROOT);
    }

    /**
     * Adds tags atomically, rejecting duplicates and an excessive total count.
     *
     * @param newTags the tags to add.
     * @throws IllegalArgumentException if a tag is invalid, duplicated, or exceeds the limit.
     */
    public void addTags(Collection<String> newTags) {
        assert newTags != null : "Tags to add cannot be null.";
        SortedSet<String> updatedTags = new TreeSet<>(tags);
        for (String tag : newTags) {
            String normalizedTag = normalizeTag(tag);
            if (!updatedTags.add(normalizedTag)) {
                throw new IllegalArgumentException("Duplicate tag: " + tag);
            }
        }
        if (updatedTags.size() > MAX_TAG_COUNT) {
            throw new IllegalArgumentException("A task cannot have more than three tags.");
        }
        tags.clear();
        tags.addAll(updatedTags);
    }

    /**
     * Removes tags atomically, requiring every requested tag to be present.
     *
     * @param tagsToRemove the tags to remove.
     * @throws IllegalArgumentException if a tag is invalid, duplicated, or absent.
     */
    public void removeTags(Collection<String> tagsToRemove) {
        assert tagsToRemove != null : "Tags to remove cannot be null.";
        SortedSet<String> updatedTags = new TreeSet<>(tags);
        for (String tag : tagsToRemove) {
            String normalizedTag = normalizeTag(tag);
            if (!updatedTags.remove(normalizedTag)) {
                throw new IllegalArgumentException("Tag is not present: " + tag);
            }
        }
        tags.clear();
        tags.addAll(updatedTags);
    }

    /**
     * Checks whether this task has the given tag.
     *
     * @param tag the tag to check.
     * @return true if the normalized tag is present.
     */
    public boolean hasTag(String tag) {
        return tags.contains(normalizeTag(tag));
    }

    /**
     * Returns this task's tags in alphabetical order.
     *
     * @return an immutable list of tags.
     */
    public List<String> getTags() {
        return List.copyOf(tags);
    }

    /**
     * Checks whether two tasks describe the same logical task, ignoring completion status.
     *
     * @param other the task to compare with this task.
     * @return true when both tasks have the same type and canonical details.
     */
    public boolean hasSameDetailsAs(Task other) {
        return other != null && getClass().equals(other.getClass())
                && getCanonicalDetails().equals(other.getCanonicalDetails());
    }

    /**
     * Builds the canonical details used to detect duplicate tasks.
     *
     * @return normalized description and sorted tags.
     */
    protected String getCanonicalDetails() {
        return normalizeDescription(description) + "|" + String.join(",", tags);
    }

    private static String normalizeDescription(String value) {
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns tags formatted for display after a task description.
     *
     * @return a space-separated tag string, or an empty string.
     */
    protected String getTagsForDisplay() {
        return String.join(" ", tags);
    }

    /**
     * Returns tags formatted for the optional storage field.
     *
     * @return a comma-separated tag string, or an empty string.
     */
    protected String getTagsForStorage() {
        return String.join(",", tags);
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns "X" if the task is done, or a blank space otherwise.
     *
     * @return the status icon for this task.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description of this task.
     *
     * @return the task description.
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
     * Converts this task into a single line suitable for saving to disk.
     *
     * @return the save-format string for this task.
     */
    public String toSaveFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description + getTagsStorageSuffix();
    }

    /**
     * Returns the optional storage suffix for tagged tasks.
     *
     * @return the storage suffix, or an empty string when there are no tags.
     */
    protected String getTagsStorageSuffix() {
        return tags.isEmpty() ? "" : " | " + getTagsForStorage();
    }

    /**
     * Returns a user-facing string representation of this task.
     *
     * @return the display string for this task.
     */
    @Override
    public String toString() {
        String displayTags = getTagsForDisplay();
        return "[" + getStatusIcon() + "] " + description
                + (displayTags.isEmpty() ? "" : " " + displayTags);
    }
}
