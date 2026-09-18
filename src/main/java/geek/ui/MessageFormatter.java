package geek.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import geek.task.Task;

/**
 * Creates user-facing response text shared by the console and graphical UIs.
 */
public final class MessageFormatter {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "MMM d yyyy",
                    Locale.ENGLISH
            );

    private MessageFormatter() {
    }

    /**
     * Returns the application welcome message.
     *
     * @return Welcome message.
     */
    public static String welcome() {
        return "Systems online. I'm Geek, your calm mission control.\n"
                + "What are we tackling next?";
    }

    /**
     * Returns the application goodbye message.
     *
     * @return Goodbye message.
     */
    public static String goodbye() {
        return "Mission paused. Powering down for now—"
                + "see you next time!";
    }

    /**
     * Formats the complete task list.
     *
     * @param tasks Tasks to show.
     * @return Task-list response.
     */
    public static String taskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your mission board is clear.";
        }

        return "Here's what's on your mission board:\n"
                + formatTasks(tasks);
    }

    /**
     * Formats confirmation that tasks were sorted chronologically.
     *
     * @param tasks Tasks in their new display order.
     * @return Sort confirmation and the sorted task list.
     */
    public static String tasksSorted(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your mission board is clear. Nothing to align.";
        }

        return "Timeline aligned. Dated tasks come first, "
                + "with undated tasks after them:\n"
                + formatTasks(tasks);
    }

    /**
     * Formats tasks matching a search keyword.
     *
     * @param tasks Matching tasks.
     * @return Search response.
     */
    public static String matchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Nothing matching that signal is on your mission board.";
        }

        return "I found these matching tasks on your mission board:\n"
                + formatTasks(tasks);
    }

    /**
     * Formats tasks occurring on a date.
     *
     * @param date Date being shown.
     * @param tasks Tasks occurring on the date.
     * @return Dated-task response.
     */
    public static String tasksOnDate(
            LocalDate date,
            List<Task> tasks
    ) {
        String heading = "Mission schedule for "
                + date.format(DISPLAY_DATE_FORMAT) + ":";

        if (tasks.isEmpty()) {
            return heading + "\nNo tasks are on the radar.";
        }

        return heading + "\n" + formatTasks(tasks);
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     * @return Addition response.
     */
    public static String taskAdded(Task task, int taskCount) {
        return "Task logged. One less thing to keep in your head:\n  "
                + task + "\nYou now have " + taskCount
                + taskNoun(taskCount) + " on the mission board.";
    }

    /**
     * Formats confirmation that a task was marked.
     *
     * @param task Marked task.
     * @return Mark response.
     */
    public static String taskMarked(Task task) {
        return "Mission complete—nice work:\n  " + task;
    }

    /**
     * Formats confirmation that a task was unmarked.
     *
     * @param task Unmarked task.
     * @return Unmark response.
     */
    public static String taskUnmarked(Task task) {
        return "No pressure. This task is back on the radar:\n  " + task;
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Number of tasks after the deletion.
     * @return Deletion response.
     */
    public static String taskDeleted(Task task, int taskCount) {
        return "Cleared from the mission board:\n  " + task
                + "\nYou now have " + taskCount
                + taskNoun(taskCount) + " on the board.";
    }

    /**
     * Formats a recoverable error.
     *
     * @param message Explanation of the error.
     * @return Error response.
     */
    public static String error(String message) {
        return "OOPS!!! " + message;
    }

    private static String formatTasks(List<Task> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(index -> String.format(
                        "%d. %s",
                        index + 1,
                        tasks.get(index)
                ))
                .collect(Collectors.joining("\n"));
    }

    private static String taskNoun(int taskCount) {
        return taskCount == 1 ? " task" : " tasks";
    }
}
