package potato.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a deadline task with a description and a due date/time.
 */
public class Deadline extends Task {
    protected String rawDeadlineString;
    protected LocalDate parsedDeadlineDate;
    protected LocalDateTime parsedDeadlineDateTime;

    private static final DateTimeFormatter[] DATETIME_FORMATTERS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    };
    private static final DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter OUTPUT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy, HH:mm");

    /**
     * Constructs a {@code Deadline} task with a description and due date.
     *
     * @param description Description of the task.
     * @param rawDeadlineString Due date string, optionally in YYYY-MM-DD or YYYY-MM-DD HHmm format.
     */
    public Deadline(String description, String rawDeadlineString) {
        super(description);
        this.rawDeadlineString = rawDeadlineString;
        parseDateOrDateTime(rawDeadlineString);
    }

    private void parseDateOrDateTime(String input) {
        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                this.parsedDeadlineDateTime = LocalDateTime.parse(input, formatter);
                this.parsedDeadlineDate = null;
                return;
            } catch (DateTimeParseException ignored) {
                // Try next pattern
            }
        }
        try {
            this.parsedDeadlineDate = LocalDate.parse(input);
            this.parsedDeadlineDateTime = null;
        } catch (DateTimeParseException e) {
            this.parsedDeadlineDate = null;
            this.parsedDeadlineDateTime = null;
        }
    }

    /**
     * Returns the raw due date string.
     *
     * @return Raw deadline string.
     */
    public String getBy() {
        return rawDeadlineString;
    }

    /**
     * Returns the string representation of the deadline task.
     *
     * @return Formatted deadline task string.
     */
    @Override
    public String toString() {
        String formattedDisplayDate;
        if (parsedDeadlineDateTime != null) {
            formattedDisplayDate = parsedDeadlineDateTime.format(OUTPUT_DATETIME_FORMATTER);
        } else if (parsedDeadlineDate != null) {
            formattedDisplayDate = parsedDeadlineDate.format(OUTPUT_DATE_FORMATTER);
        } else {
            formattedDisplayDate = rawDeadlineString;
        }
        return "[D]" + super.toString() + " (by: " + formattedDisplayDate + ")";
    }

    /**
     * Returns the deadline task in the plain-text format used for file storage.
     *
     * @return Formatted save file line for this deadline.
     */
    public String toFileFormat() {
        String isDoneFlag = getStatusIcon().equals("X") ? "1" : "0";
        return "D | " + isDoneFlag + " | " + getDescription() + " | " + rawDeadlineString;
    }
}