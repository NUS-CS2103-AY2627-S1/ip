package bob.task;

import java.time.temporal.TemporalAccessor;

import bob.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a specified date or date-time.
 */
public class Deadline extends Task {
    private final TemporalAccessor by;

    /**
     * Creates a deadline task and parses its deadline value.
     *
     * @param description The task description.
     * @param by Deadline in dd/MM/yyyy or dd/MM/yyyy HHmm format.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = DateTimeParser.parse(by);
    }

    /**
     * Returns the parsed deadline.
     *
     * @return Deadline stored as LocalDate or LocalDateTime.
     */
    public TemporalAccessor getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimeParser.formatForDisplay(by) + ")";
    }
}
