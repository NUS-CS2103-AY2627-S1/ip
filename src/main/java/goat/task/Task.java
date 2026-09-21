package goat.task;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A single item on the user's task list: a description plus
 * whether it has been completed.
 *
 * The fields are protected rather than private so that more specific
 * kinds of task can subclass this later and reuse them directly.
 */
public class Task {
    /**
     * What separates one field of a saved task from the next.
     *
     * Storage splits on this and every toFileFormat below joins with it, so
     * both sides of the encoding read the same constant rather than two copies
     * of the same literal that could drift apart.
     */
    public static final String FILE_SEPARATOR = " | ";

    /** How a completed task is written to the save file. */
    public static final String FILE_FLAG_DONE = "1";

    /** How a task that is not yet completed is written to the save file. */
    public static final String FILE_FLAG_NOT_DONE = "0";

    /** The task text exactly as the user typed it. */
    protected String description;

    /** Whether the user has marked this task as completed. */
    protected boolean isDone;

    /**
     * Creates a task that starts off not done, since a task
     * only becomes done once the user says so.
     *
     * @param description the task text as the user typed it
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task text as the user typed it, without the status box or
     * type letter that {@link #toString()} adds.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the moment this task is scheduled for, if it has one.
     *
     * A plain task has none, which is why the return is an Optional rather
     * than a nullable date: sorting has to decide where such a task goes, and
     * an empty Optional says "no time" in a way the caller cannot forget to
     * check. Subclasses that carry a time override this.
     *
     * @return the task's time, or empty if it has none
     */
    public Optional<LocalDateTime> getScheduledTime() {
        return Optional.empty();
    }

    /**
     * Reports whether another task describes the same thing as this one.
     *
     * Two tasks are the same thing when they are the same kind, worded the
     * same, and scheduled for the same moment. Whether either is done is not
     * part of it: a task already ticked off is still the same task, and
     * offering to add a second copy of it would be no more useful.
     *
     * Not equals(), deliberately. equals() carries expectations -- a matching
     * hashCode, use as a map key -- that nothing here needs, and a task's
     * identity really is "the same thing to do" rather than "the same object".
     *
     * @param other the task to compare against
     * @return true if both describe the same thing
     */
    public boolean hasSameDetailsAs(Task other) {
        return getClass() == other.getClass()
                && description.equals(other.description)
                && getScheduledTime().equals(other.getScheduledTime());
    }

    /**
     * Returns the symbol shown inside the status box.
     *
     * @return "X" if the task is done, or a single space if it is not
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed, undoing a previous mark. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Renders the task the way it should be written to the save file.
     * Each subclass prepends its own type letter, so the encoding of a task
     * lives with the class that knows what a task of that kind contains.
     *
     * The status is written as 1 or 0 rather than the "X" shown on screen,
     * so that the display wording can change without breaking saved files.
     *
     * @return the shared part of the saved line: "1 | description"
     */
    public String toFileFormat() {
        return (isDone ? FILE_FLAG_DONE : FILE_FLAG_NOT_DONE) + FILE_SEPARATOR + description;
    }

    /**
     * Renders the task the way it should appear to the user.
     * Overriding toString means a Task can be concatenated straight
     * into a message without the caller formatting it by hand.
     *
     * @return the task formatted as "[X] description" or "[ ] description"
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
