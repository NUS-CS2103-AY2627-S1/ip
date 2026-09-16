package kia.ui;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import kia.exception.KiaException;
import kia.task.Task;

/**
 * Handles all console input and output for Kia.
 *
 * <p>Keeping presentation here allows the command-processing code to focus on
 * application behavior instead of formatting messages for the user.</p>
 */
public class Ui {
    private static final String SEPARATOR = "_".repeat(60);

    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out;
    }

    /**
     * Creates a UI that writes messages to the supplied stream.
     *
     * @param output destination for user-facing messages
     */
    public Ui(PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} if another line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the next user-entered command
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the initial banner and welcome message. */
    public void showWelcome() {
        String banner = "██╗  ██╗██╗ █████╗\n"
                + "██║ ██╔╝██║██╔══██╗\n"
                + "█████╔╝ ██║███████║\n"
                + "██╔═██╗ ██║██╔══██║\n"
                + "██║  ██╗██║██║  ██║\n"
                + "╚═╝  ╚═╝╚═╝╚═╝  ╚═╝";
        showSeparator();
        output.println(banner);
        output.println("Heyo! I'm Kia.");
        output.println("What can I do for you?");
        showSeparator();
    }

    /** Prints the separator used between chatbot messages. */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /** Prints Kia's playful response to a recognised dog command. */
    public void showPetResponse(String response) {
        output.println(response);
    }

    /** Prints the farewell message. */
    public void showBye() {
        output.println("Aww, goodbye. Hope to see you again soon!");
        showSeparator();
    }

    /**
     * Prints an error returned while processing a command.
     *
     * @param exception the command error to display
     */
    public void showError(KiaException exception) {
        output.println("Hey!!! " + exception.getMessage());
    }

    /**
     * Prints the loading error returned during startup.
     *
     * @param exception the loading error to display
     */
    public void showLoadingError(KiaException exception) {
        showError(exception);
    }

    /**
     * Prints all tasks with their one-based list positions.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(ArrayList<Task> tasks) {
        output.println("Here ya go! These are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints tasks whose descriptions contain the supplied keyword.
     *
     * @param tasks the tasks to search
     * @param keyword the keyword to match
     */
    public void showMatchingTasks(ArrayList<Task> tasks, String keyword) {
        output.println("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).matchesDescription(keyword)) {
                output.println((i + 1) + "." + tasks.get(i));
            }
        }
    }

    /**
     * Prints confirmation for a newly added task.
     *
     * @param task the newly added task
     * @param taskCount the number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Gotcha! I've added this task:");
        output.println("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Prints confirmation for a deleted task.
     *
     * @param task the deleted task
     * @param taskCount the number of tasks after the deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Okies. I've removed this task:");
        output.println("  " + task);
        showTaskCount(taskCount);
    }

    /**
     * Prints confirmation that a task was marked done.
     *
     * @param task the task that was marked done
     */
    public void showTaskMarked(Task task) {
        output.println("Yay! I've marked this task as done:");
        output.println("  " + task);
    }

    /**
     * Prints confirmation that a task was marked not done.
     *
     * @param task the task that was marked not done
     */
    public void showTaskUnmarked(Task task) {
        output.println("Golly! I've marked this task as not done yet:");
        output.println("  " + task);
    }

    /**
     * Prints confirmation that a task was updated.
     *
     * @param task the updated task
     */
    public void showTaskUpdated(Task task) {
        output.println("Got it! I've updated this task:");
        output.println("  " + task);
    }

    /**
     * Prints that an update did not alter a task.
     *
     * @param task the unchanged task
     */
    public void showTaskUnchanged(Task task) {
        output.println("Nothing changed. This task is already up to date:");
        output.println("  " + task);
    }

    /**
     * Prints the task-count confirmation using the singular or plural form.
     *
     * @param taskCount the number of tasks currently stored
     */
    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println("Alright, now you have " + taskCount + " " + taskWord + " in the list.");
    }
}
