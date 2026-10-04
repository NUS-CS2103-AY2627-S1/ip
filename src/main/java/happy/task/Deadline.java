package happy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a Deadline task that needs to be done before a specific date/time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter[] DATE_FORMATTERS = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("MMM dd yyyy"),
        DateTimeFormatter.ofPattern("MMM d yyyy")
    };
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter STORAGE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected String byRaw;
    protected LocalDate byDate;

    /**
     * Constructs a Deadline task with description and completion deadline.
     *
     * @param description Description of the task.
     * @param by Deadline date/time string.
     */
    public Deadline(String description, String by) {
        super(description);
        byRaw = by;
        byDate = parseDate(by);
    }

    /**
     * Attempts to parse a date string into a LocalDate object using standard formats.
     *
     * @param dateStr Raw date string input.
     * @return Parsed LocalDate if successful, or null if non-standard.
     */
    private static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Continue trying remaining date formatters
            }
        }
        return null;
    }

    @Override
    public boolean isOccurringOn(LocalDate date) {
        return byDate != null && byDate.equals(date);
    }

    @Override
    public String toFileFormat() {
        String dateString = (byDate != null)
                ? byDate.format(STORAGE_FORMATTER)
                : byRaw;
        return "D | " + super.toFileFormat() + " | " + dateString;
    }

    @Override
    public String toString() {
        String formattedDate = (byDate != null)
                ? byDate.format(OUTPUT_FORMATTER)
                : byRaw;
        return "[D]" + super.toString() + " (by: " + formattedDate + ")";
    }

    /**
     * Compares this Deadline task with another object for equality based on description and deadline.
     *
     * @param obj Target object to compare with.
     * @return true if target object is a Deadline with matching description and deadline.
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Deadline other = (Deadline) obj;
        return this.byRaw.equalsIgnoreCase(other.byRaw);
    }
}
