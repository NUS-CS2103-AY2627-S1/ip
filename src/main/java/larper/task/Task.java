package larper.task;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private static final Pattern VALID_TAG_PATTERN = Pattern.compile("[A-Za-z0-9]+");
    private static final String FILE_FIELD_SEPARATOR = "|";

    private String description;
    private boolean isDone;
    private Set<String> tags;

    /**
     * Creates an unmarked task with the specified description.
     *
     * @param description Text that describes the task.
     */
    public Task(String description) {
        this(description, Collections.emptySet());
    }

    /**
     * Creates an unmarked task with the specified description and tags.
     *
     * @param description Text that describes the task.
     * @param tags One-word tags associated with the task.
     */
    public Task(String description, Collection<String> tags) {
        assert description != null && !description.isBlank() : "Task description should be provided before creation.";
        assert tags != null : "Task tags should be provided as an empty collection when there are none.";
        if (description.contains(FILE_FIELD_SEPARATOR)) {
            throw new IllegalArgumentException("Task descriptions cannot contain the storage field separator.");
        }
        this.description = description;
        this.isDone = false;
        this.tags = new TreeSet<>();
        addTags(tags);
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
        assert isDone : "Task should be marked after markAsDone runs.";
    }

    /**
     * Marks this task as not done.
     */
    public void unmarkAsDone() {
        isDone = false;
        assert !isDone : "Task should be unmarked after unmarkAsDone runs.";
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Adds a normalized one-word tag to this task.
     *
     * @param tag Tag text, with or without a leading number sign.
     * @throws IllegalArgumentException If the tag is not one word.
     */
    public void addTag(String tag) {
        String normalizedTag = normalizeTag(tag);
        if (!isValidTag(normalizedTag)) {
            throw new IllegalArgumentException("Task tags must contain one word.");
        }
        tags.add(normalizedTag);
    }

    /**
     * Adds all normalized one-word tags to this task.
     *
     * @param tags Tags to add.
     * @throws IllegalArgumentException If any tag is not one word.
     */
    public void addTags(Collection<String> tags) {
        assert tags != null : "Task tags should not be null.";
        for (String tag : tags) {
            addTag(tag);
        }
    }

    /**
     * Removes a normalized one-word tag from this task.
     *
     * @param tag Tag text, with or without a leading number sign.
     * @return True when the task contained the tag.
     * @throws IllegalArgumentException If the tag is not one word.
     */
    public boolean removeTag(String tag) {
        String normalizedTag = normalizeTag(tag);
        if (!isValidTag(normalizedTag)) {
            throw new IllegalArgumentException("Task tags must contain one word.");
        }
        return tags.remove(normalizedTag);
    }

    /**
     * Returns whether the task description or an exact tag matches a search phrase.
     *
     * @param phrase Search phrase to match, ignoring case.
     * @return True when the description contains the phrase or a tag equals it.
     */
    public boolean matchesSearch(String phrase) {
        assert phrase != null : "Search phrase should be an empty string instead of null.";
        String normalizedPhrase = phrase.trim().toLowerCase();
        if (normalizedPhrase.isEmpty()) {
            return false;
        }
        if (description.toLowerCase().contains(normalizedPhrase)) {
            return true;
        }
        return tags.contains(normalizeTagSearchPhrase(normalizedPhrase));
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task tags in normalized alphabetical order.
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns a detached copy of this task.
     */
    public Task copy() {
        Task taskCopy = new Task(description, tags);
        taskCopy.setDone(isDone);
        return taskCopy;
    }

    /**
     * Returns whether this task has the specified tag.
     *
     * @param tag Tag text, with or without a leading number sign.
     * @return True when the normalized tag is attached to this task.
     */
    public boolean hasTag(String tag) {
        String normalizedTag = normalizeTag(tag);
        return isValidTag(normalizedTag) && tags.contains(normalizedTag);
    }

    /**
     * Returns the task type icon used in console and file output.
     */
    public String getTypeIcon() {
        return "?";
    }

    /**
     * Returns this task in the storage file format.
     */
    public String toFileString() {
        assert getTypeIcon() != null && !getTypeIcon().isBlank() : "Task type icon should be available for storage.";
        return getBaseFileString() + getTagsFileSuffix();
    }

    private String getDoneStatusForFile() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task in the console display format.
     */
    @Override
    public String toString() {
        assert getTypeIcon() != null && !getTypeIcon().isBlank() : "Task type icon should be available for display.";
        return formatWithTags(formatTaskWithoutTags());
    }

    /**
     * Returns the task's base storage fields without date details or tags.
     */
    protected String getBaseFileString() {
        return getTypeIcon() + " | " + getDoneStatusForFile() + " | " + description;
    }

    /**
     * Returns the task's display text without date details or tags.
     */
    protected String formatTaskWithoutTags() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns display text with this task's tags appended as tag chips.
     *
     * @param taskText Display text before tags are appended.
     * @return Display text with tags appended when tags exist.
     */
    protected String formatWithTags(String taskText) {
        assert taskText != null : "Task display text should not be null.";
        if (tags.isEmpty()) {
            return taskText;
        }
        String tagText = tags.stream()
                .map(tag -> "[#" + tag + "]")
                .collect(Collectors.joining(" "));
        return taskText + " " + tagText;
    }

    /**
     * Returns the storage suffix containing this task's tags.
     */
    protected String getTagsFileSuffix() {
        if (tags.isEmpty()) {
            return "";
        }
        String tagText = tags.stream()
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));
        return " | " + tagText;
    }

    /**
     * Returns whether a normalized tag contains one valid word.
     *
     * @param tag Normalized tag text without a number sign.
     * @return True when the tag contains only letters and numbers.
     */
    public static boolean isValidTag(String tag) {
        return tag != null && VALID_TAG_PATTERN.matcher(tag).matches();
    }

    /**
     * Returns a tag in its normalized lowercase form without a number sign.
     *
     * @param tag Tag text, with or without a leading number sign.
     * @return Normalized tag text.
     */
    public static String normalizeTag(String tag) {
        if (tag == null) {
            return "";
        }
        String normalizedTag = tag.trim();
        if (normalizedTag.startsWith("#")) {
            normalizedTag = normalizedTag.substring(1);
        }
        return normalizedTag.toLowerCase();
    }

    private String normalizeTagSearchPhrase(String phrase) {
        return phrase.startsWith("#") ? phrase.substring(1) : phrase;
    }
}
