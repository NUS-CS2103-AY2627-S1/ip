package neil.ui;

import java.util.List;

import neil.task.Task;
import neil.task.ToDoList;

/**
 * Formats messages shown to the user.
 */
public class Ui {
    private static final int HELP_COMMAND_WIDTH = 40;
    private static final int HELP_PURPOSE_WIDTH = 26;
    private static final String HELP_ROW_FORMAT = "%-" + HELP_COMMAND_WIDTH + "s  %-"
            + HELP_PURPOSE_WIDTH + "s  %s";

    /**
     * Returns the welcome message.
     *
     * @return welcome message.
     */
    public String showWelcome() {
        String banner =
                "#   #  #####  #####  #    \n"
                        + "##  #  #        #    #    \n"
                        + "# # #  ####     #    #    \n"
                        + "#  ##  #        #    #    \n"
                        + "#   #  #####  #####  #####";

        return banner + "\n\nHello! I'm Neil.\nWhat can I do for you?\n"
                + "Type \"help\" to see available commands.";
    }

    /**
     * Returns guidance for using Neil's commands.
     *
     * @return command descriptions and examples.
     */
    public String showHelp() {
        return String.join(
                "\n",
                "AVAILABLE COMMANDS",
                "",
                formatHelpRow("COMMAND", "PURPOSE", "EXAMPLE"),
                formatHelpRow(
                        "-".repeat(HELP_COMMAND_WIDTH),
                        "-".repeat(HELP_PURPOSE_WIDTH),
                        "-".repeat(42)
                ),
                formatHelpRow("todo DESCRIPTION", "Add a todo task.", "todo read book"),
                formatHelpRow(
                        "deadline DESCRIPTION /by YYYY-MM-DD",
                        "Add a deadline task.",
                        "deadline return book /by 2026-09-15"
                ),
                formatHelpRow(
                        "event DESCRIPTION /from START /to END",
                        "Add an event task.",
                        "event meeting /from 2026-09-15 14:00 /to 2026-09-15 15:00"
                ),
                formatHelpRow("list", "Show all tasks.", "list"),
                formatHelpRow("mark NUMBER", "Mark a task as done.", "mark 1"),
                formatHelpRow("unmark NUMBER", "Mark a task as not done.", "unmark 1"),
                formatHelpRow("delete NUMBER", "Delete a task.", "delete 1"),
                formatHelpRow("find KEYWORD", "Find matching tasks.", "find book"),
                formatHelpRow("help", "Show this help table.", "help"),
                formatHelpRow("bye", "Exit Neil.", "bye"),
                "",
                "NUMBER is the task number shown by list.",
                "START and END use yyyy-MM-dd HH:mm and START must be earlier than END."
        );
    }

    private String formatHelpRow(String command, String purpose, String example) {
        return String.format(HELP_ROW_FORMAT, command, purpose, example);
    }

    /**
     * Returns the goodbye message.
     *
     * @return goodbye message.
     */
    public String showGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Returns an error message.
     *
     * @param message error message to display.
     * @return error message.
     */
    public String showError(String message) {
        return message;
    }

    /**
     * Returns confirmation that a task was marked as completed.
     *
     * @param task task marked as completed.
     * @return task-marked confirmation.
     */
    public String showTaskMarked(Task task) {
        return String.format(
                "Nice! I've marked this task as done:\n%s",
                task
        );
    }

    /**
     * Returns confirmation that a task was marked as incomplete.
     *
     * @param task task marked as incomplete.
     * @return task-unmarked confirmation.
     */
    public String showTaskUnmarked(Task task) {
        return String.format(
                "OK, I've marked this task as not done yet:\n%s",
                task
        );
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task deleted task.
     * @param taskCount number of tasks remaining.
     * @return task-deleted confirmation.
     */
    public String showTaskDeleted(Task task, int taskCount) {
        return String.format(
                "Noted. I've removed this task:\n%s\n"
                        + "Now you have %d tasks in this list.",
                task,
                taskCount
        );
    }

    /**
     * Returns the tasks in the specified list.
     *
     * @param toDoList task list to display.
     * @return numbered task list message.
     */
    public String showTaskList(ToDoList toDoList) {
        return String.format(
                "Here are the tasks in your list:\n%s",
                toDoList
        );
    }

    /**
     * Returns the tasks that match a find command.
     *
     * @param matchingTasks matching tasks to display.
     * @return numbered matching-task message.
     */
    public String showMatchingTasks(List<Task> matchingTasks) {
        StringBuilder message = new StringBuilder(
                "Here are the matching tasks in your list:");

        for (int i = 0; i < matchingTasks.size(); i++) {
            message.append(String.format(
                    "\n%d.%s",
                    i + 1,
                    matchingTasks.get(i)
            ));
        }

        return message.toString();
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task added task.
     * @param taskCount number of tasks in the list.
     * @return task-added confirmation.
     */
    public String showTaskAdded(Task task, int taskCount) {
        return String.format(
                "Got it. I've added this task:\n%s\n"
                        + "Now you have %d tasks in the list.",
                task,
                taskCount
        );
    }
}
