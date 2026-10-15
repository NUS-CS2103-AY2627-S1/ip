package alzara.task;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * A task with a description and a done/not-done state.
 *
 * {@link ToDo}, {@link Deadline}, and {@link Event} are the only subtypes the
 * app actually creates; this class holds the state and formatting logic they
 * all share.
 */
public class Task {
    private String description;
    private boolean isDone;

    private Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Creates a task that starts out not done.
     *
     * @param description the task's description
     */
    public Task(String description) {
        this(description, false);
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns this task's save-file line.
     *
     * @return the save-file line, e.g. {@code "N | read book"}
     */
    public String toSaveFormat() {
        return (this.isDone ? "Y" : "N") + " | " + this.description;
    }

    /**
     * Returns true if this task's description contains every one of
     * {@code keywords}, ignoring case.
     *
     * @param keywords the keywords to search the description for
     * @return true if the description contains all of {@code keywords}
     */
    public boolean matches(String... keywords) {
        String descriptionChecker = this.description.toLowerCase();
        return Arrays.stream(keywords).allMatch(keyword -> descriptionChecker.contains(keyword.toLowerCase()));
    }

    /**
     * Returns true if this task is scheduled on {@code date}, for the
     * {@code view} command. The base implementation returns false, since a
     * plain {@link Task}/{@link ToDo} has no date of its own; {@link Deadline}
     * overrides this to match its due date exactly, {@link Event} to match
     * anywhere within its start-end range (inclusive).
     *
     * @param date the queried date
     * @return true if this task occurs on {@code date}
     */
    public boolean isScheduledOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the date {@link alzara.command.ViewCommand} sorts this task by,
     * once it has already matched {@link #isScheduledOn}.
     *
     * <p><b>Design note:</b> unlike {@link #matches}, there is no sane default
     * here - a plain {@link Task}/{@link ToDo} has no date concept at all, so
     * this base implementation always throws rather than pretending to
     * return one. This is a deliberate, known asymmetry with {@link #matches}
     * (kept as-is after review): {@link alzara.command.ViewCommand} only ever
     * calls this after filtering with {@link #isScheduledOn}, which guarantees
     * only {@link Deadline}/{@link Event} instances reach this method in
     * correct usage, so the throw is an intentional guard against future
     * misuse rather than a code path this feature can hit.
     *
     * @return the date this task is sorted by
     * @throws UnsupportedOperationException always, on the base {@link Task}
     */
    public LocalDate getSortDate() {
        throw new UnsupportedOperationException("Task has no date to sort by");
    }

    /**
     * Returns this task's console display.
     *
     * @return the display line, e.g. {@code "[X] read book"} once done
     */
    @Override
    public String toString() {
        if (this.isDone) {
            return "[X] " + this.description;
        }
        return "[ ] " + this.description;
    }

    /**
     * Returns true if {@code other} is a task of the same concrete type with
     * the same description, ignoring case and completion state. Used to
     * detect duplicate tasks when adding a new one; {@link Deadline} and
     * {@link Event} extend this with their own date fields.
     *
     * @param other the object to compare against
     * @return true if {@code other} represents the same task details
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Task task = (Task) other;
        return description.equalsIgnoreCase(task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), description.toLowerCase());
    }
}
