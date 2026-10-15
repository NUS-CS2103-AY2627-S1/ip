package magnus.command;

/**
 * Formats task-count summaries used in command responses.
 */
final class TaskCountMessage {
    private TaskCountMessage() {
    }

    /**
     * Formats the current number of tasks, using the correct singular or plural noun.
     *
     * @param taskCount The current number of tasks.
     * @return A user-facing task-count summary.
     */
    static String formatTotal(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        return String.format("\tYou now have %d %s in the list.", taskCount, taskNoun);
    }

    /**
     * Formats the current number of completed tasks, using the correct noun form.
     *
     * @param completedTaskCount The current number of completed tasks.
     * @return A user-facing completed-task summary.
     */
    static String formatCompleted(long completedTaskCount) {
        String taskNoun = completedTaskCount == 1 ? "task" : "tasks";
        return String.format("\tYou have completed %d %s.", completedTaskCount, taskNoun);
    }
}
