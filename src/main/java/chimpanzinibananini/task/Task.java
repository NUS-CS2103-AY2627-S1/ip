package chimpanzinibananini.task;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public boolean isDone() {
        return isDone;
    }

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
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns this task in the format used by the data file.
     */
    public String toDataString() {
        String doneValue = isDone ? "1" : "0";
        return getTypeIcon() + " | " + doneValue + " | " + encodeField(description);
    }

    /**
     * Returns the value with characters that have special meaning in the data file escaped.
     */
    protected static String encodeField(String value) {
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
