package percy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A task that spans a period between two dates.
 */
public class Event extends Task {
    private LocalDate from;
    private LocalDate to;

    /**
     * Creates an event.
     *
     * @param description what the event is
     * @param from the start date
     * @param to the end date
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        assert from != null : "event start date should not be null";
        assert to != null : "event end date should not be null";
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start date.
     *
     * @return the date this event starts
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * Returns the end date.
     *
     * @return the date this event ends
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * Replaces the start date.
     *
     * @param from the new start date
     */
    public void setFrom(LocalDate from) {
        assert from != null : "event start date should not be null";
        this.from = from;
    }

    /**
     * Replaces the end date.
     *
     * @param to the new end date
     */
    public void setTo(LocalDate to) {
        assert to != null : "event end date should not be null";
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "[E]";
    }

    /**
     * {@inheritDoc}
     *
     * <p>Appends the date range in a friendly {@code MMM dd yyyy} format,
     * e.g. {@code "project (from: Oct 10 2019 to: Oct 12 2019)"}.
     */
    @Override
    public String getDescription() {
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("MMM dd yyyy");
        return super.getDescription()
                + " (from: " + from.format(outputFormat)
                + " to: " + to.format(outputFormat) + ")";
    }

    @Override
    public String getTypeLetter() {
        return "E";
    }

    /**
     * {@inheritDoc}
     *
     * <p>Appends both dates in ISO {@code yyyy-mm-dd} form so they can be
     * parsed back when loading.
     */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + FILE_SEPARATOR + from + FILE_SEPARATOR + to;
    }
}
