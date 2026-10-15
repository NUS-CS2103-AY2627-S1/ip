package bob;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import bob.task.Task;

/**
 * Handles all console input and output for Bob.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final PrintStream output;
    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.output = System.out;
    }

    /**
     * Creates a response-only user interface that writes to the given stream.
     *
     * @param output destination for Bob's response
     */
    public Ui(PrintStream output) {
        this.scanner = null;
        this.output = output;
    }

    /**
     * Displays Bob's greeting when the application starts.
     */
    public void showWelcome() {
        String banner = " ____        _     \n"
                + "| __ )  ___ | |__  \n"
                + "|  _ \\ / _ \\| '_ \\ \n"
                + "| |_) | (_) | |_) |\n"
                + "|____/ \\___/|_.__/ \n";

        output.println(banner);
        output.println("hello im bob !!");
        output.println("how can i help :)");
        showDivider();
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} when standard input has another line
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and removes surrounding whitespace.
     *
     * @return the next user command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays Bob's farewell.
     */
    public void showGoodbye() {
        output.println("  yippee glad to have helped (＠＾◡＾)");
        output.println("  byebye !! have a good day (๑˃ᴗ˂)ﻭ");
    }

    /**
     * Displays the current tasks with one-based numbering.
     *
     * @param tasks tasks to display
     */
    public void showTasks(List<Task> tasks) {
        output.println(" here are your tasks (⌒‿⌒) 加油 !! :");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays matching tasks using their numbers in the full task list.
     *
     * @param matchingTasks matching tasks to display
     * @param allTasks full task list used by mark, unmark, and delete commands
     */
    public void showFoundTasks(List<Task> matchingTasks, List<Task> allTasks) {
        output.println(" here's what i found:");
        for (Task task : matchingTasks) {
            int taskNumber = allTasks.indexOf(task) + 1;
            output.println(" " + taskNumber + "." + task);
        }
    }

    /**
     * Displays confirmation that a task was marked complete.
     *
     * @param task task that was marked
     */
    public void showMarked(Task task) {
        output.println(" yippee task done, i've marked it as so:");
        output.println("   " + task);
    }

    /**
     * Displays confirmation that a task was marked incomplete.
     *
     * @param task task that was unmarked
     */
    public void showUnmarked(Task task) {
        output.println(" okie, i've marked this task incomplete:");
        output.println("   " + task);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task deleted task
     * @param remainingTaskCount number of tasks left
     */
    public void showDeleted(Task task, int remainingTaskCount) {
        output.println(" okays here's the task i deleted: ");
        output.println("   " + task);
        output.println(" pls get to the remaining " + remainingTaskCount + " tasks in your list");
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount new number of tasks
     */
    public void showAdded(Task task, int taskCount) {
        output.println(" okays task added:");
        output.println("   " + task);
        output.println(" you now have " + taskCount + " tasks in the list, get to it !!");
    }

    /**
     * Displays an error message to the user.
     *
     * @param message explanation of the problem
     */
    public void showError(String message) {
        output.println(" oopsies !! (´ ∀ ` *) " + message);
    }

    /**
     * Displays the separator between command responses.
     */
    public void showDivider() {
        output.println(DIVIDER);
    }

    /**
     * Releases the input scanner when Bob exits.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
