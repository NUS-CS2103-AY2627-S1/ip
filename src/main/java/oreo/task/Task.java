package oreo.task;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import oreo.exception.OreoException;

/**
 * Represents the shared state and behaviour of a task.
 */
public abstract class Task {
    /** Pattern accepted for a user-defined task tag. */
    private static final Pattern TAG_PATTERN = Pattern.compile("#[A-Za-z0-9_-]+");

    /** The text entered by the user to describe the task. */
    private final String description;

    /** Whether the task has been marked as completed. */
    private boolean isDone;

    /** Tags attached to this task, retained in the order in which they were added. */
    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Creates a task that is initially not done.
     *
     * @param description text describing the task
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new OreoException("A task description cannot be empty.");
        }
        if (description.contains("\n") || description.contains("\r") || description.contains(" | ")) {
            throw new OreoException("A task description contains unsupported characters.");
        }
        this.description = description;
        this.isDone = false;
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
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the display icon for this task's completion state.
     *
     * @return {@code X} for completed tasks, otherwise a blank space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns this task's tags in their display order. */
    public List<String> getTags() {
        return List.copyOf(tags);
    }

    /** Returns whether this task has the same user-visible details as another task. */
    public boolean hasSameDetails(Task other) {
        return other != null && getClass().equals(other.getClass())
                && description.equalsIgnoreCase(other.description)
                && getDetails().equals(other.getDetails()) && tags.equals(new LinkedHashSet<>(other.tags));
    }

    /** Returns whether the supplied text is a valid task tag. */
    public static boolean isValidTag(String tag) {
        return TAG_PATTERN.matcher(tag).matches();
    }

    /** Adds valid tags to this task. Duplicate tags are ignored. */
    public void addTags(Collection<String> tagsToAdd) {
        for (String tag : tagsToAdd) {
            if (!isValidTag(tag)) {
                throw new IllegalArgumentException("Invalid task tag: " + tag);
            }
            tags.add(tag.toLowerCase(Locale.ROOT));
        }
    }

    /** Removes the supplied tags from this task. Tags that are absent are ignored. */
    public void removeTags(Collection<String> tagsToRemove) {
        for (String tag : tagsToRemove) {
            tags.remove(tag.toLowerCase(Locale.ROOT));
        }
    }

    /** Returns this task's optional trailing storage field for its tags. */
    protected String getTagsStorageRepresentation() {
        return tags.isEmpty() ? "" : " | tags: " + String.join(",", tags);
    }

    /**
     * Returns the icon identifying this kind of task.
     *
     * @return task type icon
     */
    protected abstract String getTypeIcon();

    /**
     * Returns optional type-specific details to append to the task description.
     *
     * @return formatted task details, or an empty string when none apply
     */
    protected abstract String getDetails();

    protected abstract String toStorageString();

    /**
     * Returns a formatted representation of this task.
     *
     * @return task type, completion status, description, and type-specific details
     */
    @Override
    public String toString() {
        String displayedTags = tags.isEmpty() ? "" : " " + String.join(" ", tags);
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description + getDetails() + displayedTags;
    }
}
