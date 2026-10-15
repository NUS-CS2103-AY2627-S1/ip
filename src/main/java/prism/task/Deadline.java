package prism.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import prism.PrismException;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("[d/M/uuuu HHmm][uuuu-MM-dd HHmm]")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    protected LocalDateTime by;

    /** Creates a deadline, accepting either supported date format. */
    public Deadline(String description, String by) throws PrismException {
        super(description, TaskType.DEADLINE);
        this.by = parseDateTime(by);
    }

    /** Parses a deadline date and applies the end-of-day time when only a date is supplied. */
    private LocalDateTime parseDateTime(String text) throws PrismException {
        try {
            String trimmedText = text.trim();
            if (trimmedText.length() == 10) {
                trimmedText += " 2359";
            }
            return LocalDateTime.parse(trimmedText, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new PrismException(
                    "!!! That is not a valid date or time. Please use 'd/M/yyyy HHmm' or "
                            + "'yyyy-MM-dd HHmm'.");
        }
    }

    /** Returns the deadline date and time. */
    public LocalDateTime getBy() {
        return this.by;
    }

    /** Reschedules this deadline to the supplied date and time. */
    public void reschedule(String newBy) throws PrismException {
        this.by = parseDateTime(newBy);
    }

    /** Returns the serialized deadline representation. */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + this.by.format(FILE_FORMATTER);
    }

    /** Returns the human-readable deadline representation. */
    @Override
    public String toString() {
        return super.toString() + " (by: " + this.by.format(OUTPUT_FORMATTER) + ")";
    }
}
