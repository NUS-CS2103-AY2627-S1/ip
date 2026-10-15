package luckynoslacky.luckyresponse;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import luckynoslacky.ResponseContent;
import luckynoslacky.TaskContent;
import luckynoslacky.TextContent;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskList;
import luckynoslacky.luckytask.TaskView;

/**
 * Creates structured user responses that present task data.
 */
public final class LuckyNoTaskResponses {
    private static final DateTimeFormatter FIND_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    private LuckyNoTaskResponses() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Creates the response shown after a task is added.
     *
     * @param task task that was added
     * @param taskCount number of tasks after adding the task
     * @return task-added response content
     */
    public static TaskContent added(Task task, int taskCount) {
        String leadingLine = "Got one more thing to remember ah: ";
        String trailingLine = "Now you got " + taskCount + " tasks to settle.";
        return createTaskResponse(leadingLine, task, trailingLine);
    }

    /**
     * Creates the response shown after a task is marked as done.
     *
     * @param task task marked as done
     * @return task-marked response content
     */
    public static TaskContent marked(Task task) {
        return createTaskResponse(
                "Swee lah you're done with this task:", task, null);
    }

    /**
     * Creates the response shown after a task is marked as not done.
     *
     * @param task task marked as not done
     * @return task-unmarked response content
     */
    public static TaskContent unmarked(Task task) {
        return createTaskResponse(
                "Eh salah you're not done with this task ah, "
                        + "must remember to do ah!",
                task,
                null);
    }

    /**
     * Creates the response shown after a task is deleted.
     *
     * @param task task that was deleted
     * @param taskCount number of remaining tasks
     * @return task-deleted response content
     */
    public static TaskContent deleted(Task task, int taskCount) {
        String leadingLine = "Solid man can don't care about this one already:";
        String trailingLine = "But you still got " + taskCount + " tasks to settle.";
        return createTaskResponse(leadingLine, task, trailingLine);
    }

    /**
     * Creates the response shown after a task is snoozed.
     *
     * @param task updated task
     * @return task-snoozed response content
     */
    public static TaskContent snoozed(Task task) {
        return createTaskResponse(
                "Nah here's your snooze you lazy bum, don't slack too much hor!",
                task,
                null);
    }

    /**
     * Creates the response shown after a task is rescheduled.
     *
     * @param task updated task
     * @return task-rescheduled response content
     */
    public static TaskContent rescheduled(Task task) {
        return createTaskResponse(
                "Nah here's your resched you lazy bum, don't slack too much hor!",
                task,
                null);
    }

    /**
     * Creates the response shown when tasks are listed or found.
     *
     * @param taskList indexed task list to present
     * @return text content for an empty list, or structured task content
     */
    public static ResponseContent listed(TaskList taskList) {
        if (taskList == null) {
            throw new IllegalArgumentException("Task list cannot be null.");
        }
        if (taskList.isEmpty()) {
            return new TextContent(emptyTaskListMessage());
        }

        String header = getListHeader(taskList);
        List<TaskView> taskViews = taskList.getTaskViews();
        String message = String.join("\n", header, taskList.toDisplayString());
        return new TaskContent(message, List.of(header), taskViews, List.of());
    }

    /**
     * Returns the message shown for an empty task list.
     *
     * @return empty-list message
     */
    public static String emptyTaskListMessage() {
        return "Chill lah bro got nothing yet lah!";
    }

    /** Creates a one-task response with optional trailing prose. */
    private static TaskContent createTaskResponse(
            String leadingLine,
            Task task,
            String trailingLine) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        List<String> trailingLines = trailingLine == null
                ? List.of()
                : List.of(trailingLine);
        TaskView taskView = TaskView.fromTask(task);
        String message = String.join("\n", leadingLine,
                "  " + task);
        if (trailingLine != null) {
            message += "\n" + trailingLine;
        }
        return new TaskContent(
                message, List.of(leadingLine), List.of(taskView), trailingLines);
    }

    /** Returns the prose heading for a non-empty task-list response. */
    private static String getListHeader(TaskList taskList) {
        return taskList.getSearchDate()
                .map(date -> "Nah, all these things you need to do on: "
                        + FIND_DATE_FORMAT.format(date))
                .orElse("Nah, all these things you need to do:");
    }
}
