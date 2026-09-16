package eve.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A task with a description and a done/not-done status.
 * Base class for the specific task types ({@link ToDo}, {@link Deadline}, {@link Event}).
 */
public class Task {
    /** Shared format for displaying dates, e.g. "Dec 2 2019". */
    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    protected String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description, initially not done.
     *
     * @param description what the task is.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon shown in task listings.
     *
     * @return "X" if this task is done, or " " (a space) otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's display text: its status icon followed by its description.
     * Subclasses extend this with their own type icon and any extra details.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task's on-disk representation. Subclasses override this to
     * add their own type letter and any extra fields.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns whether this task occurs on the given date. Plain tasks (to-dos)
     * have no date, so this is false unless a subclass overrides it.
     *
     * @param date the date to check against.
     * @return true if this task occurs on that date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the given keyword
     * (case-insensitive).
     *
     * @param keyword the text to search for.
     * @return true if the description contains the keyword.
     */
    public boolean matches(String keyword) {
        return description.toLowerCase(Locale.ENGLISH).contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the date this task should be sorted by in a chronological
     * schedule view. Plain tasks (to-dos) have no date, so this is empty
     * unless a subclass overrides it.
     *
     * @return the task's schedule date, or empty if it has none.
     */
    public Optional<LocalDate> getScheduleDate() {
        return Optional.empty();
    }

    /**
     * Returns whether this task has the same type and description as another --
     * used to catch adding an exact duplicate of a task already on the list.
     * Deliberately ignores done/not-done status, since marking a task doesn't
     * make it a different task. Subclasses that add their own fields (the
     * dates on {@link Deadline}/{@link Event}) override this to also compare
     * those.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Task)) {
            return false;
        }
        Task task = (Task) other;
        return getClass() == task.getClass() && description.equals(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), description);
    }
}
