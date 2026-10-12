package bogos;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Represents a task with a type, description, and completion state.
 */
public abstract class Task {
    protected static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final TaskType taskType;
    private final String description;
    private final List<String> tags;
    private boolean isDone;

    /**
     * Creates an incomplete task of the supplied type, description, and tags.
     *
     * @param taskType Type of this task.
     * @param description Description of the task.
     * @param tags Tags assigned to the task.
     * @throws IllegalArgumentException If the description or a tag is invalid.
     */
    protected Task(TaskType taskType, String description, List<String> tags) {
        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        this.taskType = taskType;
        this.description = description;
        validateTags(tags);
        this.tags = List.copyOf(tags);
        this.isDone = false;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's tags in the order in which they were assigned.
     *
     * @return Immutable tags without their display prefixes.
     */
    public List<String> getTags() {
        return tags;
    }

    public String getStatusIcon() {
        return (isDone() ? "X" : " ");
    }

    /**
     * Marks this task as complete.
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

    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns this task in its user-facing display format.
     *
     * @return User-facing task description.
     */
    @Override
    public String toString() {
        return appendTags(getBasicDisplayFormat());
    }

    /**
     * Returns this task's display format before its tags are appended.
     *
     * @return User-facing task description without tags.
     */
    protected String getBasicDisplayFormat() {
        return "[" + getTaskType().getStorageCode() + "][" + getStatusIcon() + "] "
                + getDescription();
    }

    /**
     * Returns this task in the format used by the data file.
     *
     * @return Data-file representation of this task.
     */
    public String toFileFormat() {
        return appendTags(getBasicFileFormat());
    }

    /**
     * Returns this task's file format before its tags are appended.
     *
     * @return Data-file representation without tags.
     */
    protected String getBasicFileFormat() {
        return getTaskType().getStorageCode()
                + " | " + (isDone() ? "true" : "false")
                + " | " + getDescription();
    }

    /**
     * Appends this task's tags to a display or storage representation.
     *
     * @param text Representation to which tags should be appended.
     * @return Representation including tags.
     */
    protected String appendTags(String text) {
        StringBuilder taggedText = new StringBuilder(text);
        String separator = text.contains(" | ") ? " | " : " #";

        for (String tag : tags) {
            taggedText.append(separator).append(tag);
        }
        return taggedText.toString();
    }

    /**
     * Validates that tags are non-blank and do not repeat.
     *
     * @param tags Tags to validate.
     * @throws IllegalArgumentException If a tag is blank or occurs more than once.
     */
    private void validateTags(List<String> tags) {
        Set<String> uniqueTags = new HashSet<>();
        for (String tag : tags) {
            if (tag.isBlank()) {
                throw new IllegalArgumentException("Task tags cannot be empty.");
            }
            if (!uniqueTags.add(tag)) {
                throw new IllegalArgumentException("Task tags cannot repeat.");
            }
        }
    }
}
