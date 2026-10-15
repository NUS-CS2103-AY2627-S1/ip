package pan;

import java.time.LocalDateTime;

/**
 * Represents a deadline: a task that must be done by a given date and time.
 */
public class Deadline extends Task {

    /**
     * The moment the task is due. Stored as a real {@link LocalDateTime}
     * (date + time, no timezone) rather than free text, so the chatbot can
     * reformat it and, later, reason about it.
     */
    private LocalDateTime by;

    /** Creates a deadline: a task with the given description, due at {@code by}. */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null
                : "by should never be null: Parser.parseDateTime()/parseStoredDateTime() "
                + "always return a value or throw, never null";
        this.by = by;
    }

    @Override
    public void applyUpdate(String option, String value) throws PanException {
        if (option.equals("by")) {
            by = Parser.parseDateTime(value);
            return;
        }
        super.applyUpdate(option, value);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + Parser.formatDateTime(by) + ")";
    }

    /**
     * Returns the save-file line for a deadline, e.g.
     * {@code "D | 0 | return book | 2019-10-15T18:00"}. The date is written in
     * ISO-8601 form ({@link LocalDateTime#toString()}) so it reloads exactly
     * via {@link Parser#parseStoredDateTime(String)}, independent of the
     * display format.
     */
    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + by;
    }
}
