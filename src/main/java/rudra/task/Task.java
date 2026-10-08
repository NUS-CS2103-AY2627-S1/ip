package rudra.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final TaskType taskType;
    private final String description;
    private boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param taskType Type of task being created.
     * @param description Description of the task.
     */
    public Task(TaskType taskType, String description) {
        assert taskType != null : "A task must have a type";
        assert description != null && !description.isBlank() : "A task must have a non-blank description";

        this.taskType = taskType;
        this.description = description;
        this.isDone = false;
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
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the status icon used when displaying the task.
     *
     * @return {@code X} if the task is done, otherwise a space.
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns the task description text entered by the user.
     *
     * @return Task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether the task description contains the given keyword, ignoring letter case.
     *
     * @param keyword Search term entered by the user.
     * @return {@code true} if the description contains the keyword.
     */
    public boolean matchesKeyword(String keyword) {
        String normalizedDescription = this.description.toLowerCase(Locale.ROOT);
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return normalizedDescription.contains(normalizedKeyword);
    }

    /**
     * Returns the task type for storage and display purposes.
     *
     * @return Task type of this task.
     */
    public TaskType getTaskType() {
        return this.taskType;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return {@code true} when the task is done, otherwise {@code false}.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Converts this task into a plain-text storage line.
     *
     * @return Text representation suitable for saving to disk.
     */
    public String toStorageString() {
        List<String> storageFields = getStorageFields();
        assert storageFields.size() >= 3 : "Every task must provide type, status, and description fields";

        return storageFields.stream()
                .map(Task::escapeStorageField)
                .collect(Collectors.joining(" | "));
    }

    /**
     * Returns the fields that should be stored on disk for this task.
     *
     * @return Storage fields for this task in file order.
     */
    protected List<String> getStorageFields() {
        ArrayList<String> storageFields = new ArrayList<>();
        addStorageFields(storageFields, this.taskType.getDisplayCode(), this.isDone ? "1" : "0", this.description);
        return storageFields;
    }

    /**
     * Appends one or more fields to a task's storage representation.
     *
     * @param storageFields Fields accumulated for a task.
     * @param fields Fields to append in storage order.
     */
    protected static void addStorageFields(List<String> storageFields, String... fields) {
        storageFields.addAll(List.of(fields));
    }

    /**
     * Escapes storage separators so task details can be loaded back reliably.
     *
     * @param value Raw field value.
     * @return Escaped field value.
     */
    protected static String escapeStorageField(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\|");
    }

    /**
     * Returns the user-facing text for this task.
     *
     * @return Formatted task description with type and completion status.
     */
    @Override
    public String toString() {
        return "[" + this.taskType.getDisplayCode() + "][" + getStatusIcon() + "] " + this.description;
    }
}
