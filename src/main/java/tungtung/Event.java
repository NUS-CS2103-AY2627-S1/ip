package tungtung;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

/** Represents a task that occurs between two calendar dates. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    protected LocalDate from;
    protected LocalDate to;

    /**
     * Creates an event task.
     *
     * @param description event description
     * @param from event start date
     * @param to event end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = Objects.requireNonNull(from, "Event start date cannot be null.");
        this.to = Objects.requireNonNull(to, "Event end date cannot be null.");
        if (this.to.isBefore(this.from)) {
            throw new IllegalArgumentException("Event end date cannot be before its start date.");
        }
        assert !this.to.isBefore(this.from) : "An event must end on or after its start date.";
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from.format(DISPLAY_DATE_FORMAT)
                + " - to: " + this.to.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
