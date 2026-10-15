package bern.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import bern.datetime.DateTime;

/**
 * Represents an event with a start and end, each of which may omit its time.
 */
public class Event extends Task implements IDateTimeComparable {
    private final DateTime startDateTime;
    private final DateTime endDateTime;

    /**
     * Creates an event with the specified date and time boundaries.
     *
     * @param name The name of the task.
     * @param startDateTime The date and optional time when the event starts.
     * @param endDateTime The date and optional time when the event ends.
     */
    Event(String name, DateTime startDateTime, DateTime endDateTime) {
        super(name);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Returns a symbol that represents the Event task type.
     *
     * @return The symbol representing the task type.
     */
    @Override
    protected String getSymbol() {
        return "E";
    }

    /**
     * Returns the event's start formatted for display and storage.
     *
     * @return The formatted start date and optional time.
     */
    public String getStartDateTimeString() {
        return startDateTime.toString();
    }

    /**
     * Returns the event's end formatted for display and storage.
     *
     * @return The formatted end date and optional time.
     */
    public String getEndDateTimeString() {
        return endDateTime.toString();
    }

    /**
     * Returns the event's start date and optional time.
     *
     * @return The start date and optional time.
     */
    public DateTime getStartDateTime() {
        return startDateTime;
    }

    /**
     * Returns the event's end date and optional time.
     *
     * @return The end date and optional time.
     */
    public DateTime getEndDateTime() {
        return endDateTime;
    }

    /**
     * Returns whether neither boundary specifies a time.
     *
     * @return Whether the event covers whole dates instead of a timed interval.
     */
    public boolean isAllDay() {
        return startDateTime.getTime() == null && endDateTime.getTime() == null;
    }

    /**
     * Returns whether the event ends after it starts, interpreting omitted times as day boundaries.
     * An omitted start time means midnight, while an omitted end time includes the entire end date.
     *
     * @return Whether the event has a positive duration.
     */
    public boolean hasPositiveDuration() {
        int dateOrder = startDateTime.compareDate(endDateTime);
        if (dateOrder != 0) {
            return dateOrder < 0;
        }

        LocalTime endTime = endDateTime.getTime();
        if (endTime == null) {
            return true;
        }
        LocalTime startTime = startDateTime.getTime();
        LocalTime effectiveStartTime = startTime == null ? LocalTime.MIDNIGHT : startTime;
        return endTime.isAfter(effectiveStartTime);
    }

    /**
     * Returns the last occupied date, excluding a timed midnight end.
     * Legacy stored events without positive durations remain visible on their start date.
     *
     * @return The final date on which the event should appear.
     */
    public LocalDate getLastOccupiedDate() {
        if (!hasPositiveDuration()) {
            return startDateTime.getDate();
        }
        LocalDate endDate = endDateTime.getDate();
        return LocalTime.MIDNIGHT.equals(endDateTime.getTime()) ? endDate.minusDays(1) : endDate;
    }

    /**
     * Returns whether the event occupies the specified calendar date.
     * A timed midnight end excludes its date; an omitted end time includes its entire date.
     * Events without positive durations occupy only their start date.
     *
     * @param date The date to check.
     * @return Whether the date is within the event's occupied dates.
     */
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDateTime.getDate()) && !date.isAfter(getLastOccupiedDate());
    }

    /**
     * Returns the task type, completion marker, and name followed by its start and end.
     *
     * @return A string representation of an Event.
     */
    @Override
    public String toString() {
        return String.format("%s (from: %s to: %s)", super.toString(), startDateTime, endDateTime);
    }

    /**
     * Returns the task fields followed by its start and end in save-file order.
     *
     * @return The fields used to save this event.
     */
    @Override
    public ArrayList<String> toDataList() {
        ArrayList<String> data = super.toDataList();
        data.add(startDateTime.toString());
        data.add(endDateTime.toString());
        return data;
    }

    @Override
    protected boolean isOnDay(DateTime dateTime) {
        return occursOn(dateTime.getDate());
    }

    /**
     * Returns the event's start as the reference used for chronological ordering.
     *
     * @return The start date and optional time.
     */
    @Override
    public DateTime getReferenceDateTime() {
        return startDateTime;
    }

    @Override
    public int compareDateTime(IDateTimeComparable timeComparable) {
        return getReferenceDateTime().compareTo(timeComparable.getReferenceDateTime());
    }
}
