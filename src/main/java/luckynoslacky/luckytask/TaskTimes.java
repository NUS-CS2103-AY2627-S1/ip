package luckynoslacky.luckytask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents the absolute timing information associated with a task.
 *
 * <p>A ToDo has no times, a deadline has only an end time, and an event has
 * both a start and an end time. Use the named factory methods to create a
 * valid instance.
 */
public final class TaskTimes {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private TaskTimes(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates timing information for a ToDo task.
     *
     * @return timing information without a start or end time
     */
    public static TaskTimes none() {
        return new TaskTimes(null, null);
    }

    /**
     * Creates timing information for a deadline task.
     *
     * @param endTime deadline date and time
     * @return timing information containing only an end time
     * @throws IllegalArgumentException if {@code endTime} is null
     */
    public static TaskTimes makeDeadlineTimes(LocalDateTime endTime) {
        if (endTime == null) {
            throw new IllegalArgumentException("Deadline cannot be empty.");
        }
        return new TaskTimes(null, endTime);
    }

    /**
     * Creates timing information for an event task.
     *
     * @param startTime event start date and time
     * @param endTime event end date and time
     * @return timing information containing both event times
     * @throws IllegalArgumentException if either time is null or the end is
     *                                  before the start
     */
    public static TaskTimes makeEventTimes(
            LocalDateTime startTime,
            LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Event times cannot be empty.");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException(
                    "Event end cannot be before its start.");
        }
        return new TaskTimes(startTime, endTime);
    }

    /**
     * Verifies that the supplied timing information represents a deadline.
     *
     * @param times timing information to verify
     * @throws IllegalArgumentException if the timing is not deadline-shaped
     */
    static void verifyDeadlineTimes(TaskTimes times) {
        if (times == null || times.hasStartTime() || !times.hasEndTime()) {
            throw new IllegalArgumentException("Invalid deadline times.");
        }
    }

    /**
     * Verifies that the supplied timing information represents an event.
     *
     * @param times timing information to verify
     * @throws IllegalArgumentException if the timing is not event-shaped
     */
    static void verifyEventTimes(TaskTimes times) {
        if (times == null
                || !times.hasStartTime()
                || !times.hasEndTime()) {
            throw new IllegalArgumentException("Invalid event times.");
        }
    }

    /**
     * Returns whether this timing information contains a start time.
     *
     * @return true for event timing information
     */
    public boolean hasStartTime() {
        return startTime != null;
    }

    /**
     * Returns whether this timing information contains an end time.
     *
     * @return true for deadline and event timing information
     */
    public boolean hasEndTime() {
        return endTime != null;
    }

    /**
     * Returns the stored start time, or null when it is not applicable.
     *
     * @return start time, or null for ToDos and deadlines
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the stored end time, or null when it is not applicable.
     *
     * @return end time, or null for ToDos
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Checks whether this timing information occurs on the supplied date.
     *
     * <p>A deadline occurs only on its end date. An event occurs on every date
     * from its start date through its end date, inclusive. ToDo timing does
     * not occur on any date.
     *
     * @param date date to check
     * @return true if this timing occurs on the supplied date
     * @throws IllegalArgumentException if {@code date} is null
     */
    public boolean occursOn(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Search date cannot be null.");
        }

        if (!hasEndTime()) {
            return false;
        }

        LocalDate endDate = endTime.toLocalDate();
        if (!hasStartTime()) {
            return endDate.equals(date);
        }

        LocalDate startDate = startTime.toLocalDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Creates a schedule with a replacement end time while preserving the
     * existing schedule shape.
     *
     * @param replacementEndTime replacement end time
     * @return deadline or event timing information with the new end time
     * @throws IllegalArgumentException if this object has no end time or the
     *                                  replacement is invalid
     */
    public TaskTimes withEndTime(LocalDateTime replacementEndTime) {
        if (!hasEndTime()) {
            throw new IllegalArgumentException("Task has no ending time.");
        }
        return hasStartTime()
                ? makeEventTimes(startTime, replacementEndTime)
                : makeDeadlineTimes(replacementEndTime);
    }

    /**
     * Compares this timing information with another timing value.
     *
     * @param other object to compare with
     * @return true if both timing values contain the same times
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TaskTimes)) {
            return false;
        }
        TaskTimes otherTimes = (TaskTimes) other;
        return Objects.equals(startTime, otherTimes.startTime)
                && Objects.equals(endTime, otherTimes.endTime);
    }

    /**
     * Returns a hash code based on both stored times.
     *
     * @return hash code for this timing information
     */
    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    /**
     * Returns a readable representation of the stored times.
     *
     * @return textual timing representation
     */
    @Override
    public String toString() {
        return "TaskTimes{"
                + "startTime=" + startTime
                + ", endTime=" + endTime
                + '}';
    }
}
