package rocky.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import rocky.RockyException;

/** A task with a start and end time, e.g. "meeting (from: Dec 02 2019 to: Dec 03 2019)". */
public class Event extends Task {
    /** How the start/end dates are shown to the user, e.g. "Oct 15 2019". */
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates an event, initially not marked done. Both dates are expected
     * in ISO format (yyyy-mm-dd, e.g. 2019-10-15); anything else raises a
     * RockyException.
     *
     * @param description what the task is, e.g. "project meeting".
     * @param startDate the event's start date, in ISO format.
     * @param endDate the event's end date, in ISO format.
     * @throws RockyException if either date isn't in ISO format.
     */
    public Event(String description, String startDate, String endDate) throws RockyException {
        super(description);
        try {
            this.startDate = LocalDate.parse(startDate);
            this.endDate = LocalDate.parse(endDate);
        } catch (DateTimeParseException e) {
            throw new RockyException(
                    "Please give event dates as yyyy-mm-dd, e.g. 2019-10-15.");
        }
    }

    /**
     * Returns the start date in ISO format (yyyy-mm-dd), used when saving to disk.
     *
     * @return the start date as an ISO-8601 string, e.g. "2019-12-02".
     */
    public String getStartDate() {
        return this.startDate.toString();
    }

    /**
     * Returns the end date in ISO format (yyyy-mm-dd), used when saving to disk.
     *
     * @return the end date as an ISO-8601 string, e.g. "2019-12-03".
     */
    public String getEndDate() {
        return this.endDate.toString();
    }

    /** Returns the "[E]" type icon, the checkbox/description, and the formatted date range. */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.startDate.format(OUTPUT_FORMAT)
                + " to: " + this.endDate.format(OUTPUT_FORMAT) + ")";
    }
}
