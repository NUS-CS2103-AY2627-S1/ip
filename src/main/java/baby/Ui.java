package baby;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles command-line input and output for Baby.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private Scanner scanner;
    private boolean shouldPrint;
    private String lastResponse;

    /**
     * Creates a UI that reads commands from the command line.
     */
    public Ui() {
        this(true);
    }

    /**
     * Creates a UI that can either print responses or only remember them.
     *
     * @param shouldPrint True if responses should be printed to the console.
     */
    public Ui(boolean shouldPrint) {
        this.shouldPrint = shouldPrint;
        lastResponse = "";
        if (shouldPrint) {
            scanner = new Scanner(System.in);
        }
    }

    /**
     * Checks whether there is another line of user input.
     *
     * @return True if another command can be read.
     */
    public boolean hasNextLine() {
        if (scanner == null) {
            return false;
        }

        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return The trimmed command.
     */
    public String readCommand() {
        if (scanner == null) {
            return "";
        }

        return scanner.nextLine().trim();
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Prints the welcome message.
     */
    public void showWelcome() {
        printResponse("Welcome back, Your Highness!", "What would you like to do today?");
    }

    /**
     * Prints the goodbye message.
     */
    public void showGoodbye() {
        printResponse("Until next time, Your Highness!", "Stay wonderful!");
    }

    /**
     * Prints the message shown after adding a task.
     *
     * @param task The task that was added.
     * @param taskCount The number of tasks after adding.
     */
    public void printTaskAdded(Task task, int taskCount) {
        printResponse(
                "Consider it done, Your Highness!",
                "I've added: " + task,
                "You now have " + taskCount + " " + taskCountLabel(taskCount) + " in your list.");
    }

    /**
     * Prints all stored tasks with their current 1-based task numbers.
     *
     * @param tasks The current task list.
     */
    public void printTaskList(ArrayList<Task> tasks) {
        String[] lines = new String[tasks.size() + 1];
        lines[0] = "Your Highness, here are the tasks in your list:";
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + tasks.get(i);
        }

        printResponse(lines);
    }

    /**
     * Prints tasks that match a find command.
     *
     * @param matchingTasks The tasks whose descriptions contain the keyword.
     */
    public void printMatchingTasks(ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            printResponse("No matching tasks were found, Your Highness.");
            return;
        }

        String[] lines = new String[matchingTasks.size() + 1];
        lines[0] = "Here are the matching tasks, Your Highness:";
        for (int i = 0; i < matchingTasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + matchingTasks.get(i);
        }

        printResponse(lines);
    }

    /**
     * Prints an error message.
     *
     * @param message The error message to print.
     */
    public void printError(String message) {
        printResponse(message);
    }

    /**
     * Prints one chatbot response wrapped in horizontal lines.
     *
     * @param lines Lines to show inside the response box.
     */
    public void printResponse(String... lines) {
        lastResponse = formatResponse(lines);
        if (shouldPrint) {
            System.out.print(lastResponse);
        }
    }

    /**
     * Returns the most recent response created by this UI.
     *
     * @return The latest response text.
     */
    public String getLastResponse() {
        return lastResponse;
    }

    /**
     * Returns the correctly pluralized label for a number of tasks.
     *
     * @param taskCount The number of tasks.
     * @return "task" for one task, or "tasks" otherwise.
     */
    private String taskCountLabel(int taskCount) {
        return taskCount == 1 ? "task" : "tasks";
    }

    /**
     * Formats response lines in the same style used by the console UI.
     *
     * @param lines Lines to show inside the response box.
     * @return The formatted response text.
     */
    private String formatResponse(String... lines) {
        StringBuilder response = new StringBuilder();
        response.append(LINE).append(System.lineSeparator());
        for (String line : lines) {
            response.append(" ").append(line).append(System.lineSeparator());
        }
        response.append(LINE).append(System.lineSeparator());
        return response.toString();
    }
}
