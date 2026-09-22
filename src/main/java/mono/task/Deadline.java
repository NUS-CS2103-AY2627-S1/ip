package mono.task;

import java.time.LocalDate;

import mono.parser.DateParser;

/**
 * A task that must be completed by a specific calendar date.
 */
public class Deadline extends Task {
    private final LocalDate date;

    /**
     * Creates a deadline from an ISO-8601 date string.
     *
     * @param name deadline description
     * @param dateText date in {@code yyyy-MM-dd} format
     * @throws java.time.format.DateTimeParseException if the date is invalid
     */
    public Deadline(String name, String dateText) {
        super(name);
        this.date = DateParser.parseDate(dateText);
    }

    /**
     * Returns the deadline's stable, pipe-delimited save-file representation.
     *
     * @return task type, completion status, name, and deadline
     */
    @Override
    public String toFileString() {
        return String.format("D | %d | %s | %s", super.isDone ? 1 : 0, super.name, this.date);
    }

    /**
     * Returns this deadline's user-facing representation.
     *
     * @return task type, completion status, name, and formatted deadline
     */
    @Override
    public String toString() {
        return String.format(
                "[D][%s] %s (by: %s)",
                super.isDone ? "X" : " ",
                super.name,
                DateParser.formatDate(this.date));
    }
}
