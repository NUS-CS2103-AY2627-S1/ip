package wodan.task;

import java.time.LocalDate;

/**
 * A task that starts and ends at given times.
 */
public class Event extends Task {
    /** When this event starts. */
    private TaskDateTime startAt;
    /** When this event ends. */
    private TaskDateTime endAt;

    /**
     * Creates an event with the given start and end date-times.
     *
     * @param description What the event is.
     * @param startAt When the event starts.
     * @param endAt When the event ends.
     */
    public Event(String description, TaskDateTime startAt, TaskDateTime endAt) {
        super(description);
        assert startAt != null && endAt != null : "Event needs a start and an end";
        assert isValidRange(startAt, endAt) : "Event end must be after start";
        this.startAt = startAt;
        this.endAt = endAt;
    }

    /**
     * Returns whether {@code endAt} is a valid end for an event that starts at {@code startAt}.
     * A date-only event may start and end on the same calendar day.
     * If a time of day is given, the end must be after the start.
     *
     * @param startAt Event start.
     * @param endAt Event end.
     * @return {@code true} if the range is allowed.
     */
    public static boolean isValidRange(TaskDateTime startAt, TaskDateTime endAt) {
        if (endAt.toLocalDateTime().isBefore(startAt.toLocalDateTime())) {
            return false;
        }
        if (endAt.toLocalDateTime().equals(startAt.toLocalDateTime())
                && (startAt.hasTime() || endAt.hasTime())) {
            return false;
        }
        return true;
    }

    /**
     * Returns this event as {@code [E][status] description (from: start to: end)}.
     *
     * @return Display text for list replies.
     */
    @Override
    public String toString() {
        return String.format("[E][%s] %s (from: %s to: %s)",
                getStatusIcon(), getDescription(),
                startAt.toDisplayString(), endAt.toDisplayString());
    }

    /**
     * Returns when this event starts.
     *
     * @return The start date or date-time.
     */
    public TaskDateTime getStartAt() {
        return this.startAt;
    }

    /**
     * Returns when this event ends.
     *
     * @return The end date or date-time.
     */
    public TaskDateTime getEndAt() {
        return this.endAt;
    }

    /**
     * Returns whether {@code other} is an event with the same description, start, and end.
     *
     * @param other Task to compare, which may be {@code null}.
     * @return {@code true} if both events would look like the same quest.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        if (!super.hasSameDetails(other)) {
            return false;
        }
        Event event = (Event) other;
        return startAt.hasSameValue(event.startAt) && endAt.hasSameValue(event.endAt);
    }

    /**
     * Returns {@code true} if {@code date} is between the start and end dates, inclusive.
     *
     * @param date Date to check.
     * @return Whether this event falls on {@code date}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate startDate = startAt.toLocalDate();
        LocalDate endDate = endAt.toLocalDate();
        boolean isOnOrAfterStart = !date.isBefore(startDate);
        boolean isOnOrBeforeEnd = !date.isAfter(endDate);
        return isOnOrAfterStart && isOnOrBeforeEnd;
    }

    /**
     * Returns this event as {@code E | 0|1 | description | start | end | category}.
     *
     * @return One save-file line.
     */
    @Override
    public String toStorageString() {
        return toStoragePrefix(STORAGE_TYPE_EVENT)
                + " | " + startAt.toStorageString() + " | " + endAt.toStorageString()
                + " | " + getCategory();
    }
}
