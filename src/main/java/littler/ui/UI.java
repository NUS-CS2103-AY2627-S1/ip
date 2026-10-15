package littler.ui;

import java.util.ArrayList;

import littler.datetime.StringDateTimeConverter.ParsedDateTime;
import littler.task.Statistics;
import littler.task.Task;

/**
 * Handles all user interface interactions for the application, including formatting
 * and printing output messages, task lists, banners, and reading user commands.
 */
public class UI {

    /** Prefix that every error message returned by this class starts with. */
    public static final String ERROR_PREFIX = "Error: ";

    // TASK PRINTING METHODS

    /**
     * Displays all tasks currently stored in the task list along with their 1-based index numbers.
     *
     * @param tasks the list of tasks to be printed
     * @return a formatted string representation of the task list
     */
    public static String taskList(ArrayList<Task> tasks) {
        String output = "Tasks in your list:\n";
        for (int i = 0; i < tasks.size(); i++) {
            output += taskWithIndex(i + 1, tasks.get(i)) + "\n";
        }
        return output;
    }

    /**
     * Displays a summary of statistics about the current task list.
     *
     * @param stats the computed Statistics to display
     * @return a formatted statistics summary, or a message if the list is empty
     */
    public static String statistics(Statistics stats) {
        if (stats.total() == 0) {
            return "There are no tasks yet.";
        }
        return "Task statistics:\n"
            + "Total: " + stats.total() + "\n"
            + "Completed: " + stats.completed() + ", Pending: " + stats.pending() + "\n"
            + "By type - Todo: " + stats.todoCount() + ", Deadline: " + stats.deadlineCount()
            + ", Event: " + stats.eventCount() + "\n"
            + "By priority - High: " + stats.highPriorityCount() + ", Medium: " + stats.mediumPriorityCount()
            + ", Low: " + stats.lowPriorityCount() + ", None: " + stats.noPriorityCount() + "\n"
            + "Tagged: " + stats.taggedCount() + ", Untagged: " + stats.untaggedCount() + "\n";
    }

    /**
     * Displays a confirmation message indicating that a task has been marked.
     *
     * @param task the task that was marked
     * @return a string on the confirmation of the marked task
     */
    public static String taskMarked(Task task) {
        return "Marked: " + task;
    }

    /**
     * Displays a confirmation message indicating that a task has been unmarked.
     *
     * @param task the task that was unmarked
     * @return a string on the confirmation of the unmarked task
     */
    public static String taskUnmarked(Task task) {
        return "Unmarked: " + task;
    }

    /**
     * Displays a confirmation message indicating that a task has been tagged.
     *
     * @param task the task that was tagged
     * @return a string on the confirmation of the tagged task
     */
    public static String taskTagged(Task task) {
        return "Tagged: " + task;
    }

    /**
     * Displays a confirmation message indicating that a task has been untagged.
     *
     * @param task the task that was untagged
     * @return a string on the confirmation of the untagged task
     */
    public static String taskUntagged(Task task) {
        return "Untagged: " + task;
    }

    /**
     * Displays a confirmation message indicating that a task's priority has been updated.
     *
     * @param task the task whose priority was updated
     * @return a string on the confirmation of the updated priority
     */
    public static String taskPriorityUpdated(Task task) {
        return "Priority updated: " + task;
    }

    /**
     * Displays a confirmation message indicating that a task has been edited.
     *
     * @param task the task that was edited
     * @return a string on the confirmation of the edited task
     */
    public static String taskEdited(Task task) {
        return "Updated: " + task;
    }

    /**
     * Displays a confirmation message indicating that a task was duplicated, along with the total task count.
     *
     * @param task the newly created duplicate task
     * @param total the total number of tasks in the list
     * @return a string on the confirmation of the duplicated task and the updated task count
     */
    public static String taskDuplicated(Task task, int total) {
        return "Duplicated: " + task + "\nNow you have " + total + " tasks in the list.";
    }

