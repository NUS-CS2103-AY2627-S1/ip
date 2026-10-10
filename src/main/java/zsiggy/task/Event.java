package zsiggy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event with a start date and end date.
 */
public class Event extends Task {
    private final LocalDate fromDate;
    private final LocalDate toDate;

    /**
     * Creates an event.
     *
     * @param description The event description.
     * @param fromDate The start date in yyyy-MM-dd format.
     * @param toDate The end date in yyyy-MM-dd format.
     */
    public Event(String description, String fromDate, String toDate) {
        super(description);

        this.fromDate = LocalDate.parse(
                fromDate,
                DateTimeFormatter.ISO_LOCAL_DATE
        );

        this.toDate = LocalDate.parse(
                toDate,
                DateTimeFormatter.ISO_LOCAL_DATE
        );
    }

    /**
     * Returns the event start date in storage format.
     *
     * @return The event start date in yyyy-MM-dd format.
     */
    public String getFromDate() {
        return this.fromDate.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        );
    }

    /**
     * Returns the event end date in storage format.
     *
     * @return The event end date in yyyy-MM-dd format.
     */
    public String getToDate() {
        return this.toDate.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        );
    }

    /**
     * Returns the formatted textual representation of the event.
     *
     * @return The formatted event task.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: "
                + this.fromDate.format(
                DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH)
        )
                + " to: "
                + this.toDate.format(
                DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH)
        )
                + ")";
    }
}
