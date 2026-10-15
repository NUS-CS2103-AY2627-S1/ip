package luckynoslacky.luckytask;

import java.time.LocalDateTime;


/**
 * Represents a task without any date or time information.
 */
public class TodoTask extends Task {

    /**
     * Creates an incomplete ToDo task.
     *
     * @param description task description
     */
    public TodoTask(String description) {
        super(description);
    }

    /**
     * Returns this ToDo's concise command-line representation.
     *
     * @return typed ToDo task text
     */
    @Override
    public String toString() {
        return "[📌]" + super.toString();
    }

    /**
     * Returns the category of this task.
     *
     * @return ToDo task category
     */
    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }

    /**
     * Rejects snoozing because a ToDo has no ending time.
     *
     * @param amount duration by which to extend the ending time
     * @throws IllegalArgumentException always, because ToDos cannot be
     *                                  snoozed
     */
    @Override
    public void snoozeBy(DurationPeriod amount) {
        throw new IllegalArgumentException("ToDos cannot be snoozed.");
    }

    /**
     * Returns the absence of timing information for this ToDo.
     *
     * @return timing information without a start or end time
     */
    @Override
    public TaskTimes getTaskTimes() {
        return TaskTimes.none();
    }

    /**
     * Rejects schedule replacement because a ToDo has no timing information.
     *
     * @param newTimes replacement task timing information
     * @throws IllegalArgumentException always, because ToDos cannot be
     *                                  rescheduled
     */
    @Override
    public void reschedule(TaskTimes newTimes) {
        throw new IllegalArgumentException("ToDos cannot be rescheduled.");
    }

    /**
     * Rejects ending-time access because a ToDo has no ending time.
     *
     * @return never returns normally
     * @throws IllegalArgumentException always, because ToDos have no ending
     *                                  time
     */
    @Override
    public LocalDateTime getEndTime() {
        throw new IllegalArgumentException("Task has no ending time.");
    }

    /**
     * Rejects schedule-changing commands because a ToDo has no schedule.
     *
     * @throws TaskSchedulingException always, because ToDos cannot be
     *                                  scheduled
     */
    @Override
    void verifyCanBeScheduled() {
        throw createCannotScheduleException();
    }

    /**
     * Rejects snoozed timing creation because a ToDo has no ending time.
     *
     * @param amount duration by which to extend the ending time
     * @return never returns normally
     * @throws TaskSchedulingException always, because ToDos cannot be
     *                                  snoozed
     */
    @Override
    TaskTimes createSnoozedTimes(DurationPeriod amount) {
        throw createCannotScheduleException();
    }

    /**
     * Rejects replacement timing creation because a ToDo has no schedule.
     *
     * @param startTime requested start time
     * @param endTime requested ending time
     * @param currentTime current time used for validation
     * @return never returns normally
     * @throws TaskSchedulingException always, because ToDos cannot be
     *                                  rescheduled
     */
    @Override
    TaskTimes createRescheduledTimes(
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime currentTime) {
        throw createCannotScheduleException();
    }

    /** Creates the standard scheduling exception for a ToDo task. */
    private TaskSchedulingException createCannotScheduleException() {
        return new TaskSchedulingException(TaskSchedulingException.Reason.TODO_TASK);
    }

}
