package gooble.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a task in Gooble's task list.
 */
public class Task {
    private static final int MAX_TAGS = 3;
    private static final Pattern VALID_TAG = Pattern.compile("#[A-Za-z0-9_-]+");

    /** The text describing this task. */
    protected String description;

    /** Whether this task has been completed. */
    protected boolean isDone;

    /** Tags assigned to this task, in insertion order. */
    private final List<String> tags;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        this.description = description;
        this.isDone = false;
        this.tags = new ArrayList<>();
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, or a blank space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /** Returns this task's tags in insertion order. */
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /** Returns whether this task has at least one tag. */
    public boolean hasTags() {
        return !tags.isEmpty();
    }

    /** Returns whether two tasks have the same user-visible details. */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()
                || !description.equals(other.description) || !tags.equals(other.tags)) {
            return false;
        }
        if (this instanceof Deadline thisDeadline && other instanceof Deadline otherDeadline) {
            return thisDeadline.getStoredDeadline().equals(otherDeadline.getStoredDeadline());
        }
        if (this instanceof Event thisEvent && other instanceof Event otherEvent) {
            return Objects.equals(thisEvent.getStartDate(), otherEvent.getStartDate())
                    && Objects.equals(thisEvent.getEndDate(), otherEvent.getEndDate());
        }
        return true;
    }

    /** Adds a normalized tag, removing the oldest tag when the limit is reached. */
    public void addTag(String tag) {
        String normalizedTag = normalizeTag(tag);
        if (tags.contains(normalizedTag)) {
            throw new IllegalArgumentException("This task already has that tag.");
        }
        if (tags.size() == MAX_TAGS) {
            tags.remove(0);
        }
        tags.add(normalizedTag);
    }

    /** Removes all tags from this task. */
    public void removeTags() {
        tags.clear();
    }

    /** Returns this task's tags formatted for display. */
    protected String formatTags() {
        return hasTags() ? " [" + String.join(", ", tags) + "]" : "";
    }

    /** Returns whether the supplied value is a valid tag. */
    public static boolean isValidTag(String tag) {
        return tag != null && VALID_TAG.matcher(tag).matches();
    }

    /** Normalizes and validates a tag. */
    private String normalizeTag(String tag) {
        String normalizedTag = tag == null ? "" : tag.toLowerCase(Locale.ROOT);
        if (!isValidTag(normalizedTag)) {
            throw new IllegalArgumentException("Invalid tag format.");
        }
        return normalizedTag;
    }

    /**
     * Returns the formatted representation used for a general task.
     *
     * @return the completion status and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description + formatTags();
    }
}
