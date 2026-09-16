package discitrack.task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a task with an activity description and completion status.
 */
public class Task {
    public static final String INVALID_TAG_MESSAGE = "UHOH! Tags must be 1-30 characters, "
            + "start with a letter or digit, and contain only English letters, digits, hyphens, or underscores.";

    private final String activity;
    private boolean isDone;
    private final Map<String, String> tags = new LinkedHashMap<>();

    /**
     * Creates a task with the given activity description.
     *
     * @param activity the activity represented by the task.
     */
    public Task(String activity) {
        if (activity == null || activity.isBlank()) {
            throw new IllegalArgumentException("OOPSIE! Add a task description.");
        }
        if (activity.contains(" | ") || activity.matches("(?s).*\\R.*")) {
            throw new IllegalArgumentException("UHOH! Keep the description on one line and avoid ' | '. "
                    + "Use a comma or dash instead so your task can be saved safely.");
        }
        this.activity = activity;
        this.isDone = false;
    }

    /**
     * Returns the activity description of this task.
     *
     * @return the activity description.
     */
    public String getActivity() {
        return this.activity;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return true if the task is done, otherwise false.
     */
    public boolean isDone() {
        return this.isDone;
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
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns the display symbol for this task's completion status.
     *
     * @return X if the task is done, otherwise a blank space.
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Validates a single tag name shared by commands and storage.
     *
     * @param tag the tag name to validate.
     * @throws IllegalArgumentException if the name is invalid.
     */
    public static void validateTag(String tag) {
        if (tag == null || !tag.matches("[A-Za-z0-9][A-Za-z0-9_-]{0,29}")) {
            throw new IllegalArgumentException(INVALID_TAG_MESSAGE);
        }
    }

    public List<String> getTags() {
        return List.copyOf(tags.values());
    }

    public String getTag(String tag) {
        validateTag(tag);
        return tags.get(tag.toLowerCase(Locale.ROOT));
    }

    /**
     * Adds a tag, preserving the first spelling and insertion order.
     *
     * @param tag the tag to add.
     * @return true if the task gained a tag.
     */
    public boolean addTag(String tag) {
        validateTag(tag);
        return tags.putIfAbsent(tag.toLowerCase(Locale.ROOT), tag) == null;
    }

    /**
     * Removes a tag without regard to case.
     *
     * @param tag the tag to remove.
     * @return the original spelling, or null if absent.
     */
    public String removeTag(String tag) {
        validateTag(tag);
        return tags.remove(tag.toLowerCase(Locale.ROOT));
    }

    /**
     * Replaces tags only after validating the entire collection.
     * Also restores spelling and order after a failed save.
     *
     * @param names the tags in display order.
     */
    public void replaceTags(List<String> names) {
        names.forEach(Task::validateTag);
        tags.clear();
        names.forEach(this::addTag);
    }

    protected String formatTaskBody() {
        return "[" + getStatusIcon() + "] " + this.activity;
    }

    protected String formatTags() {
        StringBuilder result = new StringBuilder();
        for (String tag : tags.values()) {
            result.append(" [#").append(tag).append("]");
        }
        return result.toString();
    }

    /**
     * Returns the display text for this task.
     *
     * @return the task formatted for display.
     */
    @Override
    public String toString() {
        return formatTaskBody() + formatTags();
    }
}
