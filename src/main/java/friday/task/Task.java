package friday.task;

import java.time.LocalDateTime;
import java.util.Optional;

import friday.FridayException;

/**
 * Represents a task in Friday's task list.
 */
public class Task {
    protected String description;
    private boolean isDone;
    private RepeatFrequency repeatFrequency;

    /**
     * Creates a task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
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
     * Sets how often this task repeats.
     *
     * @param repeatFrequency repeat frequency to set
     */
    public void setRepeatFrequency(RepeatFrequency repeatFrequency) {
        assert repeatFrequency != null : "Repeat frequency should not be null";

        this.repeatFrequency = repeatFrequency;
    }

    /**
     * Returns the icon that shows whether this task is done.
     *
     * @return status icon of the task
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns the icon that shows this task's type.
     *
     * @return type icon of the task
     */
    public String getTaskTypeIcon() {
        return " ";
    }

    /**
     * Returns the date and time to use when sorting or reminding about this task.
     *
     * @return empty value for tasks without a date or time
     */
    public Optional<LocalDateTime> getReminderDateTime() {
        return Optional.empty();
    }

    /**
     * Returns this task in the format used by the save file.
     *
     * @return save-file representation of the task
     */
    public String toFileString() {
        return appendRepeatFrequency(getBaseFileString());
    }

    /**
     * Returns this task's common save-file fields.
     *
     * @return common save-file representation
     */
    protected String getBaseFileString() {
        return getTaskTypeIcon() + " | " + (this.isDone ? "1" : "0") + " | " + this.description;
    }

    /**
     * Adds recurring task information to a save-file line when needed.
     *
     * @param savedTask save-file line without recurrence information
     * @return save-file line with recurrence information
     */
    protected String appendRepeatFrequency(String savedTask) {
        if (repeatFrequency == null) {
            return savedTask;
        }
        return savedTask + " | repeat:" + repeatFrequency.getText();
    }

    /**
     * Returns whether this task's description contains the keyword.
     *
     * @param keyword keyword to find
     * @return true if the description contains the keyword
     */
    public boolean containsKeyword(String keyword) {
        return this.description.contains(keyword);
    }

    /**
     * Returns this task's description.
     *
     * @return task description
     */
    public String getDescription() {
        return description;
    }

    /** Updates the description without changing the task's dates or status. */
    public void setDescription(String description) {
        assert description != null && !description.isBlank() : "Task descriptions must be nonblank";
        this.description = description;
    }

    /** Returns whether the task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Creates the next occurrence, or returns empty for a non-recurring task.
     *
     * @return next incomplete occurrence
     * @throws FridayException if the next date cannot be represented
     */
    public Optional<Task> nextOccurrence() throws FridayException {
        if (repeatFrequency == null) {
            return Optional.empty();
        }
        Task next = createNextOccurrence(repeatFrequency);
        next.setRepeatFrequency(repeatFrequency);
        return Optional.of(next);
    }

    /** Removes recurrence from an occurrence after its successor has been created. */
    public void clearRepeatFrequency() {
        repeatFrequency = null;
    }

    /** Creates a dated successor for an undated recurring task. */
    protected Task createNextOccurrence(RepeatFrequency frequency) throws FridayException {
        return new Deadline(description, TaskDateTime.formatForFile(frequency.advance(LocalDateTime.now())));
    }

    /**
     * Returns this task as text for display to the user.
     *
     * @return user-facing representation of the task
     */
    @Override
    public String toString() {
        String taskText = "[" + getTaskTypeIcon() + "][" + getStatusIcon() + "] " + this.description;
        if (repeatFrequency == null) {
            return taskText;
        }
        return taskText + " (repeats: " + repeatFrequency.getText() + ")";
    }
}
