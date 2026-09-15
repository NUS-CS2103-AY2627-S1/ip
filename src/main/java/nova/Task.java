package nova;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A single item on the user's list, with a description and a done status.
 * Task is abstract because every real task has a type — ToDo, Deadline or
 * Event — which decides how it is displayed and how it is written to disk.
 */
public abstract class Task {
    /** Display format used when the user gave a date but no time. */
    private static final DateTimeFormatter DATE_ONLY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    /** Display format used when the user gave a date and a time. */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    protected String taskName;
    protected boolean isMarked;

    /**
     * Creates a task with the given description and done status.
     *
     * @param taskName what the user typed as the description.
     * @param isMarked whether the task starts out done, true when reloading a
     *                 task that was already completed.
     */
    public Task(String taskName, boolean isMarked) {
        this.taskName = taskName;
        this.isMarked = isMarked;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isMarked = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isMarked = false;
    }

    /**
     * Returns the description the user gave for this task.
     *
     * @return the task description.
     */
    public String getTaskName() {
        return this.taskName;
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return true if the task is marked done.
     */
    public boolean isDone() {
        return this.isMarked;
    }

    protected String dataFields() {
        return (isMarked ? "1" : "0") + " | " + this.taskName;
    }

    /**
     * Formats a date-time for display.
     * A LocalDateTime always carries a time, so midnight is taken to mean
     * "the user gave a date only" and the time is left out of the output.
     *
     * @param dateTime the value to render.
     * @return e.g. "Oct 15 2019" or "Oct 15 2019, 6:00PM".
     */
    protected static String formatDateTime(LocalDateTime dateTime) {
        // Deadline and Event pass their own fields, which Nova null-checks
        // before it ever constructs the task. A null reaching here means a
        // task was built from a date that was never validated.
        assert dateTime != null : "formatDateTime() given a null date-time";

        boolean hasTime = !dateTime.toLocalTime().equals(LocalTime.MIDNIGHT);
        return dateTime.format(hasTime ? DATE_TIME_FORMAT : DATE_ONLY_FORMAT);
    }

    /**
     * Renders this task as one line of the data file.
     * Declared abstract so that each subclass supplies its own type letter and
     * extra fields, and the compiler catches any new subclass that forgets to.
     *
     * @return e.g. "D | 1 | return book | 2019-10-15T18:00".
     */
    public abstract String toDataString();

    /**
     * Renders this task for display, without its type tag.
     * Subclasses prefix their own tag, giving "[D][X] return book (by: ...)".
     *
     * @return e.g. "[X] return book".
     */
    @Override
    public String toString() {
        return (isMarked ? "[X]" : "[ ]") + " " + this.taskName;
    }
}
