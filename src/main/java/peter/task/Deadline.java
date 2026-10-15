package peter.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import peter.exception.PeterException;

/**
 * Represents a Deadline type of Task.
 * A deadline has a time besides the basic information of Task.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");
    private static final DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private final LocalDateTime by;

    /**
     * Constructs a Deadline object with the specified description.
     * The task is initially not completed.
     *
     * @param description The textual description of the task.
     * @param byStr Raw date time string in yyyy-MM-dd HHmm or yyyy-MM-dd format.
     * @throws PeterException If the date time format is invalid.
     */
    public Deadline(String description, String byStr) throws PeterException {
        super(description);
        this.by = parseDateTime(byStr.trim());
    }

    /**
     * Helper method to parse string into LocalDateTime.
     */
    private static LocalDateTime parseDateTime(String byStr) throws PeterException {
        try {
            if (byStr.contains(" ")) {
                return LocalDateTime.parse(byStr, INPUT_FORMATTER);
            } else {
                return java.time.LocalDate.parse(byStr, INPUT_DATE_ONLY_FORMATTER).atTime(23, 59);
            }
        } catch (DateTimeParseException e) {
            throw new PeterException(
                    "I couldn't chart that arrival time. Use yyyy-MM-dd HHmm, such as 2026-09-10 1800.");
        }
    }

    /**
     * Returns the LocalDateTime representation of the deadline.
     *
     * @return LocalDateTime of the deadline.
     */
    public LocalDateTime getBy() {
        return this.by;
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadline) other).by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(OUTPUT_FORMATTER) + ")";
    }

    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.format(FILE_FORMATTER);
    }
}
