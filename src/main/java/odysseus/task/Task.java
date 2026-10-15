package odysseus.task;

import odysseus.OdysseusException;

import java.time.LocalDate;

/**
 * A task with a description and done status (the base type).
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a Task, initially marked as undone.
     *
     * @param description string representing description of task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of the task depending on whether it is done.
     *
     * @return {@code "X"} if the task is done, otherwise a single space
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /**
     * Marks task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks task as undone.
     */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the string of a task in format to be saved in.
     *
     * @return string of task to be written to file
     */
    public abstract String toSaveFormat();

    /**
     * Returns whether a task occurs on a given date.
     *
     * @param date LocalDate of the event to be checked against
     * @return whether task's deadline occurs on given date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether task description contains keyword.
     *
     * @param keyword string to check description against
     * @return whether string in task description
     */
    public boolean descriptionContains(String keyword) {
        return this.description.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns whether this task is a duplicate of another: it must be the same
     * concrete type and share the same defining fields. Done status is ignored.
     *
     * @param other the task to compare against
     * @return true if the two tasks represent the same task
     */
    public boolean isSameTask(Task other) {
        return other != null
                && this.getClass() == other.getClass()
                && this.description.equals(other.description);
    }

    /**
     * Updates one field of this task, identified by a flag. The base
     * implementation handles the shared {@code /desc} field; subclasses
     * override to handle their own fields.
     *
     * @param flag  the field to update (e.g. {@code /desc})
     * @param value the new value for that field
     * @throws OdysseusException if the flag is not applicable to this task type
     */
    public void update(String flag, String value) throws OdysseusException {
        if (flag.equals("/desc")) {
            this.description = value;
            return;
        }
        throw new OdysseusException("This task has no " + flag + " to change.");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s",
                this.getStatusIcon(),
                this.description);
    }
}
