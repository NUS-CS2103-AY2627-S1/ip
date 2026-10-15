package zikiai.ui;

import java.util.Scanner;

import zikiai.exception.ZikiaiException;
import zikiai.task.Task;
import zikiai.task.TaskList;

/**
 * Formats shared chatbot responses and handles optional console input and output.
 */
public class Ui {
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = " _____ _ _    _       _\n"
            + "|__  /(_) | _(_) __ _(_)\n"
            + "  / / | | |/ / |/ _` | |\n"
            + " / /_ | |   <| | (_| | |\n"
            + "/____||_|_|\\_\\_|\\__,_|_|";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the chatbot banner and greeting.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println(getGreeting());
        System.out.println(SEPARATOR);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return true when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return next complete input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Formats confirmation that a task was marked as done.
     *
     * @param task task whose status changed.
     */
    public static String formatTaskMarked(Task task) {
        return "Shiok! This task is done already:\n    " + task.getDescription();
    }

    /**
     * Formats confirmation that a task was marked as not done.
     *
     * @param task task whose status changed.
     */
    public static String formatTaskUnmarked(Task task) {
        return "Okay can, this task is not done yet:\n    " + task.getDescription();
    }

    /**
     * Formats confirmation that a task's priority was updated.
     *
     * @param task task whose priority changed.
     */
    public static String formatTaskPrioritized(Task task) {
        return "Steady! I've updated this task's priority:\n    " + task.getDescription();
    }

    /**
     * Formats confirmation that a task was removed.
     *
     * @param task task that was removed.
     * @param taskCount number of remaining tasks.
     */
    public static String formatTaskDeleted(Task task, int taskCount) {
        return "Okay, removed this task already:\n    " + task.getDescription()
                + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Formats every task using one-based numbering.
     *
     * @param tasks task list to display.
     */
    public static String formatTaskList(TaskList tasks) {
        return formatNumberedTasks("Here are your tasks:", tasks);
    }

    /**
     * Formats tasks whose descriptions match a find command, or reports no matches.
     *
     * @param matchingTasks matching tasks to display.
     */
    public static String formatMatchingTasks(TaskList matchingTasks) {
        if (matchingTasks.size() == 0) {
            return "Aiyo, no matching tasks leh.";
        }
        return formatNumberedTasks("Found these matching tasks for you:", matchingTasks);
    }

    /**
     * Formats the supplied tasks below a heading using one-based numbering.
     */
    private static String formatNumberedTasks(String heading, TaskList tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(tasks.get(i).getDescription());
        }
        return response.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount current number of tasks.
     */
    public static String formatTaskAdded(Task task, int taskCount) {
        return "Can! I've added this task for you:\n    " + task.getDescription()
                + "\n" + formatTaskCount(taskCount);
    }

    /**
     * Formats a user-facing error message.
     *
     * @param exception error to display.
     */
    public static String formatError(ZikiaiException exception) {
        return "Aiyo! " + exception.getMessage();
    }

    /**
     * Formats the chatbot's farewell message.
     */
    public static String formatGoodbye() {
        return "Okay, bye bye! See you again lah.";
    }

    /**
     * Returns the greeting shared by console and graphical interfaces.
     */
    public static String getGreeting() {
        return "Hello! I'm Zikiai, your task buddy.\nWhat do you need help with today ah?";
    }

    /**
     * Formats a grammatically correct task count with a local conversational rhythm.
     */
    private static String formatTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "You have " + taskCount + " " + taskWord + " in your list now.";
    }

    /**
     * Displays one response between console separators.
     *
     * @param response message from the chatbot.
     */
    public void showResponse(String response) {
        System.out.println(SEPARATOR);
        System.out.println(response);
        System.out.println(SEPARATOR);
    }

    /**
     * Releases the input scanner when the chatbot stops.
     */
    public void close() {
        scanner.close();
    }
}
