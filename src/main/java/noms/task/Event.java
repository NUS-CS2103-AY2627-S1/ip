package noms.task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import noms.util.DateUtil;

/** Represents a task that takes place between two stated dates. */
public class Event extends Task {
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates an event with the given description and start and end dates.
     *
     * @param description the text describing the task
     * @param startDate the date the event starts
     * @param endDate the date the event ends
     */
    public Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        requireValidDateRange(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns whether the end date is strictly after the start date.
     *
     * @param startDate the proposed event start date
     * @param endDate the proposed event end date
     * @return {@code true} if the date range is valid
     */
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        return endDate.isAfter(startDate);
    }

    /** Returns true if this event spans (inclusively) the given date. */
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /** Returns this event's start date. */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** Returns this event's end date. */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Replaces this event's start date and moves its end date by the same
     * number of days, preserving the original duration.
     *
     * @param newStartDate the replacement start date
     */
    public void rescheduleFrom(LocalDate newStartDate) {
        long durationInDays = ChronoUnit.DAYS.between(startDate, endDate);
        startDate = newStartDate;
        endDate = newStartDate.plusDays(durationInDays);
    }

    /**
     * Replaces both dates of this event without changing its other details.
     *
     * @param newStartDate the replacement start date
     * @param newEndDate the replacement end date
     */
    public void reschedule(LocalDate newStartDate, LocalDate newEndDate) {
        requireValidDateRange(newStartDate, newEndDate);
        startDate = newStartDate;
        endDate = newEndDate;
    }

    @Override
    public boolean hasSameDetailsAs(Task other) {
        return super.hasSameDetailsAs(other)
                && startDate.equals(((Event) other).startDate)
                && endDate.equals(((Event) other).endDate);
    }

    /** Rejects a date range that cannot represent a positive-duration event. */
    private static void requireValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (!isValidDateRange(startDate, endDate)) {
            throw new IllegalArgumentException("Event end date must be after start date");
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateUtil.format(startDate)
                + " to: " + DateUtil.format(endDate) + ")";
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat()
                + " | " + escape(startDate.toString())
                + " | " + escape(endDate.toString());
    }
}
