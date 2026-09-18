package bobby.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bobby.BobbyException;

/**
 * Represents one task in Bobby's in-memory task list.
 */
public class Task {
    private final String description;
    private final TaskType type;
    private final List<String> tags;
    private boolean isDone;

    /**
     * Creates a task that starts out as not done.
     *
     * @param description text entered by the user to describe the task.
     * @param type category of the task.
     */
    public Task(String description, TaskType type) {
        assert description != null : "Task description should be non-null.";
        assert type != null : "Task type should be non-null.";

        this.description = description;
        this.type = type;
        this.tags = new ArrayList<>();
        this.isDone = false;
    }

    /**
     * Returns a line that can be saved in the data file.
     *
     * @return serialized task data.
     */
    public String toStorageString() {
        return typeAndStatusStorageString() + " | " + description + tagsStorageString();
    }

    /**
     * Returns a stable key used to detect duplicate tasks.
     *
     * @return task type and core details, without status or tags.
     */
    public String getIdentityKey() {
        return type.getSymbol() + " | " + description;
    }

    /**
     * Returns the task description entered by the user.
     *
     * @return task description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Returns the serialized task type and done status.
     *
     * @return serialized task type and done status.
     */
    protected String typeAndStatusStorageString() {
        return type.getSymbol() + " | " + getStorageStatus();
    }

    /**
     * Returns the serialized tag field, if this task has tags.
     *
     * @return storage suffix for tags, or an empty string if there are no tags.
     */
    protected String tagsStorageString() {
        if (tags.isEmpty()) {
            return "";
        }
        return " | " + String.join(" ", tags);
    }

    /**
     * Returns whether this task's description or tags contain the given keyword.
     *
     * @param keyword text to search for
     * @return true if the description or tags contain the keyword
     */
    public boolean containsKeyword(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return description.toLowerCase(Locale.ROOT).contains(lowerCaseKeyword)
                || tags.stream().anyMatch(tag -> tag.toLowerCase(Locale.ROOT).contains(lowerCaseKeyword));
    }

    /**
     * Adds a tag to this task.
     *
     * @param tag tag text, including the leading #.
     * @throws BobbyException if the tag is already present.
     */
    public void addTag(String tag) throws BobbyException {
        assert tag != null : "Task tag should be non-null.";
        assert isValidTag(tag) : "Task tag should be valid.";

        if (tags.contains(tag)) {
            throw new BobbyException("That task already has this tag.");
        }
        tags.add(tag);
    }

    /**
     * Returns whether the given text is a valid task tag.
     *
     * @param tag tag text to check.
     * @return true if the tag starts with # and contains no spaces.
     */
    public static boolean isValidTag(String tag) {
        return tag != null && tag.matches("#[A-Za-z0-9][A-Za-z0-9_-]*");
    }

    /**
     * Returns whether text is safe to use as a task description.
     *
     * @param description task description to check.
     * @return true if the description can be displayed and stored safely.
     */
    public static boolean isValidDescription(String description) {
        return description != null && !description.isBlank()
                && !description.contains("|")
                && description.chars().noneMatch(Character::isISOControl);
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
     * Returns this task in the display format expected by the chatbot.
     *
     * @return task status and description.
     */
    @Override
    public String toString() {
        return baseDisplayString() + tagsDisplayString();
    }

    /**
     * Returns this task's display text without tags.
     *
     * @return task type, status, and description.
     */
    protected String baseDisplayString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the display suffix for tags, if this task has tags.
     *
     * @return display suffix for tags, or an empty string if there are no tags.
     */
    protected String tagsDisplayString() {
        if (tags.isEmpty()) {
            return "";
        }
        return " " + String.join(" ", tags);
    }

    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    private String getStorageStatus() {
        return isDone ? "1" : "0";
    }
}
