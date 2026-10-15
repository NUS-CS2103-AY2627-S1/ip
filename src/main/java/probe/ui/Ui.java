package probe.ui;

import java.util.List;
import java.util.Scanner;

import probe.task.Task;

/**
 * Handles console input and output for the application.
 */
public class Ui {
    private static final String SEPARATOR = "    ____________________________________________________________";
    private static final String BANNER = "____________________________________________________________\n"
            + " ____  ____   ___  ____  _____\n"
            + "|  _ \\|  _ \\ / _ \\| __ )| ____|\n"
            + "| |_) | |_) | | | |  _ \\|  _|\n"
            + "|  __/|  _ <| |_| | |_) | |___\n"
            + "|_|   |_| \\_\\\\___/|____/|_____|\n";
    private final Scanner scanner = new Scanner(System.in);
    /**
     * Returns one command read and trimmed from standard input.
     *
     * @return Trimmed user command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }
    /**
     * Displays the application welcome message.
     */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println("Hello! I'm Probe.");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");
    }
    /**
     * Displays the application exit message.
     */
    public void showGoodbye() {
        System.out.println(SEPARATOR);
        System.out.println("    Bye. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }
    /**
     * Displays an informational or error message.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
    /**
     * Displays all tasks with one-based numbering.
     *
     * @param tasks Tasks to display.
     */
    public void showList(List<Task> tasks) {
        System.out.println(SEPARATOR);
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + ". " + tasks.get(i));
        }
        System.out.println(SEPARATOR);
    }
    /**
     * Displays confirmation after a task is added.
     *
     * @param task Task that was added.
     * @param count Number of tasks after the addition.
     */
    public void showAdded(Task task, int count) {
        System.out.println(SEPARATOR);
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + count + " tasks in the list.");
        System.out.println(SEPARATOR);
    }
    /**
     * Displays confirmation after a task is removed.
     *
     * @param task Task that was removed.
     * @param count Number of tasks after the removal.
     */
    public void showRemoved(Task task, int count) {
        System.out.println(SEPARATOR);
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + count + " tasks in the list.");
        System.out.println(SEPARATOR);
    }
    /**
     * Displays confirmation after a task's completion status changes.
     *
     * @param task Task whose status changed.
     * @param isMarked Whether the task was marked as done.
     */
    public void showUpdated(Task task, boolean isMarked) {
        System.out.println(SEPARATOR);
        System.out.println(isMarked ? "    Nice! I've marked this task as done:"
                : "    OK, I've marked this task as not done yet:");
        System.out.println("       [" + task.getStatusIcon() + "] " + task.getDescription());
        System.out.println(SEPARATOR);
    }

    /**
     * Displays tasks matching a search query with one-based numbering.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatches(List<Task> tasks) {
        System.out.println(SEPARATOR);
        System.out.println("     Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + ". " + tasks.get(i));
        }
        System.out.println(SEPARATOR);
    }
    /**
     * Closes the standard-input scanner.
     */
    public void close() {
        scanner.close();
    }
}
