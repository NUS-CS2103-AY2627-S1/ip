package remy.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import remy.task.Task;

/**
 * Handles all messages shown to the user by the Remy application.
 *
 * @author LEE-wz
 */
public class Ui {
    /** Divider used to frame messages shown to the user. */
    private static final String MESSAGE_DIVIDER = "____________________________________________________________";

    /** Reads commands entered through the console. */
    private final Scanner scanner;

    /** Receives messages produced by the application. */
    private final PrintStream output;

    /** Creates a user interface that communicates through the standard console. */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates an output-only user interface that writes to the given stream.
     *
     * @param output stream that receives application messages
     */
    public Ui(PrintStream output) {
        this(null, output);
    }

    /**
     * Creates a user interface using the given input reader and output stream.
     *
     * @param scanner reader used to obtain commands, or null for an output-only interface
     * @param output stream that receives application messages
     */
    private Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next command
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This user interface does not accept input.");
        }
        return scanner.nextLine();
    }

    /** Closes the console input reader. */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /** Displays the greeting shown when a chat session begins. */
    public void showGreeting() {
        output.println(MESSAGE_DIVIDER + "\n"
                + """
                         _____                     \s
                        |  __ \\                    \s
                        | |__) | ___ _ __ ___  _   _
                        |  _  / / _ \\ '_ ` _ \\| | | |
                        | | \\ \\|  __/ | | | | | |_| |
                        |_|  \\_\\\\___|_| |_| |_|\\__, |
                                                __/ |
                                               |___/\s
                        Yo! I am Remy the rat from Ratatouille.
                        How can I serve you today? :D

                        """
                + MESSAGE_DIVIDER + "\n");
    }

    /** Displays the farewell shown when a chat session ends. */
    public void showFarewell() {
        output.println(MESSAGE_DIVIDER);
        output.println("Cya. Call me again when you need me!");
        output.println(MESSAGE_DIVIDER + "\n");
    }

    /**
     * Displays an error message produced while handling a command.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        output.println(message);
    }

    /**
     * Displays every task in the task list.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        output.println(MESSAGE_DIVIDER + "\n");
        if (tasks.isEmpty()) {
            output.println("There are no tasks in your list yet.");
        } else {
            output.println("Here are the tasks in your list:");
            showNumberedTasks(tasks);
        }
        output.println(MESSAGE_DIVIDER + "\n");
    }

    /**
     * Displays the tasks whose descriptions match a search keyword.
     *
     * @param tasks matching tasks to display
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.println(MESSAGE_DIVIDER + "\n");
        output.println("Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
        output.println(MESSAGE_DIVIDER + "\n");
    }

    /** Displays tasks with one-based numbering. */
    private void showNumberedTasks(List<Task> tasks) {
        for (int index = 0; index < tasks.size(); index++) {
            output.println((index + 1) + ". " + tasks.get(index));
        }
    }

    /**
     * Displays the confirmation after a task has been added.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks now in the list
     */
    public void showTaskAdded(Task task, int taskCount) {
        String result =
                MESSAGE_DIVIDER + "\n"
                        + "Okay, I have helped you create a task:\n"
                        + task + "\n"
                        + "Now you have " + taskCount + " task(s) in the list. Better hurry before it piles up!\n"
                        + MESSAGE_DIVIDER + "\n";
        output.println(result);
    }

    /**
     * Displays the confirmation after a task has been deleted.
     *
     * @param task the task that was deleted
     * @param taskCount the number of tasks now in the list
     */
    public void showTaskDeleted(Task task, int taskCount) {
        String result =
                MESSAGE_DIVIDER + "\n"
                        + "Okay, I have helped you removed a task, remember to thank me:\n"
                        + task + "\n"
                        + "Now you have " + taskCount + " tasks in the list. Good luck LOL.\n"
                        + MESSAGE_DIVIDER + "\n";
        output.println(result);
    }

    /**
     * Displays the confirmation after a task has been marked as done.
     *
     * @param task the task marked as done
     */
    public void showTaskMarkedAsDone(Task task) {
        output.println(MESSAGE_DIVIDER + "\n");
        output.println(" Nice! I've marked this task as done:\n");
        output.println(task);
        output.println(MESSAGE_DIVIDER + "\n");
    }

    /**
     * Displays the confirmation after a task has been marked as undone.
     *
     * @param task the task marked as undone
     */
    public void showTaskMarkedAsUndone(Task task) {
        output.println(MESSAGE_DIVIDER + "\n");
        output.println(" OK, I've marked this task as not done yet:\n");
        output.println(task);
        output.println(MESSAGE_DIVIDER + "\n");
    }

    /**
     * Displays an actionable error after saving tasks fails.
     *
     * @param message description of the save failure and suggested recovery
     */
    public void showSavingError(String message) {
        output.println(message);
    }

    /**
     * Displays a warning produced while loading saved tasks.
     *
     * @param message description of the load problem and its effect
     */
    public void showLoadingWarning(String message) {
        output.println(message);
    }
}
