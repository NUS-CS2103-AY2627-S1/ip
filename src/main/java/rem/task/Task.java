package rem.task;

/**
 * Represents a task with a description, completion status, and optional note.
 */
public class Task {
    /** Maximum number of Unicode code points allowed in a note. */
    public static final int MAX_NOTE_LENGTH = 200;

    protected String description;
    protected boolean isDone;
    private String note;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.isBlank() : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
        this.note = null;
    }

    /**
     * Returns the icon used to display this task's completion status.
     *
     * @return {@code X} if the task is done, or a space otherwise.
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return True if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task has a note.
     *
     * @return True if a note is attached to this task.
     */
    public boolean hasNote() {
        return note != null;
    }

    /**
     * Returns this task's note, or {@code null} when it has none.
     *
     * @return Task note, or {@code null}.
     */
    public String getNote() {
        return note;
    }

    /**
     * Adds or replaces this task's note.
     *
     * @param note Valid, trimmed note text.
     */
    public void setNote(String note) {
        assert note != null : "Task note must not be null";
        assert !note.isBlank() : "Task note must not be blank";
        assert note.equals(note.strip()) : "Task note must be trimmed";
        assert !note.contains("\n") && !note.contains("\r")
                : "Task note must be a single line";
        assert note.codePointCount(0, note.length()) <= MAX_NOTE_LENGTH
                : "Task note must not exceed the maximum length";
        this.note = note;
    }

    /**
     * Removes this task's note.
     */
    public void deleteNote() {
        note = null;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns this task's completion status, description, and optional note.
     *
     * @return Display representation of this task.
     */
    @Override
    public String toString() {
        return formatWithNote(getTaskDetails());
    }

    /**
     * Returns the common completion status and description portion of a task display.
     *
     * @return Task details without its type, scheduling information, or note.
     */
    protected String getTaskDetails() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Appends this task's note to an otherwise complete task display.
     *
     * @param taskDisplay Complete task display without a note.
     * @return Task display with its note appended when present.
     */
    protected String formatWithNote(String taskDisplay) {
        return hasNote() ? taskDisplay + "\n  Note: " + note : taskDisplay;
    }
}
