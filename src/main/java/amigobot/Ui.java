package amigobot;

import java.util.ArrayList;
import java.util.Scanner;

import amigobot.task.Task;
import amigobot.task.TaskList;

/**
 * Handles all interactions with the user: reading input and printing output.
 */
public class Ui {

    private static final String LINE = "~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*";

    private static final String BANNER = "    _          _           ____        _   \n"
            + "   / \\   _ __ (_) __ _  ___| __ )  ___ | |_ \n"
            + "  / _ \\ | '_ \\| |/ _` |/ _ \\  _ \\ / _ \\| __|\n"
            + " / ___ \\| | | | | (_| | (_) | |_) | (_) | |_ \n"
            + "/_/   \\_\\_| |_|_|\\__, |\\___/|____/ \\___/ \\__|\n"
            + "                 |___/                        \n";

    private static final String CAPYBARA =
              "⠀⠀⢀⣀⠤⠿⢤⢖⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n"
            + "⡔⢩⠂⠀⠒⠗⠈⠀⠉⠢⠄⣀⠠⠤⠄⠒⢖⡒⢒⠂⠤⢄⠀⠀⠀⠀\n"
            + "⠇⠤⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠀⠀⠈⠀⠈⠈⡨⢀⠡⡪⠢⡀⠀\n"
            + "⠈⠒⠀⠤⠤⣄⡆⡂⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠢⠀⢕⠱⠀\n"
            + "⠀⠀⠀⠀⠀⠈⢳⣐⡐⠐⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠀⠁⠇\n"
            + "⠀⠀⠀⠀⠀⠀⠀⠑⢤⢁⠀⠆⠀⠀⠀⠀⠀⢀⢰⠀⠀⠀⡀⢄⡜⠀\n"
            + "⠀⠀⠀⠀⠀⠀⠀⠀⠘⡦⠄⡷⠢⠤⠤⠤⠤⢬⢈⡇⢠⣈⣰⠎⠀⠀\n"
            + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⣃⢸⡇⠀⠀⠀⠀⠀⠈⢪⢀⣺⡅⢈⠆⠀⠀\n"
            + "⠀⠀⠀⠀⠀⠀⠀⠶⡿⠤⠚⠁⠀⠀⠀⢀⣠⡤⢺⣥⠟⢡⠃⠀⠀⠀\n"
            + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠀⠀⠀⠀⠀⠀⠀⠀";

    private final Scanner scanner;

    /** Constructs a Ui that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the welcome banner, capybara art, and greeting. */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println(CAPYBARA);
        System.out.println("Hola amigo! I'm AmigoBot, your friendly capybara assistant!");
        System.out.println("Did you know capybaras are the world's largest rodents?");
        System.out.println("Anyway, what can I do for you, compadre?");
        showLine();
    }

    /** Prints the goodbye message. */
    public void showGoodbye() {
        System.out.println("Adios amigo! Hope to see you again soon!");
    }

    /** Prints a decorative separator line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints an error message.
     *
     * @param message the error message to display.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Prints an error message when tasks cannot be loaded from file.
     *
     * @param message the error details.
     */
    public void showLoadingError(String message) {
        System.out.println("Ay caramba! Could not load saved tasks: " + message);
    }

    /**
     * Prints an error message when tasks cannot be saved to file.
     *
     * @param message the error details.
     */
    public void showSavingError(String message) {
        System.out.println("Ay caramba! Could not save tasks: " + message);
    }

    /**
     * Prints a confirmation that a task was added, along with the current task count.
     *
     * @param task the task that was added.
     * @param totalTasks the total number of tasks after adding.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalTasks + " tasks in the list.");
    }

    /**
     * Prints a confirmation that a task was deleted, along with the current task count.
     *
     * @param task the task that was removed.
     * @param totalTasks the total number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int totalTasks) {
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + totalTasks + " tasks in the list.");
    }

    /**
     * Prints a confirmation that a task was marked as done.
     *
     * @param task the task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
    }

    /**
     * Prints a confirmation that a task was marked as not done.
     *
     * @param task the task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
    }

    /**
     * Prints all tasks in the list with their 1-based index numbers.
     *
     * @param tasks the task list to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.getTask(i));
        }
    }

    /**
     * Prints tasks whose descriptions match the search keyword.
     *
     * @param matchingTasks the list of tasks that matched the keyword.
     */
    public void showFoundTasks(ArrayList<Task> matchingTasks) {
        System.out.println("Here are the matching tasks in your list:");
        if (matchingTasks.isEmpty()) {
            System.out.println("No matching tasks found, compadre!");
        } else {
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println((i + 1) + "." + matchingTasks.get(i));
            }
        }
    }

    /**
     * Prints tasks that fall on the given date.
     *
     * @param dateString the formatted date string to display.
     * @param matchingTasks the tasks matching that date.
     */
    public void showTasksOnDate(String dateString, ArrayList<Task> matchingTasks) {
        System.out.println("Here are the tasks on " + dateString + ":");
        if (matchingTasks.isEmpty()) {
            System.out.println("No tasks found on that date, compadre!");
        } else {
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println((i + 1) + "." + matchingTasks.get(i));
            }
        }
    }

    /**
     * Reads the next line of user input.
     *
     * @return the user's input string.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Closes the underlying Scanner. */
    public void close() {
        scanner.close();
    }
}
