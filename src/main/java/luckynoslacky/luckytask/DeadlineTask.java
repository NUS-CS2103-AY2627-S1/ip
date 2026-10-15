package luckynoslacky.luckytask;

import java.time.LocalDateTime;


/**
 * Represents a task that must be completed by a specified time.
 */
public class DeadlineTask extends Task {
    private TaskTimes times;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description task description
     * @param byTime deadline date and time
     */
    public DeadlineTask(String description, LocalDateTime byTime) {
        this(description, TaskTimes.makeDeadlineTimes(byTime));
    }

    /**
     * Creates an incomplete deadline task from validated timing information.
     *
     * @param description task description
     * @param times deadline timing information
     * @throws IllegalArgumentException if the timing information is not a
     *                                  deadline schedule
     */
    public DeadlineTask(String description, TaskTimes times) {
        super(description);

        TaskTimes.verifyDeadlineTimes(times);
        this.times = times;
    }

    /**
     * Returns this deadline's concise command-line representation.
     *
     * @return typed deadline task text with its ending time
     */
    @Override
    public String toString() {
        return "[⏳]" + super.toString()
                + " (by: " + formatDateTime(getEndTime()) + ")";
    }

    /**
     * Returns the category of this task.
     *
     * @return deadline task category
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns the current deadline.
     *
     * @return deadline date and time
     */
    public LocalDateTime getByTime() {
        return times.getEndTime();
    }

    /**
     * Returns the deadline through the common timed-task interface.
     *
     * @return deadline date and time
     */
    @Override
    public LocalDateTime getEndTime() {
        return times.getEndTime();
    }

    /**
     * Returns this deadline's timing information.
     *
     * @return deadline timing information
     */
    @Override
    public TaskTimes getTaskTimes() {
        return times;
    }

    /**
     * Replaces the deadline schedule.
     *
     * @param newTimes replacement deadline timing information
     * @throws IllegalArgumentException if the timing information is not a
     *                                  deadline schedule
     */
    @Override
    public void reschedule(TaskTimes newTimes) {
        TaskTimes.verifyDeadlineTimes(newTimes);
        times = TaskTimes.makeDeadlineTimes(newTimes.getEndTime());
    }

    /**
     * Accepts schedule-changing commands because a deadline has an ending
     * time.
     */
    @Override
    void verifyCanBeScheduled() {
        // Deadlines can always be scheduled.
    }

    /**
     * Creates the timing information after extending this deadline.
     *
     * @param amount duration by which to extend the deadline
     * @return deadline timing information with the extended ending time
     * @throws IllegalArgumentException if {@code amount} is null
     */
    @Override
    TaskTimes createSnoozedTimes(DurationPeriod amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Snooze amount cannot be null.");
        }
        return TaskTimes.makeDeadlineTimes(amount.addTo(getEndTime()));
    }

    /**
     * Creates validated deadline timing information.
     *
     * <p>Deadlines do not use {@code startTime}; it is accepted to preserve
     * the shared scheduling interface.
     *
     * @param startTime ignored requested start time
     * @param endTime requested deadline
     * @param currentTime current time used to reject past deadlines
     * @return validated deadline timing information
     * @throws TaskSchedulingException if the requested deadline is in the past
     */
    @Override
    TaskTimes createRescheduledTimes(
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime currentTime) {
        if (endTime.isBefore(currentTime)) {
            throw new TaskSchedulingException(
                    TaskSchedulingException.Reason.PAST_DEADLINE);
        }
        return TaskTimes.makeDeadlineTimes(endTime);
    }

}
