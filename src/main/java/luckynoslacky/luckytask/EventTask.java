package luckynoslacky.luckytask;

import java.time.LocalDateTime;


/**
 * Represents a task with a specified start and end time.
 */
public class EventTask extends Task {
    private TaskTimes times;

    /**
     * Creates an incomplete event task.
     *
     * @param description task description
     * @param startTime event start date and time
     * @param endTime event end date and time
     */
    public EventTask(String description, LocalDateTime startTime, LocalDateTime endTime) {
        this(description, TaskTimes.makeEventTimes(startTime, endTime));
    }

    /**
     * Creates an incomplete event task from validated timing information.
     *
     * @param description task description
     * @param times event timing information
     * @throws IllegalArgumentException if the timing information is not an
     *                                  event schedule
     */
    public EventTask(String description, TaskTimes times) {
        super(description);

        TaskTimes.verifyEventTimes(times);
        this.times = TaskTimes.makeEventTimes(
                times.getStartTime(), times.getEndTime());
    }

    /**
     * Returns this event's concise command-line representation.
     *
     * @return typed event task text with its start and ending times
     */
    @Override
    public String toString() {
        return "[📆]" + super.toString()
                + " (from: " + formatDateTime(getStartTime())
                + " to: " + formatDateTime(getEndTime()) + ")";
    }

    /**
     * Returns the category of this task.
     *
     * @return event task category
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.EVENT;
    }

    /**
     * Returns the current event start time.
     *
     * @return event start date and time
     */
    public LocalDateTime getStartTime() {
        return times.getStartTime();
    }

    /**
     * Returns the current event end time.
     *
     * @return event end date and time
     */
    public LocalDateTime getEndTime() {
        return times.getEndTime();
    }

    /**
     * Returns this event's timing information.
     *
     * @return event timing information
     */
    @Override
    public TaskTimes getTaskTimes() {
        return times;
    }

    /**
     * Replaces both event times after validating the supplied schedule.
     *
     * @param newTimes replacement event timing information
     * @throws IllegalArgumentException if the timing information is not an
     *                                  event schedule
     */
    @Override
    public void reschedule(TaskTimes newTimes) {
        TaskTimes.verifyEventTimes(newTimes);
        times = TaskTimes.makeEventTimes(
                newTimes.getStartTime(), newTimes.getEndTime());
    }

    /**
     * Accepts schedule-changing commands because an event has start and end
     * times.
     */
    @Override
    void verifyCanBeScheduled() {
        // Events can always be scheduled.
    }

    /**
     * Creates the timing information after extending this event's end time.
     *
     * @param amount duration by which to extend the event
     * @return event timing information with the extended ending time
     * @throws IllegalArgumentException if {@code amount} is null
     */
    @Override
    TaskTimes createSnoozedTimes(DurationPeriod amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Snooze amount cannot be null.");
        }
        return TaskTimes.makeEventTimes(
                getStartTime(), amount.addTo(getEndTime()));
    }

    /**
     * Creates validated event timing information.
     *
     * <p>Events do not use {@code currentTime}; past event start times remain
     * valid.
     *
     * @param startTime requested event start time
     * @param endTime requested event end time
     * @param currentTime unused current time supplied by the shared interface
     * @return validated event timing information
     * @throws IllegalArgumentException if {@code startTime} is null
     * @throws TaskSchedulingException if the end time is before the start time
     */
    @Override
    TaskTimes createRescheduledTimes(
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime currentTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Event start time cannot be null.");
        }
        if (endTime.isBefore(startTime)) {
            throw new TaskSchedulingException(
                    TaskSchedulingException.Reason.END_BEFORE_START);
        }
        return TaskTimes.makeEventTimes(startTime, endTime);
    }

}
