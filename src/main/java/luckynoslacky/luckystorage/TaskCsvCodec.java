package luckynoslacky.luckystorage;

import java.util.List;

import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskTimes;

/**
 * Converts task state into records used by the CSV storage format.
 */
final class TaskCsvCodec {
    private TaskCsvCodec() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Converts a task into fields in the CSV column order.
     *
     * @param task task to encode
     * @return CSV fields representing the task
     */
    static List<String> toRecord(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        TaskTimes taskTimes = task.getTaskTimes();
        return List.of(
                getTaskTypeMarker(task),
                task.isDone() ? "1" : "0",
                task.getDescription(),
                DateTimeStorageCodec.format(
                        taskTimes.hasStartTime() ? taskTimes.getStartTime() : null),
                DateTimeStorageCodec.format(
                        taskTimes.hasEndTime() ? taskTimes.getEndTime() : null));
    }

    /** Returns the persisted single-character type marker for a task. */
    private static String getTaskTypeMarker(Task task) {
        return switch (task.getTaskType()) {
            case TODO -> "T";
            case DEADLINE -> "D";
            case EVENT -> "E";
        };
    }
}
