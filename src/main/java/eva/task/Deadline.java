package eva.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that must be completed by a date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    protected LocalDate by;

    /**
     * Creates a deadline with the specified description and date.
     *
     * @param description Description of the deadline.
     * @param by Deadline date in {@code yyyy-MM-dd} format.
     * @throws IllegalArgumentException If the date is not valid.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);

        try {
            this.by = LocalDate.parse(by);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "The date must use yyyy-MM-dd format.");
        }
    }

    public LocalDate getBy() {
        return by;
    }

    @Override
    public String toFileString() {
        return "D | " + getFileStatus() + " | " + description + " | " + by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + by.format(OUTPUT_DATE_FORMAT) + ")";
    }
}
