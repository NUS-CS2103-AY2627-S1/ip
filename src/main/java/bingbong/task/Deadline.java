package bingbong.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that must be completed by a specific deadline date.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    protected LocalDate by;

    /**
     * Constructs a Deadline instance checking input text patterns against datetime parsers.
     *
     * @param description The input text description summary details of the task item.
     * @param by          The target constraint time parameter string.
     * @throws DateTimeParseException If input fails to match both custom and standardised fallback formats.
     */
    public Deadline(String description, String by) throws DateTimeParseException {
        super(description);
        String trimmedBy = by.trim();
        try {
            this.by = LocalDate.parse(trimmedBy, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            this.by = LocalDate.parse(trimmedBy);
        }
    }

    /**
     * Retrieves the chronological calendar date bound of the milestone object instance.
     *
     * @return The target LocalDate value.
     */
    public LocalDate getDueDate() {
        return this.by;
    }

    /**
     * Returns a formatted text summary representation containing deadline status block tags.
     *
     * @return The formatted terminal view layout string.
     */
    @Override
    public String toString() {
        String formattedDate = this.by.format(DATE_FORMATTER);
        return "[D] [" + getStatusIcon() + "] " + this.description + " (by: " + formattedDate + ")";
    }

    /**
     * Translates object parameters into a file save storage record line pattern layout.
     *
     * @return A pipe-delimited database safe data line serialisation row.
     */
    @Override
    public String formatToFile() {
        return "D | " + getStatusBinary() + " | " + this.description + " | " + this.by;
    }
}
