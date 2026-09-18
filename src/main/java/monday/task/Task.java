package monday.task;

/**
 * Represents a task managed by Monday.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private final String notes;

    /**
     * Creates an incomplete task with the given description.
     */
    public Task(String description) {
        this(description, false, "");
    }

    /**
     * Creates an incomplete task with the given description and notes.
     */
    public Task(String description, String notes) {
        this(description, false, notes);
    }

    /**
     * Creates a task with the given description and completion status.
     */
    public Task(String description, boolean isDone) {
        this(description, isDone, "");
    }

    /**
     * Creates a task with the given description, completion status, and notes.
     */
    public Task(String description, boolean isDone, String notes) {
        this.description = description;
        this.isDone = isDone;
        this.notes = notes;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getNotes() {
        return notes;
    }

    /**
     * Returns an 'X' or " " if the task is completed or not completed, respectively.
     *
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks a task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks a task as no completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return formatTask() + formatNotes();
    }

    /**
     * Formats the completion status and description shared by all task types.
     */
    protected String formatTask() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Formats notes as an optional suffix for display.
     */
    protected String formatNotes() {
        return notes.isEmpty() ? "" : " " + notes;
    }
}