    /**
     * Displays a confirmation message indicating that a task was deleted, along with the updated task count.
     *
     * @param task the task that was deleted
     * @param remaining the number of remaining tasks in the list
     * @return a string on the confirmation of the deleted task and the updated task count
     */
    public static String taskDeleted(Task task, int remaining) {
        return "Deleted: " + task + "\nNow you have " + remaining + " tasks in the list.";
    }

    public static String undoSuccessful() {
        return "Undone. Your task list has been restored to its previous state.";
    }

    /**
     * Displays a confirmation message indicating that a task was added, along with the total task count.
     *
     * @param task the task that was added
     * @param total the total number of tasks in the list
     * @return a string on the confirmation of the added task and the updated task count
     */
    public static String taskAdded(Task task, int total) {
        return "Added: " + task + "\nNow you have " + total + " tasks in the list.";
    }

    /**
     * Displays a confirmation message indicating that all tasks have been archived and the list is now empty.
     *
     * @param count the number of tasks that were archived
     * @return a string on the confirmation of the archived tasks and the empty list
     */
    public static String tasksArchived(int count) {
        return count == 0
            ? "There are no tasks to archive."
            : "Archived " + count + " task(s). Your list is now empty.";
    }

    /**
     * Displays a header message preceding a list of tasks scheduled for a specific date.
     *
     * @param date the target parsed date
     * @return a header string indicating that tasks on the specified date will be listed
     */
    public static String tasksOnDateHeader(ParsedDateTime date) {
        return "Tasks on " + date + ":";
    }

    /**
     * Displays a header message preceding a list of tasks that match a search keyword.
     *
     * @return a header string indicating that matching tasks will be listed
     */
    public static String findResultsHeader() {
        return "Here are the matching tasks in your list:";
    }

    /**
     * Displays a single task formatted with its specific index number.
     *
     * @param index the 1-based index of the task
     * @param task the task to be displayed
     * @return a formatted string representation of the task with its index
     */
    public static String taskWithIndex(int index, Task task) {
        return index + ". " + task;
    }

    /**
     * Displays a notification message when no tasks match a given date filter.
     *
     * @return a string about no tasks found on the specified date
     */
    public static String noTasksFound() {
        return "No tasks found on this date.";
    }

    /**
     * Displays a notification message when no tasks match a given search keyword.
     *
     * @return a string about no matching tasks found
     */
    public static String noMatchingTasksFound() {
        return "No matching tasks found.";
    }

    /**
     * Displays a warning that the just-added task appears to duplicate an existing one.
     *
     * @return a string warning the user about a likely duplicate task
     */
    public static String duplicateTaskWarning() {
        return "Note: This looks like a duplicate of an existing task.\n";
    }

    // ERROR PRINTING METHODS

    /**
     * Prints an error message when an invalid command is entered and displays available help options.
     *
     * @return a string of an invalid command error message
     */
    public static String commandNotFoundError() {
        return error("Invalid command. Please use the \"help\" command to see available options.");
    }

    /**
     * Prints a formatted general error message.
     *
     * @param message the error message details to be displayed
     * @return a string of the formatted error message
     */
    public static String error(String message) {
        return ERROR_PREFIX + message;
    }

    // GENERAL PRINTING METHODS

