package luckynoslacky.luckytask;

import java.util.Objects;

/**
 * Describes the scheduling state of one task for a command operation.
 *
 * @param taskType concrete category of the task
 * @param taskTimes current timing information for the task
 */
public record TaskSchedule(Task.TaskType taskType, TaskTimes taskTimes) {
    /** Validates the scheduling-state fields. */
    public TaskSchedule {
        Objects.requireNonNull(taskType, "Task type cannot be null.");
        Objects.requireNonNull(taskTimes, "Task times cannot be null.");
    }
}
