package bott.ui;

import java.util.List;
import java.util.Scanner;

import bott.task.Task;

/**
 * Produces the text of Bott's responses, and drives the console interface:
 * reading the commands the user types and printing Bott's replies wrapped
 * in horizontal dividers.
 *
 * <p>The {@code get...Message} methods return a reply as a plain string
 * (lines separated by {@code "\n"}, no divider or indent). The console
 * uses them via {@link #showMessage(String)}; the GUI uses the same
 * methods to get divider-free text for its dialog bubbles.
 */
public class Ui {

    /** Indent shared by the horizontal divider and every line of message text. */
    private static final String INDENT = "    ";

    /** Horizontal divider printed around every message, indented to match the message text. */
    private static final String HORIZONTAL_LINE =
            INDENT + "____________________________________________________________";

    /** Startup banner spelling out "SARGE", printed once above the greeting. */
    private static final String BANNER =
            " ____        _       _____      ____    _____ \n"
            + "/ ___|      / \\     |  __ \\    / ___|  |  ___|\n"
            + "\\___ \\     / _ \\    | |__) |  | |  _   | |__  \n"
            + " ___) |   / ___ \\   |  _  /   | |_| |  |  __| \n"
            + "|____/   /_/   \\_\\  |_|  \\_\\   \\____|  |_____|\n";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints Bott's startup banner and greeting to the console. */
    public void showWelcome() {
        System.out.println(HORIZONTAL_LINE);
        System.out.print(BANNER);
        showMessage(getWelcomeMessage());
    }

    /**
     * Reads the next line of input typed by the user.
     *
     * @return Line of input typed by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints Bott's farewell message to the console and releases the input resources. */
    public void showGoodbye() {
        showMessage(getGoodbyeMessage());
        scanner.close();
    }

    /**
     * Prints an error message to the console.
     *
     * @param message Description of what went wrong.
     */
    public void showError(String message) {
        showMessage(getErrorMessage(message));
    }

    /**
     * Prints a reply to the console: each line indented and the whole
     * block wrapped in horizontal dividers, followed by a blank line.
     *
     * @param message Reply text, with lines separated by {@code "\n"}.
     */
    public void showMessage(String message) {
        System.out.println(HORIZONTAL_LINE);
        for (String line : message.split("\n", -1)) {
            System.out.println(INDENT + " " + line);
        }
        System.out.println(HORIZONTAL_LINE);
        System.out.println();
    }

    /** Returns Sergeant Bott's greeting. */
    public String getWelcomeMessage() {
        return "Ten-hut! Sergeant Bott reporting for duty.\nWhat's your first order, recruit?";
    }

    /** Returns Sergeant Bott's farewell message. */
    public String getGoodbyeMessage() {
        return "Dismissed! Fall out, recruit.";
    }

    /**
     * Returns an error message prefixed with "NEGATIVE, RECRUIT!".
     *
     * @param message Description of what went wrong.
     */
    public String getErrorMessage(String message) {
        return "NEGATIVE, RECRUIT! " + message;
    }

    /**
     * Returns the full task list as a numbered message.
     *
     * @param tasks Tasks stored so far.
     */
    public String getTaskListMessage(List<Task> tasks) {
        return numberedTasks("Roll call! Here's your mission roster:", tasks);
    }

    /**
     * Returns the tasks matching a "find" command's keyword as a numbered message.
     *
     * @param matches Tasks whose description matched the keyword, in list order.
     */
    public String getMatchingTasksMessage(List<Task> matches) {
        return numberedTasks("Found these missions matching your intel:", matches);
    }

    /**
     * Returns acknowledgement that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Total number of tasks now stored.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return taskCountMessage("Mission logged, recruit! Fall in:", task, taskCount);
    }

    /**
     * Returns acknowledgement that a task was removed.
     *
     * @param task Task that was removed.
     * @param taskCount Total number of tasks remaining.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return taskCountMessage("Mission scrubbed, recruit! Fall out:", task, taskCount);
    }

    /**
     * Returns acknowledgement that a task was marked as done.
     *
     * @param task Task that was marked as done.
     */
    public String getTaskMarkedMessage(Task task) {
        return "Outstanding! Mission accomplished:\n  " + task;
    }

    /**
     * Returns acknowledgement that a task was marked as not done.
     *
     * @param task Task that was marked as not done.
     */
    public String getTaskUnmarkedMessage(Task task) {
        return "At ease. Mission's back on the roster:\n  " + task;
    }

    /**
     * Returns a headline, the indented task, and the resulting task count
     * as one message - the shape shared by the "added" and "removed"
     * acknowledgements.
     *
     * @param headline First line of the message.
     * @param task Task the message is about.
     * @param taskCount Number of tasks now in the list.
     */
    private String taskCountMessage(String headline, Task task, int taskCount) {
        return String.join("\n",
                headline,
                "  " + task,
                "You now have " + taskCount + " mission(s) on the roster.");
    }

    /**
     * Returns a header line followed by one numbered line per task, e.g.
     * "Roll call! Here's your mission roster:\n1.[T][ ] read book".
     */
    private String numberedTasks(String header, List<Task> tasks) {
        StringBuilder message = new StringBuilder(header);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }
}