    /**
     * Prints the help menu containing all available commands and accepted date/time formats.
     *
     * @return a string of the help menu with available commands and formats
     */
    public static String help() {
        return "Available commands:\n"
            + "1. list [l] - List all tasks\n"

            + "2. mark <task number(s)> [m] - Mark one or more tasks as completed, e.g. mark 1 3\n"

            + "3. unmark <task number(s)> [um] - Unmark one or more tasks\n"

            + "4. todo <task description> [t] - Add a Todo task\n"

            + "5. deadline <task description> /by <due datetime> [dl] - Add a Deadline task\n"

            + "6. event <task description> /from <start datetime> /to <end datetime> [ev] - Add an Event task\n"

            + "7. delete <task number(s)> [d] - Delete one or more tasks, e.g. delete 1 2 3\n"

            + "8. on <date> [o] - List tasks due or occurring on a given date\n"

            + "9. find <keyword> [f] - Search for tasks containing a specific keyword\n"

            + "10. sort /by <date|name|priority|tag> /order <a|d> [so] - View tasks sorted by date, name, "
            + "priority, or first tag (does not change the saved task order)\n"

            + "11. edit <task number> /name <new name> /by <new due> /from <new start> /to <new end> [ed] "
            + "- Update one or more fields of an existing task (only provide the fields you want to change)\n"

            + "12. duplicate <task number> /name <new name> /by <new due> /from <new start> /to <new end> [dp] "
            + "- Create a copy of an existing task, optionally overriding some fields "
            + "(the copy always starts unmarked)\n"

            + "13. tag <task number(s)> <tag> [tg] - Add a tag to one or more tasks, e.g. tag 1 2 fun\n"

            + "14. untag <task number> <tag> [ut] - Remove a tag from a task\n"

            + "15. priority <task number(s)> <level> [p] "
            + "- Set priority (low/medium/high) on one or more tasks, e.g. priority 1 3 high\n"

            + "16. archive [a] - Archive all current tasks to a backup file and start with a clean slate\n"

            + "17. stats [st] - View statistics about your current task list\n"

            + "18. undo [un] - Undo the most recent change to your task list "
            + "(only applies to commands that modify it, e.g. not list/find/sort/stats)\n"

            + "19. help [h] - Show this list of commands\n"

            + "20. bye [b] - Exit the program\n"

            + "Date is in the format of d-M-yyyy or yyyy-M-d, or a weekday abbreviation (e.g. Tue) "
            + "meaning the next such day\n"
            + "Datetime is similar to date, with an optional time in the format of HHmm\n";
    }

    /**
     * Prints the ASCII art application header banner.
     *
     * @return a string of the ASCII art banner
     */
    public static String banner() {
        return "     ___                   ___           ___           ___       ___           ___     \n"
            + "    /\\__\\      ___        /\\  \\         /\\  \\"
            + "         /\\__\\     /\\  \\         /\\  \\    \n"
            + "   /:/  /     /\\  \\       \\:\\  \\        \\:\\"
            + "  \\       /:/  /    /::\\  \\       /::\\  \\   \n"
            + "  /:/  /      \\:\\  \\       \\:\\  \\        \\:\\"
            + "  \\     /:/  /    /:/\\:\\  \\     /:/\\:\\  \\  \n"
            + " /:/  /       /::\\__\\      /::\\  \\       /::\\  "
            + "\\   /:/  /    /::\\~\\:\\  \\   /::\\~\\:\\  \\ \n"
            + "/:/__/     __/:/\\/__/     /:/\\:\\__\\     /:/\\:\\"
            + "__\\ /:/__/    /:/\\:\\ \\:\\__\\ /:/\\:\\ \\:\\__\\\n"
            + "\\:\\  \\    /\\/:/  /       /:/  \\/__/    /:/  \\/"
            + "__/ \\:\\  \\    \\:\\~\\:\\ \\/__/ \\/_|::\\/:/  /\n"
            + " \\:\\  \\   \\::/__/       /:/  /        /:/  /    "
            + "   \\:\\  \\    \\:\\ \\:\\__\\      |:|::/  / \n"
            + "  \\:\\  \\   \\:\\__\\       \\/__/         \\/__"
            + "/         \\:\\  \\    \\:\\ \\/__/      |:|\\/__/  \n"
            + "   \\:\\__\\   \\/__/                            "
            + "        \\:\\__\\    \\:\\__\\        |:|  |    \n"
            + "    \\/__/                                        "
            + "     \\/__/     \\/__/         \\|__|      \n";
    }

    /**
     * Prints the greeting welcome message.
     *
     * @return a string of the greeting welcome message
     */
    public static String welcome() {
        return "Hello! I'm LittleR \nWhat can I do for you?\n";
    }

    /**
     * Prints the exit farewell message.
     *
     * @return a string of the exit farewell message
     */
    public static String goodbye() {
        return "Bye. Hope to see you again soon!\n";
    }
}
