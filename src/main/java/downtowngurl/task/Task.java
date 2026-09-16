package downtowngurl.task;

import java.time.LocalDateTime;

/**
 * Represents one task tracked by the chatbot.
 */
public class Task {
    private static final String STORAGE_FIELD_SEPARATOR = " * ";

    private final String description;
    private final TaskType type;
    private boolean isDone;
    private RecurrenceFrequency recurrenceFrequency;

    /**
     * Creates a new task that has not been marked as done yet.
     *
     * @param description Details of the task.
     * @param type Category of the task.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
        this.recurrenceFrequency = null;
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
     * Returns the icon used to show whether this task is done.
     *
     * @return X if the task is done, or a blank space otherwise.
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the description entered by the user.
     *
     * @return Task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns whether this task has a recurrence frequency.
     *
     * @return true if this task repeats, false otherwise.
     */
    public boolean isRecurring() {
        return this.recurrenceFrequency != null;
    }

    /**
     * Returns how often this task repeats.
     *
     * @return Recurrence frequency, or null if the task does not repeat.
     */
    public RecurrenceFrequency getRecurrenceFrequency() {
        return this.recurrenceFrequency;
    }

    /**
     * Sets how often this task repeats.
     *
     * @param recurrenceFrequency Recurrence frequency to set.
     */
    public void setRecurrenceFrequency(RecurrenceFrequency recurrenceFrequency) {
        this.recurrenceFrequency = recurrenceFrequency;
    }

    /**
     * Removes this task's recurrence frequency.
     */
    public void clearRecurrence() {
        this.recurrenceFrequency = null;
    }

    /**
     * Returns the symbol used to identify this task type.
     *
     * @return Task type symbol.
     */
    protected String getTypeIcon() {
        return this.type.getIcon();
    }

    /**
     * Returns the date-time used when sorting this task in the task list.
     *
     * @return Date-time for dated tasks, or null for tasks without dates.
     */
    public LocalDateTime getSortDateTime() {
        return null;
    }

    /**
     * Returns a line of text that can be saved in the data file.
     *
     * @return Data file representation of this task.
     */
    public String toStorageString() {
        String status = this.isDone ? "Done" : "Not done";
        return this.type.getIcon() + STORAGE_FIELD_SEPARATOR + status + STORAGE_FIELD_SEPARATOR
                + escapeStorageField(this.description);
    }

    /**
     * Escapes special characters before writing a field to the data file.
     *
     * @param field Field to save.
     * @return Escaped field.
     */
    protected String escapeStorageField(String field) {
        return field.replace("\\", "\\\\")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("*", "\\*");
    }

    /**
     * Returns the user-facing text representation of this task.
     *
     * @return Task type, completion status, and description.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + this.description;
    }

    /**
     * Returns recurrence text to append to user-facing task descriptions.
     *
     * @return Recurrence display text, or an empty string if this task does not repeat.
     */
    protected String getRecurrenceDisplayText() {
        if (!isRecurring()) {
            return "";
        }
        return " (repeats " + this.recurrenceFrequency.getDisplayText() + ")";
    }

    /**
     * Returns recurrence text to append to storage lines.
     *
     * @return Recurrence storage text, or an empty string if this task does not repeat.
     */
    protected String getRecurrenceStorageText() {
        if (!isRecurring()) {
            return "";
        }
        return STORAGE_FIELD_SEPARATOR + this.recurrenceFrequency.name();
    }
}
