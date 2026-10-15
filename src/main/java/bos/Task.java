package bos;

import java.util.Locale;
import java.util.StringJoiner;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    static final String COMPLETED_STORAGE_STATUS = "1";
    static final String INCOMPLETE_STORAGE_STATUS = "0";
    private static final String STORAGE_FIELD_DELIMITER = " | ";

    private final String description;
    private boolean isDone;

    /**
     * Creates an uncompleted task with the given description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Task description must not be blank";

        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} if completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
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
     * Checks whether this task's description contains a keyword, ignoring case.
     *
     * @param keyword text to search for.
     * @return true when the keyword occurs in the description.
     */
    public boolean descriptionContains(String keyword) {
        assert keyword != null && !keyword.isBlank()
                : "Search keyword must not be blank";

        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Checks whether another task has the same description, ignoring case and repeated whitespace.
     * Task type, completion status, and scheduling details are excluded from the comparison.
     *
     * @param other task to compare with.
     * @return true when both tasks have the same description.
     */
    public boolean hasSameDescription(Task other) {
        if (other == null) {
            return false;
        }

        String normalizedDescription = description.strip().replaceAll("\\s+", " ");
        String normalizedOtherDescription = other.description.strip().replaceAll("\\s+", " ");
        return normalizedDescription.equalsIgnoreCase(normalizedOtherDescription);
    }

    /**
     * Formats the fields shared by every stored task, followed by any type-specific fields.
     *
     * @param taskType storage identifier for the task type.
     * @param additionalFields type-specific fields to append.
     * @return task fields joined using the storage delimiter.
     */
    protected String formatStorageFields(String taskType, String... additionalFields) {
        String storageStatus = isDone ? COMPLETED_STORAGE_STATUS : INCOMPLETE_STORAGE_STATUS;
        StringJoiner fields = new StringJoiner(STORAGE_FIELD_DELIMITER);
        fields.add(taskType).add(storageStatus).add(description);
        for (String field : additionalFields) {
            fields.add(field);
        }
        return fields.toString();
    }

    /**
     * Returns a readable representation containing the completion status and description.
     *
     * @return formatted task for display.
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + description;
    }

    /**
     * Converts this task into the text-file storage format.
     *
     * @return storage representation of this task.
     */
    public abstract String formatForStorage();
}
