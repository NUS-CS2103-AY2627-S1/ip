package eden.ui;

import java.util.List;
import java.util.Scanner;

import eden.task.Task;

/**
 * Handles console input and output for Eden.
 */
public class Ui {
    private static final String BANNER = " _____    _            \n"
            + "| ____|__| | ___ _ __  \n"
            + "|  _| / _` |/ _ \\ '_ \\ \n"
            + "| |__| (_| |  __/ | | |\n"
            + "|_____|\\__,_|\\___|_| |_|\n";
    private static final String DIVIDER =
            "____________________________________________________________\n";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads the text of the next input line as a command.
     *
     * @return command text without the line terminator.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Eden's banner and greeting.
     */
    public void showWelcome() {
        System.out.print(DIVIDER);
        System.out.print(BANNER);
        System.out.println(getWelcomeMessage());
        System.out.print(DIVIDER);
    }

    /**
     * Returns the greeting shown when Eden starts.
     *
     * @return greeting without console decoration.
     */
    public String getWelcomeMessage() {
        return formatLines("Hello! I'm Eden.", "What can I do for you?");
    }

    /**
     * Displays one response with console divider lines.
     *
     * @param response response body to display.
     */
    public void showResponse(String response) {
        System.out.print(DIVIDER);
        System.out.print(response);
        if (!response.endsWith("\n")) {
            System.out.println();
        }
        System.out.print(DIVIDER);
    }

    /**
     * Formats Eden's farewell message.
     *
     * @return farewell message without console decoration.
     */
    public String formatGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Formats all tasks using one-based numbering.
     *
     * @param tasks tasks to format.
     * @return formatted task list without console decoration.
     */
    public String formatTaskList(List<Task> tasks) {
        return formatNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Formats tasks matching a find command using one-based result numbering.
     *
     * @param matchingTasks matching tasks to format.
     * @return formatted matching tasks without console decoration.
     */
    public String formatMatchingTasks(List<Task> matchingTasks) {
        return formatNumberedTasks(
                "Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Formats confirmation that a task was marked as completed.
     *
     * @param task task that was marked.
     * @return formatted confirmation without console decoration.
     */
    public String formatTaskMarked(Task task) {
        return formatLines(
                "Nice! I've marked this task as done:",
                "  " + task);
    }

    /**
     * Formats confirmation that a task was marked as not completed.
     *
     * @param task task that was unmarked.
     * @return formatted confirmation without console decoration.
     */
    public String formatTaskUnmarked(Task task) {
        return formatLines(
                "OK, I've marked this task as not done yet:",
                "  " + task);
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount number of tasks after the addition.
     * @return formatted confirmation without console decoration.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return formatLines(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " in the list.");
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks after the deletion.
     * @return formatted confirmation without console decoration.
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        return formatLines(
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " in the list.");
    }

    /**
     * Joins any number of response lines with platform-independent newlines.
     *
     * @param lines response lines in display order.
     * @return lines joined by newline characters.
     */
    private static String formatLines(String... lines) {
        return String.join("\n", lines);
    }

    /**
     * Formats a heading followed by one-based task rows.
     */
    private String formatNumberedTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return response.toString();
    }
}
