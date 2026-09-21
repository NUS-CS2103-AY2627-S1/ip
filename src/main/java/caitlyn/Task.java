package caitlyn;

import java.util.List;
import java.util.StringJoiner;

/**
 * Represents one task in Caitlyn's task list.
 */
public class Task {
    /** The text that describes what needs to be done. */
    private final String description;

    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates a new incomplete task.
     *
     * @param description the text describing the task.
     * @throws IllegalArgumentException if the description is null.
     */
    public Task(String description) {
        if (description == null) {
            throw new IllegalArgumentException("A task description cannot be null.");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete again.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the kind of this task.
     *
     * @return the task type.
     */
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }

    /**
     * Returns the task description together with any type-specific details.
     *
     * @return the text displayed after the task status.
     */
    protected String getTaskDetails() {
        return description;
    }

    /**
     * Returns this task in the format used by the task file.
     *
     * @return a pipe-separated representation of this task.
     */
    public String toStorageString() {
        String doneMarker = isDone ? "1" : "0";
        StringJoiner fields = new StringJoiner(" | ");
        fields.add(getTaskType().getMarker()).add(doneMarker);
        for (String field : getStorageFields()) {
            fields.add(escapeStorageField(field));
        }
        return fields.toString();
    }

    /**
     * Returns the raw task fields that follow its type and completion marker.
     * Subclasses add their own date or time fields.
     *
     * @return the task description and any type-specific storage fields.
     */
    protected List<String> getStorageFields() {
        return List.of(description);
    }

    /**
     * Escapes a task field so pipes, backslashes, and line breaks remain data rather than structure.
     *
     * @param field the raw field value.
     * @return the escaped field value.
     * @throws IllegalArgumentException if the field is null.
     */
    protected static String escapeStorageField(String field) {
        if (field == null) {
            throw new IllegalArgumentException("A task field cannot be null.");
        }
        return field.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    /**
     * Returns the description of this task.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} when the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task in the format used when displaying the task list.
     *
     * @return a status marker followed by the task description.
     */
    @Override
    public String toString() {
        String status = isDone ? "X" : " ";
        return "[" + getTaskType().getMarker() + "][" + status + "] " + getTaskDetails();
    }
}
