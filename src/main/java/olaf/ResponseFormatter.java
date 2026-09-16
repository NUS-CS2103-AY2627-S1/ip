package olaf;

import java.util.List;

/**
 * Produces the user-facing response text shared by Olaf's console and graphical interfaces.
 */
final class ResponseFormatter {
    private static final String GREETING = "Hello! I'm Olaf. What can I do for you?";
    private static final String FAREWELL = "Bye. Hope to see you again soon!";

    private ResponseFormatter() {
    }

    static String formatWelcome() {
        return GREETING;
    }

    static String formatFarewell() {
        return FAREWELL;
    }

    /** Formats all tasks in insertion order with one-based task numbers. */
    static String formatTaskList(TaskList tasks) {
        return formatTasks(" Here are the tasks in your list:", tasks.getTasks());
    }

    /** Formats search results with the same task numbers used by all task commands. */
    static String formatMatchingTasks(List<TaskMatch> matchingTasks) {
        StringBuilder response = new StringBuilder(" Here are the matching tasks in your list:");
        for (TaskMatch match : matchingTasks) {
            response.append(System.lineSeparator())
                    .append(' ')
                    .append(match.taskNumber())
                    .append('.')
                    .append(match.task());
        }
        return response.toString();
    }

    static String formatTaskAdded(Task task, int taskCount) {
        return " Got it. I've added this task:\n   " + task + "\n" + formatTaskCount(taskCount);
    }

    static String formatTaskMarkedAsDone(Task task) {
        return " Nice! I've marked this task as done:\n   " + task;
    }

    static String formatTaskMarkedAsNotDone(Task task) {
        return " OK, I've marked this task as not done yet:\n   " + task;
    }

    static String formatTaskDeleted(Task task, int taskCount) {
        return " Noted. I've removed this task:\n   " + task + "\n" + formatTaskCount(taskCount);
    }

    /** Formats the confirmation and updated task after a successful reschedule. */
    static String formatTaskRescheduled(Task task) {
        return " OK, I've rescheduled this task:\n   " + task;
    }

    static String formatError(String message) {
        return "error: " + message;
    }

    private static String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return " Now you have " + taskCount + " " + taskWord + " in the list.";
    }

    private static String formatTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < tasks.size(); index++) {
            response.append(System.lineSeparator())
                    .append(' ')
                    .append(index + 1)
                    .append('.')
                    .append(tasks.get(index));
        }
        return response.toString();
    }
}
