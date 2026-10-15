package pip.ui;

import java.util.Scanner;

import pip.task.Task;
import pip.task.TaskList;


/**
 * Handles all console input and output for Pip.
 */
public class Ui {
    private static final String BREAKLINE = "____________________________________________________________\n";
    private final Scanner scanner;
    private String lastResponse = "";

    /** Creates a Ui that reads user commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Pip's opening banner and greeting. */
    public void showWelcome() {
        String banner = "PIP | Small paws. Big plans.\n";
        System.out.println(banner + BREAKLINE);
        System.out.println("Hey there! I'm Pip, your chipmunk task buddy.\n"
                + "Small paws. Big plans. Let's chip away at your tasks!\n" + BREAKLINE);
    }

    /**
     * Reads one trimmed command.
     *
     * @return The command text, or null when input has ended.
     */
    public String readCommand() {
        System.out.print("What shall we chip away at? ");
        if (!scanner.hasNextLine()) {
            return null;
        }
        String input = scanner.nextLine().trim();
        System.out.println(BREAKLINE);
        return input;
    }

    /**
     * Shows the current task list.
     *
     * @param tasks The tasks to display in list order.
     */
    public void showTaskList(TaskList tasks) {
        showTasks(tasks, "Here is your task stash:\n");
    }

    /**
     * Shows the tasks matching a search keyword.
     *
     * @param matchingTasks The search results to display.
     */
    public void showMatchingTasks(TaskList matchingTasks) {
        showTasks(matchingTasks, "Sniffed them out! Here are your matching tasks:\n");
    }

    /** Builds and displays a task list with the supplied heading. */
    private void showTasks(TaskList tasks, String heading) {
        StringBuilder response = new StringBuilder(heading);
        for (int index = 0; index < tasks.size(); index++) {
            response.append(index + 1).append(".").append(tasks.get(index)).append("\n");
        }
        lastResponse = response.toString().trim();
        System.out.println(lastResponse);
        System.out.println(BREAKLINE);
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task The newly added task.
     * @param taskCount The total number of tasks after adding it.
     */
    public void showTaskAdded(Task task, int taskCount) {
        lastResponse = String.format(
                "Acorn secured! I've added this task:\n    %s\nNow you have %d task(s) in your stash.",
                task, taskCount);
        System.out.println(lastResponse);
        System.out.println(BREAKLINE);
    }

    /**
     * Shows confirmation that a task's completion state changed.
     *
     * @param task The updated task.
     * @param completed Whether the task is now complete.
     */
    public void showTaskMarked(Task task, boolean completed) {
        lastResponse = completed
                ? String.format("Nice nibbling! I've marked this task as done:\n    %s", task)
                : String.format("Back in the stash! I've marked this task as not done yet:\n    %s", task);
        System.out.println(lastResponse);
        System.out.println(BREAKLINE);
    }

    /**
     * Shows confirmation that a task was removed.
     *
     * @param task The removed task.
     * @param taskCount The number of remaining tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        lastResponse = String.format(
                "Cleared from the stash! I've removed this task:\n    %s\nNow you have %d task(s) in your stash.",
                task, taskCount);
        System.out.println(lastResponse);
        System.out.println(BREAKLINE);
    }

    /**
     * Shows an error message with the standard divider.
     *
     * @param message The error explanation to display.
     */
    public void showError(String message) {
        lastResponse = message;
        System.out.println(message);
        System.out.println(BREAKLINE);
    }

    /** Shows Pip's farewell. */
    public void showGoodbye() {
        lastResponse = "Scampering off! See you next time, task buddy.";
        System.out.println(lastResponse);
    }

    /**
     * Returns the latest command response produced by this UI, without console dividers.
     *
     * @return The response text, or an empty string before any command response.
     */
    public String getLastResponse() {
        return lastResponse;
    }
}
