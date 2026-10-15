package luckynoslacky.luckytask;

import java.util.Objects;

/**
 * Provides an immutable, presentation-neutral view of one task.
 *
 * @param taskNumber one-based task number, or {@code null} for a confirmation
 * @param taskType concrete category of the task
 * @param isDone whether the task has been completed
 * @param description task description
 * @param taskTimes timing information for the task
 */
public record TaskView(
        Integer taskNumber,
        Task.TaskType taskType,
        boolean isDone,
        String description,
        TaskTimes taskTimes) {
    /** Validates the immutable task-view fields. */
    public TaskView {
        if (taskNumber != null && taskNumber <= 0) {
            throw new IllegalArgumentException("Task number must be positive.");
        }
        Objects.requireNonNull(taskType, "Task type cannot be null.");
        Objects.requireNonNull(description, "Task description cannot be null.");
        Objects.requireNonNull(taskTimes, "Task times cannot be null.");
    }

    /**
     * Creates an unnumbered view for a task-confirmation response.
     *
     * @param task task to represent
     * @return immutable unnumbered task view
     */
    public static TaskView fromTask(Task task) {
        return fromTask(null, task);
    }

    /**
     * Creates a view for an optionally numbered task.
     *
     * @param taskNumber one-based task number, or {@code null}
     * @param task task to represent
     * @return immutable task view
     */
    public static TaskView fromTask(Integer taskNumber, Task task) {
        Objects.requireNonNull(task, "Task cannot be null.");
        return new TaskView(
                taskNumber,
                task.getTaskType(),
                task.isDone(),
                task.getDescription(),
                task.getTaskTimes());
    }
}
