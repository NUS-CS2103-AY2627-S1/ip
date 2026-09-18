package bot.task;

import java.time.LocalDate;

import bot.parser.TaskDateTime;

/**
 * Represents a task that needs to be done before a specific date/time.
 */
public class Deadline extends Task {

    /** The date (and optionally time) this task is due by. */
    protected TaskDateTime by;

    /**
     * Creates a new, not-done deadline with the given description and due date/time.
     *
     * @param description The deadline's description text.
     * @param by The date (and optionally time) the task is due by.
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        // by always comes from a successful TaskDateTime.parse() call
        // (in Parser.parseDeadline or Storage.parseLine), which either
        // returns a non-null value or throws - so by should never be
        // null here; toString/occursOn/toSaveFormat all dereference it.
        assert by != null : "a Deadline's due date/time must not be null";
        this.by = by;
    }

    /**
     * Returns this deadline's status icon, description, and due date/time,
     * prefixed with the {@code [D]} type icon.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }

    /**
     * Returns this deadline's data-file line, e.g.
     * {@code "D | 1 | return book | 2019-10-15"}.
     */
    @Override
    public String toSaveFormat() {
        return "D | " + (isDone() ? "1" : "0") + " | " + getDescription() + " | " + by.toSaveFormat();
    }

    /**
     * Returns whether the given date matches this deadline's due date exactly.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /** Returns this deadline's due date, used to order it for the {@code sort} command. */
    @Override
    protected LocalDate getSortDate() {
        return by.toLocalDate();
    }
}
