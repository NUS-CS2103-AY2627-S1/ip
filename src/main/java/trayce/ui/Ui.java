package trayce.ui;

import java.util.List;
import java.util.Scanner;

import trayce.task.Task;

/** Handles all console input and output for the Trayce application. */
public class Ui {
    private static final String SEPARATOR = "_".repeat(80);
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows Trayce's welcome banner and greeting. */
    public void showWelcome() {
        String banner = " _____\n"
                + "|_   _| __ __ _ _   _  ___ ___\n"
                + "  | |  '__/ _` | | | |/ __/ _ \\\n"
                + "  | | | | | (_| | |_| | (_|  __/\n"
                + "  |_| |_|  \\__,_|\\__, |\\___\\___|\n"
                + "                  __/ |\n"
                + "                 |___/\n";
        System.out.println(banner);
        System.out.println("Hello! I'm Trayce. What can I do for you?");
    }

    /**
     * Reads one line entered by the user from standard input.
     *
     * @return the complete line entered by the user as a string
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Shows a horizontal divider line to separate user output. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Shows the goodbye message to the user upon exiting the application. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Shows all tasks in their current order.
     *
     * @param tasks the list of tasks to be displayed to the user
     */
    public void showTaskList(List<Task> tasks) {
        showTasks("Here are the tasks in your list:", tasks.toArray(Task[]::new));
    }

    /**
     * Shows confirmation that a task was successfully added.
     *
     * @param task the newly added task object
     * @param taskCount the total number of tasks now present in the task list
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  [" + task.getTypeIcon() + "][ ] " + task.getDescription()
                + task.getDateTimeDetails());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows confirmation that a task was successfully deleted.
     *
     * @param task the deleted task object
     * @param taskCount the total number of tasks remaining in the task list
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  [" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] "
                + task.getDescription() + task.getDateTimeDetails());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows confirmation that a task's completion status has changed (marked/unmarked).
     *
     * @param task the task whose completion status was updated
     * @param isDone {@code true} if marked as done, {@code false} if marked as not done
     */
    public void showTaskMarked(Task task, boolean isDone) {
        String message = isDone ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        System.out.println(message);
        System.out.println("  [" + task.getStatusIcon() + "] " + task.getDescription());
    }

    /** Shows an error message indicating that the task number supplied was invalid. */
    public void showInvalidTaskNumber() {
        System.out.println("Please provide a valid task number.");
    }

    /** Shows an error message indicating that the task number provided does not exist in the list. */
    public void showMissingTaskNumber() {
        System.out.println("That task number does not exist.");
    }

    /**
     * Shows an error message indicating that a task-creation command format was invalid.
     * Handles specific error messages for incomplete "todo" commands.
     *
     * @param command the original invalid user command
     */
    public void showInvalidTaskCommand(String command) {
        if (command.equalsIgnoreCase("todo")) {
            System.out.println("Please provide a proper action to do.");
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    /** Shows an error message indicating that no more tasks can be added as capacity has been reached. */
    public void showTaskLimitReached() {
        System.out.println("I cannot store any more tasks.");
    }

    /** Shows an error message indicating that saved tasks could not be loaded from storage. */
    public void showLoadError() {
        System.out.println("I could not load your saved tasks. Starting with an empty list.");
    }

    /** Shows an error message indicating that tasks could not be saved to disk. */
    public void showSaveError() {
        System.out.println("I could not save your tasks.");
    }

    /** Shows tasks matching a keyword. */
    public void showMatchingTasks(List<Task> tasks) {
        showTasks("Here are the matching tasks in your list:", tasks.toArray(Task[]::new));
    }

    /** Displays a heading followed by any number of tasks. */
    private void showTasks(String heading, Task... tasks) {
        System.out.println(heading);
        for (int i = 0; i < tasks.length; i++) {
            Task task = tasks[i];
            System.out.println((i + 1) + ".[" + task.getTypeIcon() + "][" + task.getStatusIcon() + "] "
                    + task.getDescription() + task.getDateTimeDetails());
        }
    }
}
