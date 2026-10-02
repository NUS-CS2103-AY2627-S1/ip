package floppy;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input and output for the chatbot.
 */
public class Ui implements AutoCloseable {
    private static final int CARROT_REMARK_INTERVAL = 3;
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " _____ _                         \n"
            + "|  ___| | ___  _ __  _ __  _   _\n"
            + "| |_  | |/ _ \\| '_ \\| '_ \\| | | |\n"
            + "|  _| | | (_) | |_) | |_) | |_| |\n"
            + "|_|   |_|\\___/| .__/| .__/ \\__, |\n"
            + "               |_|   |_|    |___/ \n";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a console interface connected to standard input and output.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Returns whether another command is available from the input stream.
     *
     * @return {@code true} when another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next complete command from the input stream.
     *
     * @return Next line entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the chatbot banner and greeting.
     */
    public void showWelcome() {
        output.println(BANNER);
        output.println(getWelcomeMessage());
        showLine();
    }

    /**
     * Returns the greeting shared by the console and graphical interfaces.
     *
     * @return Floppy's welcome message.
     */
    static String getWelcomeMessage() {
        return "Hello! I'm Floppy.\n"
                + "I keep tasks tidy and carrots close.\n"
                + "What can I do for you?";
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        output.println(" Bye. Hope to see you again soon!");
        output.println(" I'll be nibbling a carrot until then.");
    }

    /**
     * Displays the standard output divider.
     */
    public void showLine() {
        output.println(DIVIDER);
    }

    /**
     * Displays all tasks with one-based list numbers.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        output.println(" Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays tasks that matched a find command.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.println(" Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
    }

    /**
     * Displays tasks with one-based numbers relative to the supplied list.
     *
     * @param tasks Tasks to display.
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(" Okies! I've added this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
        if (taskCount % CARROT_REMARK_INTERVAL == 0) {
            output.println(" That's a tidy little crop of tasks. Time for a carrot!");
        }
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task Removed task.
     * @param taskCount Number of tasks after the removal.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(" Noted. I've removed this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task was marked as completed.
     *
     * @param task Updated task.
     */
    public void showTaskMarked(Task task) {
        output.println(" Nice! I've marked this task as done:");
        output.println("   " + task);
        output.println(" That deserves a celebratory carrot!");
    }

    /**
     * Displays confirmation that a task was marked as not completed.
     *
     * @param task Updated task.
     */
    public void showTaskUnmarked(Task task) {
        output.println(" OK, I've marked this task as not done yet:");
        output.println("   " + task);
    }

    /**
     * Displays an application error without terminating the chatbot.
     *
     * @param message Explanation of the error.
     */
    public void showError(String message) {
        output.println(" Oops! " + message);
    }

    @Override
    public void close() {
        scanner.close();
    }
}
