package marcus.task;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Represents one task and whether it has been completed.
 */
public class Task {
    /** Description shown to the user and stored in the data file. */
    protected String description;
    /** Whether this task has been completed. */
    protected boolean isDone;
    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null : "A task must have a description";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon used when displaying the task.
     *
     * @return {@code X} for a completed task, otherwise a space.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Adds a tag to this task after normalizing it to lowercase.
     *
     * @param tag tag to add.
     * @return whether the tag was not already present.
     */
    public boolean addTag(String tag) {
        return tags.add(tag.toLowerCase(Locale.ROOT));
    }

    /**
     * Removes one tag from this task.
     *
     * @param tag tag to remove.
     * @return whether the tag was present.
     */
    public boolean removeTag(String tag) {
        return tags.remove(tag.toLowerCase(Locale.ROOT));
    }

    /** Removes all tags from this task. */
    public void removeAllTags() {
        tags.clear();
    }

    /**
     * Reports whether this task has the supplied tag.
     *
     * @param tag tag to search for.
     * @return whether this task has the tag.
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task's tags in display order.
     *
     * @return an unmodifiable view of the task's tags.
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns this task's tags separated by spaces.
     *
     * @return the task's tags, or an empty string when it has none.
     */
    public String getTagsString() {
        return String.join(" ", tags);
    }

    /**
     * Returns the optional tag field used for persistence.
     *
     * @return a storage delimiter and tags, or an empty string when there are no tags.
     */
    protected String getTagsFileSuffix() {
        return tags.isEmpty() ? "" : " | " + getTagsString();
    }

    /**
     * Reports whether this task occurs on a particular date.
     * Tasks without a date, such as to-dos, do not occur on any date.
     *
     * @param date date to check.
     * @return {@code false} for a task without a date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Reports whether this task's description contains the supplied keyword.
     *
     * @param keyword keyword to search for.
     * @return whether the description contains the keyword, ignoring case.
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns this task in the format used for persistence.
     *
     * @return task type, completion state, and description separated by pipes.
     */
    public String toFileString() {
        return "T | " + (isDone ? "1" : "0") + " | " + description + getTagsFileSuffix();
    }

    /**
     * Returns this task in its display format.
     *
     * @return status icon followed by the task description.
     */
    @Override
    public String toString() {
        String tagSuffix = tags.isEmpty() ? "" : " " + getTagsString();
        return "[" + getStatusIcon() + "] " + description + tagSuffix;
    }

    /**
     * Returns this task's display format without its tags.
     *
     * @return status icon followed by the task description.
     */
    public String toStringWithoutTags() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
