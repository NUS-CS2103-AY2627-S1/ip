package xuan.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import xuan.task.Deadline;
import xuan.task.Task;
import xuan.task.TaskList;

/**
 * Handles interaction with the user, including reading commands
 * and displaying messages.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a Ui object for reading user input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the application banner.
     */
    public void showBanner() {
        String banner = "__  __  _   _    _    _   _\n"
                + "\\ \\/ / | | | |  / \\  | \\ | |\n"
                + " \\  /  | | | | / _ \\ |  \\| |\n"
                + " /  \\  | |_| |/ ___ \\| |\\  |\n"
                + "/_/\\_\\  \\___//_/   \\_\\_| \\_|\n";

        System.out.println(banner);
    }

    /**
     * Reads and returns a command entered by the user.
     *
     * @return the user's command
     */
    public String readCommand() {
        System.out.print("You: ");
        return scanner.nextLine();
    }

    /**
     * Returns the greeting message.
     *
     * @return the greeting message
     */
    public String getGreetingMessage() {
        return "Hey! I'm Xuan, your mission control.\n"
                + "What are we taking on today?";
    }

    /**
     * Displays the greeting message.
     */
    public void showGreeting() {
        showMessage(
                "Hey! I'm Xuan, your mission control.",
                "What are we taking on today?"
        );
        System.out.println();
    }

    /**
     * Returns the goodbye message.
     *
     * @return the goodbye message
     */
    public String getByeMessage() {
        return "Mission complete for today. See you next time!";
    }

    /**
     * Displays the goodbye message.
     */
    public void showBye() {
        System.out.println(getByeMessage());
    }

    /**
     * Displays one or more specified messages.
     *
     * @param messages the messages to display
     */
    public void showMessage(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Returns all tasks in the task list as a formatted message.
     *
     * @param taskList the task list to format
     * @return the formatted task list
     */
    public String getTaskListMessage(TaskList taskList) {
        StringBuilder message = new StringBuilder(
                "Here's your mission board:");

        for (int i = 0; i < taskList.size(); i++) {
            message.append("\n      ")
                    .append(i + 1)
                    .append(". ")
                    .append(taskList.get(i));
        }

        return message.toString();
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param taskList the task list to display
     */
    public void showTaskList(TaskList taskList) {
        System.out.println(getTaskListMessage(taskList));
    }

    /**
     * Returns a formatted error message.
     *
     * @param message the error message
     * @return the formatted error message
     */
    public String getErrorMessage(String message) {
        return "Mission alert - " + message;
    }

    /**
     * Displays an error message.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println(getErrorMessage(message));
    }

    /**
     * Returns information about a newly added task.
     *
     * @param task the added task
     * @param taskCount the current number of tasks
     * @return the formatted message about the added task
     */
    public String getAddedTaskMessage(Task task, int taskCount) {
        return "Mission accepted! I've added this task:\n"
                + "      " + task + "\n"
                + "      You now have " + taskCount
                + " missions on the board.";
    }

    /**
     * Displays information about a newly added task.
     *
     * @param task the added task
     * @param taskCount the current number of tasks
     */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println(getAddedTaskMessage(task, taskCount));
    }

    /**
     * Returns information about a deleted task.
     *
     * @param task the deleted task
     * @param taskCount the current number of tasks
     * @return the formatted message about the deleted task
     */
    public String getDeletedTaskMessage(Task task, int taskCount) {
        return "Mission dropped. I've removed this task:\n"
                + "      " + task + "\n"
                + "      You now have " + taskCount
                + " missions on the board.";
    }

    /**
     * Displays information about a deleted task.
     *
     * @param task the deleted task
     * @param taskCount the current number of tasks
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println(getDeletedTaskMessage(task, taskCount));
    }

    /**
     * Returns a message for a task that has been marked as done.
     *
     * @param task the marked task
     * @return the formatted message about the marked task
     */
    public String getMarkedTaskMessage(Task task) {
        return "Mission cleared! Nice work:\n"
                + "      " + task;
    }

    /**
     * Displays a task that has been marked as done.
     *
     * @param task the marked task
     */
    public void showMarkedTask(Task task) {
        System.out.println(getMarkedTaskMessage(task));
    }

    /**
     * Returns a message for a task that has been marked as not done.
     *
     * @param task the unmarked task
     * @return the formatted message about the unmarked task
     */
    public String getUnmarkedTaskMessage(Task task) {
        return "Back on the mission board! I've marked this task "
                + "as not done yet:\n"
                + "      " + task;
    }

    /**
     * Displays a task that has been marked as not done.
     *
     * @param task the unmarked task
     */
    public void showUnmarkedTask(Task task) {
        System.out.println(getUnmarkedTaskMessage(task));
    }

    /**
     * Returns the deadlines that occur on the specified date.
     *
     * @param targetDate the date of the deadlines
     * @param deadlines the deadlines occurring on the specified date
     * @return the formatted deadline list
     */
    public String getDeadlinesOnDateMessage(
            LocalDate targetDate, ArrayList<Deadline> deadlines) {
        StringBuilder message = new StringBuilder(
                "Deadline scan for " + targetDate + ":");

        if (deadlines.isEmpty()) {
            message.append("\n      All clear! No deadlines found.");
        } else {
            for (int i = 0; i < deadlines.size(); i++) {
                message.append("\n      ")
                        .append(i + 1)
                        .append(". ")
                        .append(deadlines.get(i));
            }
        }

        return message.toString();
    }

    /**
     * Shows the deadlines that occur on the specified date.
     *
     * @param targetDate the date of the deadlines to display
     * @param deadlines the list of deadlines occurring on the specified date
     */
    public void showDeadlinesOnDate(
            LocalDate targetDate, ArrayList<Deadline> deadlines) {
        System.out.println(
                getDeadlinesOnDateMessage(targetDate, deadlines));
    }

    /**
     * Returns the tasks that match the given search keyword.
     *
     * @param tasks the matching tasks
     * @return the formatted matching tasks
     */
    public String getMatchingTasksMessage(ArrayList<Task> tasks) {
        StringBuilder message = new StringBuilder(
                "I've tracked down these matching missions:");

        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n      ")
                    .append(i + 1)
                    .append(". ")
                    .append(tasks.get(i));
        }

        return message.toString();
    }

    /**
     * Displays the tasks that match the given search keyword.
     *
     * @param tasks the matching tasks to display
     */
    public void showMatchingTasks(ArrayList<Task> tasks) {
        System.out.println(getMatchingTasksMessage(tasks));
    }

    /**
     * Returns the help message containing the available commands.
     *
     * @return the formatted help message
     */
    public String getHelpMessage() {
        return String.join("\n",
                "Xuan is your mission-control task buddy for managing "
                        + "todos, deadlines, and events.",
                "",
                "Here are the commands you can use:",
                "",
                "help",
                "    Shows this help message.",
                "",
                "list",
                "    Shows all tasks on your mission board.",
                "",
                "todo <description>",
                "    Adds a todo task.",
                "    Example: todo read book",
                "",
                "deadline <description> /by yyyy-MM-dd",
                "    Adds a deadline task.",
                "    The date must use the format yyyy-MM-dd.",
                "    Example: deadline submit report /by 2026-09-10",
                "",
                "event <description> /from <start time> /to <end time>",
                "    Adds an event task.",
                "    The start and end times can be written as text.",
                "    Example: event meeting /from 2pm /to 4pm",
                "",
                "mark <task number>",
                "    Marks a mission as cleared.",
                "    Example: mark 2",
                "",
                "unmark <task number>",
                "    Puts a cleared mission back on the board.",
                "    Example: unmark 2",
                "",
                "delete <task number>",
                "    Removes a mission from the board.",
                "    Example: delete 2",
                "",
                "find <keyword>",
                "    Tracks down tasks whose descriptions contain "
                        + "the keyword.",
                "    Example: find book",
                "",
                "finddate yyyy-MM-dd",
                "    Scans for deadlines on the specified date.",
                "    The date must use the format yyyy-MM-dd.",
                "    Example: finddate 2026-09-10",
                "",
                "bye",
                "    Ends today's mission with Xuan.");
    }
}
