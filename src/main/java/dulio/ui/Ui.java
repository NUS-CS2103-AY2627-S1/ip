package dulio.ui;

import java.util.Scanner;

import dulio.task.Task;

/**
 * Handles Dulio's interaction with the console user.
 */
public class Ui {
    private Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the startup greeting and command separator.
     */
    public void showWelcome() {
        System.out.println("____________________________________________________________");
        System.out.println(" ____  _   _ _     ___ ___  \n"
            + "|  _ \\| | | | |   |_ _/ _ \\ \n"
            + "| | | | | | | |    | | | | |\n"
            + "| |_| | |_| | |___ | | |_| |\n"
            + "|____/ \\___/|_____|___\\___/ \n");
        System.out.println("Hello! I'm Dulio.");
        System.out.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Reads the next command, or returns null when input ends.
     *
     * @return The next command, or null at end of input.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        String command = scanner.nextLine();
        showSeparator();
        return command;
    }

    /**
     * Displays a separator line.
     */
    public void showSeparator() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Closes the console input.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Displays the goodbye message and a separator.
     */
    public void showGoodbye() {
        System.out.println(" Bye. Hope to see you again soon!");
        showSeparator();
    }

    /**
     * Displays the current task list.
     *
     * @param listed The preformatted task list.
     */
    public void showList(String listed) {
        System.out.println(" Here are the tasks in your list:");
        if (listed.isEmpty()) {
            System.out.println(" (no tasks)");
        } else {
            System.out.print(listed);
        }
        showSeparator();
    }

    /**
     * Displays the matching tasks from a find search.
     *
     * @param matchingTasks The preformatted matching tasks.
     */
    public void showFind(String matchingTasks) {
        System.out.println(" Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            System.out.println(" (no matching tasks)");
        } else {
            System.out.print(matchingTasks);
        }
        showSeparator();
    }

    /**
     * Displays the error for an invalid task number.
     */
    public void showInvalidIndex() {
        System.out.println(" Invalid task index");
        showSeparator();
    }

    /**
     * Displays confirmation after adding a task.
     *
     * @param task The added task.
     * @param taskCount The resulting task count.
     */
    public void showAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   [" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] " + task);
        System.out.println("   Now you have " + taskCount + " tasks in the list.");
        showSeparator();
    }

    /**
     * Displays confirmation after deleting a task.
     *
     * @param task The deleted task.
     * @param taskCount The resulting task count.
     */
    public void showDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   [" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] " + task);
        System.out.println("   Now you have " + taskCount + " tasks in the list.");
        showSeparator();
    }

    /**
     * Displays confirmation after changing a task's completion state.
     *
     * @param task The updated task.
     * @param marked Whether the task was marked or unmarked.
     */
    public void showMarked(Task task, boolean marked) {
        System.out.println(marked
            ? " Nice! I've marked this task as done:"
            : " OK, I've marked this task as not done yet:");
        System.out.println("   [" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] " + task);
        showSeparator();
    }

    /**
     * Displays a user-facing error message.
     *
     * @param message The error message.
     */
    public void showError(String message) {
        System.out.println(" " + message);
        showSeparator();
    }
}
