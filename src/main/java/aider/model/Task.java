package aider.model;

import java.util.Locale;

/**
 * Represents a task entered by the user.
 */
public class Task {
    /** The text describing the task. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    /** The type of task represented by this object. */
    private final TaskType type;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Creates an incomplete task with the given description and type.
     *
     * @param description the text describing the task
     * @param type the type of task represented by this object
     */
    protected Task(String description, TaskType type) {
        assert description != null : "Task description must not be null";
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the symbol used to display this task's status.
     *
     * @return {@code X} when done, or a space when not done
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the one-letter icon for this task type.
     *
     * @return the task type icon
     */
    public String getTypeIcon() {
        return type.getIcon();
    }

    /**
     * Returns the description and any type-specific details.
     *
     * @return the display details for this task
     */
    protected String getTaskDetails() {
        return description;
    }

    /**
     * Returns whether this task has schedule text matching the query.
     *
     * @param query the date or time text to search for
     * @return whether the task is scheduled on the requested text
     */
    public boolean isScheduledOn(String query) {
        String scheduleText = getScheduleText();
        return scheduleText != null
                && scheduleText.toLowerCase(Locale.ENGLISH)
                .contains(query.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the date or time text associated with this task.
     *
     * @return the schedule text, or {@code null} for an unscheduled task
     */
    protected String getScheduleText() {
        return null;
    }

    /**
     * Returns the task in the format used by the chatbot.
     *
     * @return the formatted task
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + getTaskDetails();
    }

    /**
     * Returns the task in the file storage format.
     *
     * @return the file storage representation
     */
    public String toFileString() {
        return getTypeIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }
}
