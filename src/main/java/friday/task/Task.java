package friday.task;

/**
 * Represents a task stored by the Friday application.
 */
public class Task {
    private final String description;
    private final String type;
    private boolean isDone;

    /**
     * Creates a task with the specified description and type marker.
     *
     * @param description Description of the task.
     * @param type Marker identifying the task type.
     * @throws IllegalArgumentException If description is {@code null} or blank.
     */
    public Task(String description, String type) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /** Returns whether task details match, ignoring completion status. */
    public boolean hasSameDetails(Task other) {
        // The third field onward contains the description and any dates, without completion status.
        return other != null && type.equals(other.type)
                && toFileString().split(",", 3)[2].equals(other.toFileString().split(",", 3)[2]);
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    /** Returns whether this task's description contains the given keyword. */
    public boolean descriptionContains(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Search keyword cannot be null.");
        }
        return description.contains(keyword);
    }

    /**
     * Returns the display representation of this task.
     *
     * @return Type, completion status, and description of the task.
     */
    @Override
    public String toString() {
        return type + (isDone ? "[X] " : "[ ] ") + description;
    }

    /**
     * Returns the representation stored in the task data file.
     *
     * @return Escaped, comma-separated representation of the task.
     */
    public String toFileString() {
        return type + "," + (isDone ? "1" : "0")
                    + "," + escapeFileField(description);
    }

    /**
     * Escapes characters that would otherwise damage the line-based save format.
     *
     * @param value field to store
     * @return escaped field suitable for the save file
     */
    protected static String escapeFileField(String value) {
        return value.replace("\\", "\\\\")
                .replace(",", "\\,")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Returns a required task field after verifying that it is present and nonblank.
     *
     * @param fields Fields supplied to a task constructor.
     * @param index Index of the required field.
     * @param taskType Name of the task type used in the error message.
     * @param fieldName Name of the required field used in the error message.
     * @return Validated field value.
     * @throws IllegalArgumentException If the field is absent or blank.
     */
    protected static String requireField(String[] fields, int index, String taskType,
            String fieldName) {
        if (fields == null || fields.length <= index || fields[index] == null
                || fields[index].isBlank()) {
            throw new IllegalArgumentException(taskType + " " + fieldName + " cannot be empty.");
        }
        return fields[index];
    }
}
