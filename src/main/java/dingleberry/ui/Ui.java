package dingleberry.ui;

import java.util.Scanner;

import dingleberry.model.Task;
import dingleberry.model.TaskList;

/**
 * Handles all interaction with the user: reading raw command lines from
 * standard input and printing all messages (banners, task updates, and
 * errors) to standard output.
 */
public class Ui {
    /** Startup banner shown when the application first opens. */
    protected static final String WELCOME_MESSAGE = String.join("\n",
        "____________________________________________________________",
        " ____  _             _      _                          ",
        "|  _ \\(_)_ __   __ _| | ___| |__   ___ _ __ _ __ _   " + "_   _ ",
        "| | | | | '_ \\ / _` | |/ _ \\ '_ \\ / _ \\ '__| '__| | | |",
        "| |_| | | | | | | (_| | |  __/ |_) |  __/ |  | |  | |_| |",
        "|____/|_|_| |_|\\__, |_|\\___|_.__/ \\___|_|  |_|   \\__, |",
        "               |___/                              |___|",
        "",
        "Hey There! I'm Dingleberry",
        "What can I do for you?",
        "____________________________________________________________",
        "");
    /** Separates consecutive output blocks. */
    private static final String SEPARATOR =
            "____________________________________________________________";

    /** Reads input from the standard input stream. */
    private final Scanner scanner;

    /** Creates a UI bound to standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the startup banner and greeting. */
    public void showWelcome() {
        System.out.println(WELCOME_MESSAGE);
    }

    /**
     * Returns whether there is another line of input to read.
     *
     * @return true when an input line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next raw command line typed by the user.
     *
     * @return the next user input line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the horizontal separator line used to frame each message block.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Prints the current task list, numbered from 1.
     *
     * @param tasks the tasks to display.
     */
    public void showTaskList(final TaskList tasks) {
        showLine();
        System.out.println("Okie, here are your tasks. I counted them twice!");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%d.%s\n", i + 1, tasks.get(i));
        }
        showLine();
    }

    /**
     * Confirms that a task was added and reports the new list size.
     *
     * @param task the added task.
     * @param totalTaskCount the task count after the addition.
     */
    public void showTaskAdded(final Task task, final int totalTaskCount) {
        showLine();
        System.out.println("Okie dokie! I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalTaskCount
            + " tasks in the list.");
        showLine();
    }

    /**
     * Confirms that a task was deleted and reports the new list size.
     *
     * @param task the deleted task.
     * @param totalTaskCount the task count after the deletion.
     */
    public void showTaskDeleted(final Task task, final int totalTaskCount) {
        showLine();
        System.out.println("Woops! I've removed this task before I lost it: "
            + task);
        System.out.println("Now you have " + totalTaskCount
            + " tasks in the list");
        showLine();
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task marked as done.
     */
    public void showTaskMarked(final Task task) {
        showLine();
        System.out.println("Yay! I've marked this task as done. I think:");
        System.out.println("  " + task);
        showLine();
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task marked as not done.
     */
    public void showTaskUnmarked(final Task task) {
        showLine();
        System.out.println("Okie, undoing that little oops:");
        System.out.println("  " + task);
        showLine();
    }

    /**
     * Reports an unrecognized command to the user.
     *
     * @param message the explanation of the invalid command.
     */
    public void showWrongCommandError(final String message) {
        showLine();
        System.out.println(
            "Woops, Dingleberry doesn't know that command: " + message);
        System.out.println("Please try again with the correct command and"
            + " parameters.");
        showLine();
    }

    /**
     * Reports a recognized command with invalid or missing parameters.
     *
     * @param message the explanation of the invalid parameters.
     */
    public void showIncorrectParametersError(final String message) {
        showLine();
        System.out.println(
            "Uh-oh, Dingleberry got tangled in those parameters: " + message);
        System.out.println("Please try again with the correct command and"
            + " parameters.");
        showLine();
    }

    /**
     * Reports that the saved task list could not be loaded from disk.
     *
     * @param message the reason loading failed.
     */
    public void showLoadingError(final String message) {
        showLine();
        System.out.println("Woops, I couldn't load saved tasks (" + message
            + ")."
            + " Starting with an empty list.");
        showLine();
    }

    /**
     * Reports a corrupted record skipped while loading saved tasks.
     *
     * @param message the recovery warning.
     */
    public void showLoadingWarning(final String message) {
        showLine();
        System.out.println("Warning: " + message);
        showLine();
    }

    /**
     * Reports that the task list could not be saved to disk.
     *
     * @param message the reason saving failed.
     */
    public void showSavingError(final String message) {
        showLine();
        System.out.println("Woops, I couldn't save tasks to disk (" + message
            + ").");
        showLine();
    }

    /** Prints the farewell message shown when the program ends. */
    public void showGoodbye() {
        System.out.println("Bya hope to see your berries again!");
    }

    /** Releases the input scanner's resources. */
    public void close() {
        scanner.close();
    }
}
