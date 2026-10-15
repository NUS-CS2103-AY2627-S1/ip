package grug.storage.serde;

import grug.task.DeadlineTask;
import grug.task.EventTask;
import grug.task.Task;
import grug.task.TodoTask;

/**
 * Contains static methods to serialize tasks into a human-readable format that
 * can be saved to disk.
 * <p>
 * Example:
 *
 * <pre>{@code
 * Task task = new TodoTask("read a book");
 * String serialized = TaskSerializer.serialize(task);
 * // save the result to a file...
 * }</pre>
 *
 * @see #serialize(Task) The general serialized format.
 */
public class TaskSerializer {
    /**
     * Serializes the {@link Task} instance into the following format:
     *
     * <pre>
     * [task-type] // e.g. todo, event, deadline
     * completed = 1 // 1 for completed, 0 for not completed
     * details = details here
     * task-specific-property-1 = value-1
     * task-specific-property-2 = value-2
     * // ...
     * </pre>
     *
     * @param task The task to serialize.
     */
    public static String serialize(Task task) {
        return switch (task) {
            case TodoTask todo -> serialize(todo);
            case DeadlineTask deadline -> serialize(deadline);
            case EventTask event -> serialize(event);
            default -> throw new UnsupportedOperationException(
                    "`%s` task is not supported".formatted(task.getClass().getName()));
        };
    }

    /**
     * Serializes the {@link TodoTask} instance into the following format:
     *
     * <pre>
     * [todo]
     * completed = 1 // 1 for completed, 0 for not completed
     * details = details here // newlines are converted into \n
     * </pre>
     *
     * @param task The task to serialize.
     * @return The serialized task.
     */
    public static String serialize(TodoTask task) {
        String completed = task.isCompleted() ? "1" : "0";
        String details = task.getDetails().replace("\n", "\\n");
        String priority = task.getPriority().getDisplayName();
        return """
                [todo]
                completed = %s
                details = %s
                priority = %s
                """.formatted(completed, details, priority);
    }

    /**
     * Serializes the {@link DeadlineTask} instance into the following format:
     *
     * <pre>
     * [deadline]
     * completed = 1 // 1 for completed, 0 for not completed
     * details = details here // newlines are converted into \n
     * by = deadline date/time
     * </pre>
     *
     * @param task The task to serialize.
     * @return The serialized task.
     */
    public static String serialize(DeadlineTask task) {
        String completed = task.isCompleted() ? "1" : "0";
        String details = task.getDetails().replace("\n", "\\n");
        String by = task.getDeadlineDatetime().format(DeadlineTask.DATE_TIME_INPUT_FORMATTER);
        String priority = task.getPriority().getDisplayName();
        return """
                [deadline]
                completed = %s
                details = %s
                by = %s
                priority = %s
                """.formatted(completed, details, by, priority);
    }

    /**
     * Serializes the {@link EventTask} instance into the following format:
     *
     * <pre>
     * [event]
     * completed = 1 // 1 for completed, 0 for not completed
     * details = details here // newlines are converted into \n
     * start = start date/time
     * end = end date/time
     * </pre>
     *
     * @param task The task to serialize.
     * @return The serialized task.
     */
    public static String serialize(EventTask task) {
        String completed = task.isCompleted() ? "1" : "0";
        String details = task.getDetails().replace("\n", "\\n");
        String start = task.getStartDatetime().format(EventTask.DATE_TIME_INPUT_FORMATTER);
        String end = task.getEndDatetime().format(EventTask.DATE_TIME_INPUT_FORMATTER);
        String priority = task.getPriority().getDisplayName();
        return """
                [event]
                completed = %s
                details = %s
                start = %s
                end = %s
                priority = %s
                """.formatted(completed, details, start, end, priority);
    }
}
