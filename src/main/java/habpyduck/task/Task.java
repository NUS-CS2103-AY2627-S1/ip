package habpyduck.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

import habpyduck.storage.Storage;

/**
 * Represents one task in the user's task list.
 */
public class Task {
    private static final String TAG_PATTERN = "#[a-z0-9_-]+";

    protected String description;
    protected boolean isDone;
    private final ArrayList<String> tags;

    /**
     * Creates a task with the given description.
     *
     * @param description the text that describes the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        tags = new ArrayList<>();
    }

    /**
     * Returns the icon used to show whether this task is done.
     *
     * @return X if the task is done, or a blank space if it is not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return true if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the text that describes the task.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Adds a tag if the task does not already have it.
     *
     * @param tag the normalized tag to add.
     * @return true if the tag was added.
     */
    public boolean addTag(String tag) {
        assert isValidTag(tag) : "Tag should be validated before being added to a task";
        if (tags.contains(tag)) {
            return false;
        }
        tags.add(tag);
        return true;
    }

    /**
     * Removes a tag from this task.
     *
     * @param tag the normalized tag to remove.
     * @return true if the tag was removed.
     */
    public boolean removeTag(String tag) {
        assert isValidTag(tag) : "Tag should be validated before being removed from a task";
        return tags.remove(tag);
    }

    /**
     * Returns whether this task has the given tag.
     *
     * @param tag the normalized tag to check.
     * @return true if the task has the tag.
     */
    public boolean hasTag(String tag) {
        assert isValidTag(tag) : "Tag should be validated before checking a task";
        return tags.contains(tag);
    }

    /**
     * Returns a copy of this task's tags.
     *
     * @return the tags attached to this task.
     */
    public ArrayList<String> getTags() {
        return new ArrayList<>(tags);
    }

    /**
     * Returns this task's tags in the format used when displaying tasks.
     *
     * @return a leading-space-prefixed tag list, or an empty string if there are no tags.
     */
    protected String getDisplayTags() {
        if (tags.isEmpty()) {
            return "";
        }
        return " " + String.join(" ", tags);
    }

    /**
     * Returns this task in the simple text format used when saving tasks.
     *
     * @return a line of text that can be written to the save file.
     */
    public String toFileString() {
        return Storage.TODO_TASK_TYPE + Storage.FILE_FIELD_SEPARATOR + getDoneStatus()
                + Storage.FILE_FIELD_SEPARATOR + Storage.escapeFileField(description)
                + formatTagsForFile();
    }

    /**
     * Returns the numeric status used in the save file.
     *
     * @return 1 if this task is done, or 0 if it is not done.
     */
    protected String getDoneStatus() {
        return isDone ? Storage.DONE_STATUS : Storage.NOT_DONE_STATUS;
    }

    /**
     * Returns the task in the format shown to users.
     *
     * @return the task status icon and description.
     */
    @Override
    public String toString() {
        return getBaseDisplayText() + getDisplayTags();
    }

    /**
     * Returns the common task display text without task type or tags.
     *
     * @return the task status icon and description.
     */
    protected String getBaseDisplayText() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns a normalized tag that can be stored and compared.
     *
     * @param tag the user-entered tag.
     * @return the normalized tag.
     */
    public static String normalizeTag(String tag) {
        return tag.toLowerCase();
    }

    /**
     * Returns whether a tag follows HabpyDuck's tag format.
     *
     * @param tag the tag to check.
     * @return true if the tag starts with # and uses only allowed characters.
     */
    public static boolean isValidTag(String tag) {
        return tag.matches(TAG_PATTERN);
    }

    /**
     * Returns saved-file text for this task's tags, if it has any.
     *
     * @return an empty string for untagged tasks, or one separator plus comma-separated tags.
     */
    protected String formatTagsForFile() {
        if (tags.isEmpty()) {
            return "";
        }
        String savedTags = tags.stream()
                .map(Storage::escapeFileField)
                .collect(Collectors.joining(","));
        return Storage.FILE_FIELD_SEPARATOR + savedTags;
    }
}
