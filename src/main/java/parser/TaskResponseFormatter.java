package parser;

import task.Task;

/**
 * Formats task-command responses without mutating tasks or accessing storage.
 */
final class TaskResponseFormatter {
    private TaskResponseFormatter() {
        // Utility class: no instances are needed.
    }

    /**
     * Formats the confirmation for a successfully added task.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     * @return addition confirmation without a trailing newline
     */
    static String formatAddition(Task task, int taskCount) {
        return String.format("Got it. I've added this task:\n%s\nNow you have %d tasks in the list.",
                task.renderTask(), taskCount);
    }
}
