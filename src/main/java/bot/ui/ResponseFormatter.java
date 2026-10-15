package bot.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import bot.task.Task;
import bot.task.TaskList;

/**
 * Formats chatbot results for display by console and graphical interfaces.
 */
public final class ResponseFormatter {
    private static final String NEW_LINE = "\n";
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private ResponseFormatter() {
    }

    /**
     * Formats the farewell response.
     *
     * @return farewell message
     */
    public static String formatGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Formats an error caused by invalid user input.
     *
     * @param message explanation of the error.
     * @return formatted error response
     */
    public static String formatError(String message) {
        return "OOPS!!! " + message;
    }

    /**
     * Formats an error encountered while loading saved tasks.
     *
     * @param message explanation from the storage layer.
     * @return formatted loading error response
     */
    public static String formatLoadingError(String message) {
        return "OOPS!!! I couldn't load your data file: " + message
                + ". I started with an empty list.";
    }

    /**
     * Formats an error encountered while saving tasks.
     *
     * @param message explanation from the storage layer.
     * @return formatted saving error response
     */
    public static String formatSavingError(String message) {
        return "OOPS!!! I couldn't save your tasks: " + message
                + ". Your latest changes may not be available next time.";
    }

    /**
     * Formats all tasks in their current order.
     *
     * @param tasks task list to display.
     * @return formatted task list
     */
    public static String formatTaskList(TaskList tasks) {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        appendTasks(response, tasks);
        return response.toString();
    }

    /**
     * Formats the full task list after it has been sorted chronologically.
     *
     * @param tasks sorted task list to display.
     * @return formatted sorted task list
     */
    public static String formatSortedTaskList(TaskList tasks) {
        StringBuilder response = new StringBuilder("Here are your tasks sorted chronologically:");
        appendTasks(response, tasks);
        return response.toString();
    }

    /**
     * Formats tasks that match a find command in their original order.
     *
     * @param matchingTasks matching tasks to display.
     * @return formatted matching-task list
     */
    public static String formatMatchingTasks(TaskList matchingTasks) {
        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        appendTasks(response, matchingTasks);
        if (matchingTasks.size() == 0) {
            response.append(NEW_LINE).append("No matching tasks found.");
        }
        return response.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     * @return formatted task-added response
     */
    public static String formatTaskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:" + NEW_LINE
                + "    " + task + NEW_LINE
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Formats confirmation that a task was marked as done.
     *
     * @param task task that was marked.
     * @return formatted task-marked response
     */
    public static String formatTaskMarked(Task task) {
        return "Nice! I've marked this task as done:" + NEW_LINE
                + "    " + task;
    }

    /**
     * Formats confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked.
     * @return formatted task-unmarked response
     */
    public static String formatTaskUnmarked(Task task) {
        return "OK, I've marked this task as not done yet:" + NEW_LINE
                + "    " + task;
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after deletion.
     * @return formatted task-deleted response
     */
    public static String formatTaskDeleted(Task task, int taskCount) {
        return "Noted. I've removed this task:" + NEW_LINE
                + "    " + task + NEW_LINE
                + "Now you have " + taskCount + " tasks in the list.";
    }

    /**
     * Formats deadlines and events that occur on a specified date.
     * Original task numbers are preserved for use with other commands.
     *
     * @param tasks task list to search and display.
     * @param date date to display tasks for.
     * @return formatted dated-task list
     */
    public static String formatTasksOnDate(TaskList tasks, LocalDate date) {
        StringBuilder response = new StringBuilder("Here are the deadlines and events on ")
                .append(date.format(DATE_DISPLAY_FORMAT)).append(":");
        boolean hasMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.getTask(i).occursOn(date)) {
                response.append(NEW_LINE).append(i + 1).append(".").append(tasks.getTask(i));
                hasMatch = true;
            }
        }
        if (!hasMatch) {
            response.append(NEW_LINE).append("No deadlines or events found.");
        }
        return response.toString();
    }

    /**
     * Appends tasks using one-based numbering.
     *
     * @param response response being constructed.
     * @param tasks tasks to append.
     */
    private static void appendTasks(StringBuilder response, TaskList tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            response.append(NEW_LINE).append(i + 1).append(".").append(tasks.getTask(i));
        }
    }
}
