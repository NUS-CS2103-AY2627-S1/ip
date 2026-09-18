package lune.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A task that needs to be done before a specific date/time.
 */
public class Deadline extends Task {

    protected LocalDateTime by;

    /**
     * Creates a new, not-done deadline due at the given date/time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        // by always comes from Lune.parseDateTime(), which either returns a
        // real LocalDateTime or throws LuneException — it never returns
        // null — so every current caller already guarantees this.
        assert by != null : "a deadline's due date/time must never be null";
        this.by = by;
    }

    /**
     * Renders this deadline for display, e.g.
     * "[D][ ] return book (by: Oct 15 2019)".
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDateTime(by) + ")";
    }

    /**
     * Renders this deadline as one line of the on-disk save format, e.g.
     * "D | 0 | return book | 2019-10-15T00:00".
     */
    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + by;
    }

    /**
     * Returns whether this deadline's due date matches the given date
     * exactly.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }
}
