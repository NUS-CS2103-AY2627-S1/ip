package benji;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    protected String by;

    /**
     * Creates a deadline task with a description and deadline text.
     *
     * @param description task description
     * @param by deadline date or time
     */
    public Deadline(String description, String by) {
        super(description);
        assert by != null && !by.isBlank()
                : "Deadline should have a date or time";
        this.by = by;
    }

    /**
     * Formats ISO dates while preserving flexible deadline text.
     *
     * @param dateOrTime deadline date or time
     * @return formatted ISO date, or the original text
     */
    private String formatDateOrTime(String dateOrTime) {
        try {
            return LocalDate.parse(dateOrTime).format(FORMATTER);
        } catch (DateTimeParseException e) {
            return dateOrTime;
        }
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + formatDateOrTime(by) + ")";
    }
}
