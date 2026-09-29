package pavanmaxxer;

import java.util.List;
import java.util.Scanner;

/**
 * Handles all command-line input and output for Pavanmaxxer.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a terminal UI connected to standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next complete command line.
     *
     * @return Next raw command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the application greeting.
     */
    public void showWelcome() {
        System.out.println("Hello! I'm Pavanmaxxer.");
        System.out.println("What can I do for you?");
    }

    /**
     * Shows the application farewell.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Shows a response produced by the application core.
     *
     * @param response User-facing response text.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Shows tasks as a one-based numbered list.
     *
     * @param tasks Tasks to show.
     */
    public void showTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows the result of marking or unmarking a task.
     *
     * @param task Updated task.
     * @param isDone Whether the task was marked as completed.
     */
    public void showMarkedTask(Task task, boolean isDone) {
        if (isDone) {
            System.out.println("Nice! I've marked this task as done:");
        } else {
            System.out.println("OK, I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
    }

    /**
     * Shows the deleted task and remaining task count.
     *
     * @param task Deleted task.
     * @param taskCount Number of remaining tasks.
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows the added task and updated task count.
     *
     * @param task Added task.
     * @param taskCount Updated number of tasks.
     */
    public void showAddedTask(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows tasks that match a search keyword.
     *
     * @param tasks Matching tasks in display order.
     */
    public void showMatchingTasks(List<Task> tasks) {
        System.out.println("Here are the matching tasks in your list:");
        showTasks(tasks);
    }

    /**
     * Shows a user-facing error message.
     *
     * @param message Error explanation.
     */
    public void showError(String message) {
        System.out.println("OOPS!!! " + message);
    }
}
