package edith.task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/** Represents a task and whether it has been completed. */
public abstract class Task {
    private static final Pattern TAG_PATTERN = Pattern.compile("#[A-Za-z0-9_-]+");

    /** The text describing what the user needs to do. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /** The first spelling of each tag, keyed by its case-insensitive name. */
    private final Map<String, String> tags;

    /**
     * Creates a new incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.tags = new LinkedHashMap<>();
    }

    /**
     * Marks this task as completed.
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
     * Returns the status icon used when displaying this task.
     *
     * @return {@code [X]} for a completed task, or {@code [ ]} otherwise
     */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Returns the description of this task.
     *
     * @return the text describing the task
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns whether a tag uses Edith's supported spelling.
     *
     * @param tag the tag to check
     * @return whether the tag starts with {@code #} and contains only supported characters
     */
    public static boolean isValidTag(String tag) {
        return tag != null && TAG_PATTERN.matcher(tag).matches();
    }

    /**
     * Adds a tag unless one with the same case-insensitive name already exists.
     *
     * @param tag the valid tag to add
     * @return whether the task gained a tag
     */
    public boolean addTag(String tag) {
        if (!isValidTag(tag)) {
            throw new IllegalArgumentException("Invalid tag: " + tag);
        }
        String normalizedTag = tag.toLowerCase(Locale.ROOT);
        return tags.putIfAbsent(normalizedTag, tag) == null;
    }

    /**
     * Removes a tag using its case-insensitive name.
     *
     * @param tag the valid tag to remove
     * @return whether the task lost a tag
     */
    public boolean removeTag(String tag) {
        if (!isValidTag(tag)) {
            throw new IllegalArgumentException("Invalid tag: " + tag);
        }
        return tags.remove(tag.toLowerCase(Locale.ROOT)) != null;
    }

    /** Returns an immutable snapshot of tags in the order they were first added. */
    public List<String> getTags() {
        return List.copyOf(tags.values());
    }

    /**
     * Restores the exact tags and their order after an unsuccessful save.
     *
     * @param previousTags the tags from before the failed change
     */
    public void restoreTags(List<String> previousTags) {
        tags.clear();
        for (String tag : previousTags) {
            addTag(tag);
        }
    }

    /** Returns the visible tag suffix, or an empty string when the task has no tags. */
    protected String getTagSuffix() {
        return tags.isEmpty() ? "" : " [tags: " + String.join(" ", tags.values()) + "]";
    }

    /** Returns the task text stored in legacy numbered task lines, without tag metadata. */
    public abstract String toStorageString();

    /**
     * Returns the lower-case command name for this task type.
     *
     * @return the identifier for the concrete task type
     */
    protected abstract String getTaskType();
}
