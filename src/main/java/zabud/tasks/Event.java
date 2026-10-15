package zabud.tasks;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a task that occurs between a start and end time.
 */
public class Event extends Task {
    /**
     * Event start date, or {@code null} when the start is time-only.
     */
    private final LocalDate fromDate;
    /**
     * Event start time, or {@code null} when the start is date-only.
     */
    private final LocalTime fromTime;

    /**
     * Event end date, or {@code null} when the end is time-only.
     */
    private final LocalDate toDate;
    /**
     * Event end time, or {@code null} when the end is date-only.
     */
    private final LocalTime toTime;

    /**
     * Creates an event task.
     *
     * @param description the text describing the event.
     * @param fromDate event start date, or {@code null} when absent.
     * @param fromTime event start time, or {@code null} when absent.
     * @param toDate event end date, or {@code null} when absent.
     * @param toTime event end time, or {@code null} when absent.
     */
    public Event(String description, LocalDate fromDate, LocalTime fromTime, LocalDate toDate, LocalTime toTime) {
        super(description);
        this.fromDate = fromDate;
        this.fromTime = fromTime;
        this.toDate = toDate;
        this.toTime = toTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getStorageDetails() {
        return new String[] {toText(fromDate), toText(fromTime), toText(toDate), toText(toTime)};
    }

    /**
     * Returns the event start date.
     *
     * @return event start date, or {@code null} when absent.
     */
    public LocalDate getFromDate() {
        return fromDate;
    }
    /**
     * Returns the event start time.
     *
     * @return event start time, or {@code null} when absent.
     */
    public LocalTime getFromTime() {
        return fromTime;
    }

    /**
     * Returns the event end date.
     *
     * @return event end date, or {@code null} when absent.
     */
    public LocalDate getToDate() {
        return toDate;
    }
    /**
     * Returns the event end time.
     *
     * @return event end time, or {@code null} when absent.
     */
    public LocalTime getToTime() {
        return toTime;
    }

    /**
     * Returns the Duke code for event tasks.
     *
     * @return {@code "E"}.
     */
    @Override
    protected String getTaskType() {
        return "E";
    }

    /**
     * Returns the event's time range as list details.
     *
     * @return the event time range formatted for the task list.
     */
    @Override
    protected String getAdditionalDetails() {
        return " (from: " + combineDateAndTime(fromDate, fromTime)
                + " to: " + combineDateAndTime(toDate, toTime) + ")";
    }

    private static String toText(Object value) {
        return value == null ? "" : value.toString();
    }
    private static String combineDateAndTime(LocalDate date, LocalTime time) {
        return date == null ? toText(time) : time == null ? toText(date) : date + " " + time;
    }
}
