package glennon.task;

import java.time.LocalDate;

import glennon.util.Text;

/**
 * Represents a mission tracked by Glennon and its completion status.
 * Subclasses supply the type marker shown to the user, so this class is
 * never instantiated on its own.
 */
public abstract class Task {
    /** User-provided description of the mission. */
    private final String description;

    /** Whether the mission has been completed. */
    private boolean isDone;

    /**
     * Creates a pending task with the given description.
     *
     * @param description description of the mission.
     * @throws IllegalArgumentException if the description is null, blank, or contains
     *         line breaks or control characters other than tabs.
     */
    protected Task(String description) {
        String normalizedDescription = description == null ? "" : Text.stripWhitespace(description);
        if (normalizedDescription.isEmpty()) {
            throw new IllegalArgumentException("Mission descriptions must not be blank.");
        }
        if (description.codePoints().anyMatch(Task::isInvalidDescriptionCharacter)) {
            throw new IllegalArgumentException(
                    "Mission descriptions must be on one line without control characters.");
        }
        this.description = normalizedDescription;
        this.isDone = false;
    }

    /**
     * Checks for characters that could break the single-line mission display.
     * Tabs remain valid within descriptions.
     *
     * @param character Unicode code point to check.
     * @return true when the character is forbidden in a description.
     */
    private static boolean isInvalidDescriptionCharacter(int character) {
        return character != '\t'
                && (Character.isISOControl(character)
                || Character.getType(character) == Character.LINE_SEPARATOR
                || Character.getType(character) == Character.PARAGRAPH_SEPARATOR);
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} when complete, or a space when pending.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description supplied for this task.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether another task has the same concrete type and case-sensitive
     * description, ignoring completion status. Scheduled task types also compare
     * their date-times.
     *
     * @param other task to compare with this task.
     * @return true when both tasks have the same defining details.
     */
    public boolean hasSameDetails(Task other) {
        return other != null
                && getClass() == other.getClass()
                && description.equals(other.description);
    }

    /**
     * Checks whether this task has been completed.
     *
     * @return true when the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as pending.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Checks whether this task is scheduled on a particular date. Tasks with
     * no date are not scheduled on any date by default.
     *
     * @param date date to check.
     * @return true when this task occurs on the given date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the task description prefixed by its status icon.
     *
     * @return display-ready task text.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
