package whimsybot.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Represents a task that must be finished by a specified time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private final LocalDate date;
    private final String legacyBy;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description the text describing the task
     * @param by the deadline value
     */
    public Deadline(String description, String by) {
        super(description);
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(by);
        } catch (DateTimeParseException e) {
            parsedDate = null;
        }
        this.date = parsedDate;
        this.legacyBy = parsedDate == null ? by : null;
        // A deadline uses exactly one representation: a parsed date or legacy text.
        assert (date == null) != (legacyBy == null) : "Deadline date representation is inconsistent";
    }

    /** Returns the deadline text for persistence. */
    public String getBy() {
        return date == null ? legacyBy : date.toString();
    }

    @Override
    public String toString() {
        String displayDate = date == null ? legacyBy : date.format(DISPLAY_FORMAT);
        return "[D]" + super.toString() + " (by: " + displayDate + ")";
    }
}
