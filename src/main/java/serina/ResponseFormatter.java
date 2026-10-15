package serina;

import java.util.List;

import serina.task.Task;

/**
 * Builds the user-facing messages shared by Serina's console and graphical interfaces.
 */
final class ResponseFormatter {
    // These three lines adapt Serina dialogue documented at https://halo.fandom.com/wiki/Serina/Quotes.
    private static final String SYSTEMS_NORMAL = "Standard orbit achieved, all systems normal.";
    private static final String HELP_HEADING = "Expecting trouble, " + SerinaDialogue.CAPTAIN_NAME
            + "? Here are the available commands:";
    private static final String TASK_ADDED = "Task logged, " + SerinaDialogue.CAPTAIN_NAME
            + ". So...nothing too difficult, then?";
    private static final String HELP = String.join("\n",
            HELP_HEADING,
            "help - show this command list",
            "todo <task> - add a todo",
            "deadline <task> /by <yyyy-MM-dd> - add a deadline",
            "event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd> - add an event",
            "list - show all tasks",
            "mark <number> - mark a task as done",
            "unmark <number> - mark a task as not done",
            "delete <number> - remove a task",
            "find <keywords> - find tasks containing all keywords",
            "bye - exit Serina");
    private static final String GOODBYE = "Signing off, " + SerinaDialogue.CAPTAIN_NAME
            + ". Do try to keep things orderly.";

    /**
     * Prevents instantiation of this response-formatting utility class.
     */
    private ResponseFormatter() {
    }

    /**
     * Returns Serina's greeting.
     *
     * @return Greeting shown when Serina starts.
     */
    static String formatGreeting(boolean areSystemsNormal) {
        String greeting = "Standing by, " + SerinaDialogue.CAPTAIN_NAME
                + ". Type help for available commands.";
        return areSystemsNormal ? String.join("\n", greeting, SYSTEMS_NORMAL) : greeting;
    }

    /**
     * Returns Serina's command reference.
     *
     * @return Help message containing every supported command.
     */
    static String formatHelp() {
        return HELP;
    }

    /**
     * Returns Serina's farewell message.
     *
     * @return Farewell shown when a conversation ends.
     */
    static String formatGoodbye() {
        return GOODBYE;
    }

    /**
     * Returns the message shown after adding a task.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks after the addition.
     * @return Task-added response.
     */
    static String formatAddedTask(Task task, int taskCount) {
        return String.join("\n",
                TASK_ADDED,
                "  " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Returns the message shown after marking a task as done.
     *
     * @param task Task that was marked.
     * @return Task-marked response.
     */
    static String formatMarkedTask(Task task) {
        return String.join("\n",
                "Marked complete, " + SerinaDialogue.CAPTAIN_NAME + ". A measurable improvement.",
                "  " + task);
    }

    /**
     * Returns the message shown after marking a task as not done.
     *
     * @param task Task that was unmarked.
     * @return Task-unmarked response.
     */
    static String formatUnmarkedTask(Task task) {
        return String.join("\n",
                "Back on the roster, " + SerinaDialogue.CAPTAIN_NAME + ". Optimism was premature.",
                "  " + task);
    }

    /** Returns the message shown when an already completed task is marked. */
    static String formatAlreadyMarkedTask(Task task) {
        return String.join("\n",
                "Already complete, " + SerinaDialogue.CAPTAIN_NAME + ". No changes were needed.",
                "  " + task);
    }

    /** Returns the message shown when an incomplete task is unmarked. */
    static String formatAlreadyUnmarkedTask(Task task) {
        return String.join("\n",
                "That task is already active, " + SerinaDialogue.CAPTAIN_NAME + ". No changes were needed.",
                "  " + task);
    }

    /**
     * Returns the message shown after deleting a task.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of tasks after the deletion.
     * @return Task-deleted response.
     */
    static String formatDeletedTask(Task task, int taskCount) {
        return String.join("\n",
                "Task removed, " + SerinaDialogue.CAPTAIN_NAME + ". One less item on the roster.",
                "  " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Returns all tasks with one-based numbering.
     *
     * @param tasks Tasks to display.
     * @return Numbered task-list response.
     */
    static String formatTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No tasks on the roster, " + SerinaDialogue.CAPTAIN_NAME
                    + ". An unusually peaceful situation.";
        }
        return formatNumberedTasks("Your task roster, " + SerinaDialogue.CAPTAIN_NAME + ":", tasks);
    }

    /**
     * Returns matching tasks with one-based numbering.
     *
     * @param tasks Matching tasks to display.
     * @return Numbered matching-task response.
     */
    static String formatMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No matching tasks, " + SerinaDialogue.CAPTAIN_NAME + ". Try different keywords.";
        }
        return formatNumberedTasks("Matching tasks, " + SerinaDialogue.CAPTAIN_NAME + ":", tasks);
    }

    /**
     * Returns a heading followed by tasks numbered from one.
     */
    private static String formatNumberedTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append('\n').append(i + 1).append('.').append(tasks.get(i));
        }
        return response.toString();
    }

    /**
     * Returns a grammatically correct summary of the current task count.
     */
    private static String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + taskWord + " on the roster.";
    }
}
