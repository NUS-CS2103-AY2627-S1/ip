package jonk;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that takes place between a start and end date.
 */
public class Event extends Task {

    /** Formats dates for user-facing task descriptions. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Creates an event with the specified description and date range.
     *
     * @param description Description of the event.
     * @param startDate Start date of the event in {@code yyyy-MM-dd} format.
     * @param endDate End date of the event in {@code yyyy-MM-dd} format.
     */
    public Event(String description, String startDate, String endDate) {
        super(description);
        this.startDate = LocalDate.parse(startDate);
        this.endDate = LocalDate.parse(endDate);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + startDate.format(DISPLAY_DATE_FORMAT)
                + " to: " + endDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | " + encodeFileField(startDate.toString())
                + " | " + encodeFileField(endDate.toString());
    }
}
