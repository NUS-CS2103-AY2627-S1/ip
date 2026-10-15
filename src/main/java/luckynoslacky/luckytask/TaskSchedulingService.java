package luckynoslacky.luckytask;

import java.time.DateTimeException;
import java.time.LocalDateTime;

/**
 * Validates and creates scheduling information for tasks in a task list.
 */
class TaskSchedulingService {
    private final TaskList taskList;

    /**
     * Creates a scheduling service for the supplied task list.
     *
     * @param taskList task list whose schedules are validated
     */
    TaskSchedulingService(TaskList taskList) {
        if (taskList == null) {
            throw new IllegalArgumentException("Task list cannot be null.");
        }

        this.taskList = taskList;
    }

    /**
     * Returns the scheduling state needed to parse a timed-task command.
     *
     * @param taskNumber one-based number of the task
     * @return immutable task scheduling state
     */
    TaskSchedule getTaskSchedule(int taskNumber) {
        Task task = taskList.getTask(taskNumber);
        return new TaskSchedule(task.getTaskType(), task.getTaskTimes());
    }

    /**
     * Validates that a task can have its schedule modified.
     *
     * @param taskNumber one-based number of the task
     * @throws TaskSchedulingException if the task is a ToDo
     */
    void validateTimedTask(int taskNumber) {
        taskList.getTask(taskNumber).verifyCanBeScheduled();
    }

    /**
     * Validates the result of extending a task's ending time.
     *
     * @param taskNumber one-based number of the task
     * @param amount duration by which to extend the ending time
     * @param currentTime current date and time for deadline validation
     * @throws TaskSchedulingException if the task or resulting time is invalid
     */
    void validateSnoozeBy(
            int taskNumber,
            DurationPeriod amount,
            LocalDateTime currentTime) {
        if (amount == null || currentTime == null) {
            throw new IllegalArgumentException("Snooze validation values cannot be null.");
        }
        Task task = taskList.getTask(taskNumber);
        try {
            TaskTimes snoozedTimes = task.createSnoozedTimes(amount);
            task.createRescheduledTimes(
                    snoozedTimes.getStartTime(),
                    snoozedTimes.getEndTime(),
                    currentTime);
        } catch (DateTimeException exception) {
            throw new TaskSchedulingException(
                    TaskSchedulingException.Reason.TIME_OVERFLOW, exception);
        }
    }

    /**
     * Validates a replacement ending time for a deadline or event.
     *
     * @param taskNumber one-based number of the task
     * @param endTime replacement ending time
     * @param currentTime current date and time for deadline validation
     * @throws TaskSchedulingException if the task or replacement time is invalid
     */
    void validateSnoozeTo(
            int taskNumber,
            LocalDateTime endTime,
            LocalDateTime currentTime) {
        if (endTime == null || currentTime == null) {
            throw new IllegalArgumentException("Snooze validation values cannot be null.");
        }
        Task task = taskList.getTask(taskNumber);
        TaskTimes currentTimes = task.getTaskTimes();
        task.createRescheduledTimes(
                currentTimes.getStartTime(), endTime, currentTime);
    }

    /**
     * Creates validated replacement timing information for a task.
     *
     * @param taskNumber one-based number of the task
     * @param startTime replacement event start time, or {@code null} for a deadline
     * @param endTime replacement deadline or event end time
     * @param currentTime current date and time for deadline validation
     * @return validated timing information matching the task category
     * @throws TaskSchedulingException if the task or replacement schedule is invalid
     */
    TaskTimes createRescheduledTimes(
            int taskNumber,
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime currentTime) {
        if (endTime == null || currentTime == null) {
            throw new IllegalArgumentException("Rescheduling values cannot be null.");
        }
        return taskList.getTask(taskNumber).createRescheduledTimes(
                startTime, endTime, currentTime);
    }
}
