package yappa.ui;

import java.time.LocalTime;

import yappa.task.SortField;
import yappa.task.SortOrder;
import yappa.task.Task;
import yappa.task.TaskList;

/**
 * Generates Yappa's messages for the user interface.
 */
public class Ui {

    private static final String LOGO = """
            __   __
            \\ \\ / /__ _ _ __  _ __   __ _
             \\ V / _` | '_ \\| '_ \\ / _` |
              | | (_| | |_) | |_) | (_| |
              |_|\\__,_| .__/| .__/ \\__,_|
                      |_|   |_|
            """;

    private static String buildMessage(String... messageParts) {
        return String.join(System.lineSeparator(), messageParts);
    }

    /**
     * Returns Yappa's logo and welcome message.
     *
     * @return Greeting message.
     */
    public String showGreeting() {
        return buildMessage(LOGO,
                "Good " + getTimeOfDay() + "! I'm Yappa. Ready to yap and get stuff done!",
                "What are we tackling today? Let's do this!",
                "\nNot sure where to start? Enter 'help'");
    }

    /**
     * Returns the goodbye message.
     *
     * @return Goodbye message.
     */
    public String showGoodbye() {
        return "Catch you later :)!";
    }

    /**
     * Returns all current tasks with a standard header message.
     *
     * @param tasks Task list to display.
     * @return Formatted task list message.
     */
    public String showTaskList(TaskList tasks) {
        return showTasks("Here are your current tasks:", tasks);
    }

    /**
     * Returns tasks that match a search query.
     *
     * @param tasks Filtered task list containing matching tasks.
     * @return Formatted matching tasks message.
     */
    public String showMatchingTasks(TaskList tasks) {
        return showTasks("Here are the matching tasks:", tasks);
    }

    /**
     * Returns tasks together with the specified header message.
     *
     * @param message Header message.
     * @param tasks   Tasks to display.
     * @return Formatted task message.
     */
    private String showTasks(String message, TaskList tasks) {
        return buildMessage(message, tasks.toString());
    }

    /**
     * Returns a confirmation that a task has been marked as completed.
     *
     * @param taskDescription Description of the marked task.
     * @return Task marked confirmation.
     */
    public String showTaskMarked(String taskDescription) {
        return buildMessage(
                "Ok! I've marked this task as completed:",
                "\t[X] " + taskDescription);
    }

    /**
     * Returns a confirmation that a task has been unmarked.
     *
     * @param taskDescription Description of the unmarked task.
     * @return Task unmarked confirmation.
     */
    public String showTaskUnmarked(String taskDescription) {
        return buildMessage("Ok! I've unmarked this task:", "\t[ ] " + taskDescription);
    }

    /**
     * Returns a confirmation that a task has been added.
     *
     * @param task      Added task.
     * @param taskCount Number of tasks after the addition.
     * @return Task added confirmation.
     */
    public String showTaskAdded(Task task, int taskCount) {
        return buildMessage("Ok! I have added the task:",
                "\t" + task,
                formatTaskCount(taskCount));
    }

    /**
     * Returns a confirmation that a task has been deleted.
     *
     * @param task      Deleted task.
     * @param taskCount Number of tasks after the deletion.
     * @return Task deleted confirmation.
     */
    public String showTaskDeleted(Task task, int taskCount) {
        return buildMessage(
                "Ok! I will remove this task:",
                "\t" + task,
                formatTaskCount(taskCount));
    }

    /**
     * Returns a confirmation message indicating that all tasks have been cleared.
     *
     * @return A string confirming that all tasks were cleared.
     */
    public String showTaskCleared() {
        return "All tasks cleared";
    }

    /**
     * Returns a confirmation that tasks have been sorted.
     *
     * @return Task sorting confirmation.
     */
    public String showTasksSorted(SortField sortField, SortOrder sortOrder) {
        return "Tasks have been sorted by "
                + sortField
                + " in "
                + sortOrder.toString().toLowerCase()
                + " order!";
    }

    private static String formatTaskCount(int taskCount) {
        return "Now you have " + taskCount
                + (taskCount == 1 ? " task " : " tasks ")
                + "in the list.";
    }

    /**
     * Returns the list of commands available to the user.
     *
     * @return Formatted help menu.
     */
    public String showHelpMenu() {
        return buildMessage(
                "Here are the commands!",
                "\nTask commands:",
                "  todo <description>",
                "  deadline <description> /by <dd/MM/yyyy HHmm>",
                "  event <description> /from <dd/MM/yyyy HHmm> "
                        + "/to <dd/MM/yyyy HHmm>",
                "  mark <task_number>",
                "  unmark <task_number>",
                "  delete <task_number>",
                "  find <search>",

                "\nDisplay and sorting commands:",
                "  list",
                "  sort [alpha|datetime] [asc|desc]",

                "\nGeneral commands:",
                "  help",
                "  clear",
                "  bye"

        );
    }

    /**
     * Determines the greeting period from the current local time.
     *
     * @return {@code Morning}, {@code Afternoon}, or {@code Evening}.
     */
    private String getTimeOfDay() {
        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour();

        if (currentHour >= 12 && currentHour < 17) {
            return "Afternoon";
        } else if (currentHour >= 17 && currentHour < 21) {
            return "Evening";
        } else {
            return "Morning";
        }
    }
}
