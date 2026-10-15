package ace.ui;

import java.util.Scanner;

import ace.task.Task;

/**
 * The single place responsible for talking to the user — printing messages and
 * reading input. No other class should call System.out or System.in directly.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner;

    /**
     * Creates a new Ui reading from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads one line of user input, with leading/trailing whitespace trimmed.
     *
     * @return the trimmed line of input.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints the horizontal divider line used to separate one command's output
     * from the next.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints the startup banner: the ASCII-art logo and the welcome message.
     */
    public void showWelcome() {
        String logo = "   ___   _____  _____\n"
                + "  / _ \\ /  __ \\|  ___|\n"
                + " / /_\\ \\| /  \\| |__  \n"
                + " |  _  || |    |  __| \n"
                + " | | | || \\__/\\| |___ \n"
                + " \\_| |_/ \\____/\\____/\n";

        System.out.println("Hello from\n" + logo);
        showLine();
        System.out.println(" Hello! I'm ACE, your personal poker-themed task manager!");
        System.out.println(" Shall I deal you out a few cards to get started?");
        showLine();
    }

    /**
     * Prints the farewell message shown when the user exits with "bye".
     */
    public void showGoodbye() {
        System.out.println("Cashing out! See you again soon!");
    }

    /**
     * Prints an error message, typically the message from a caught
     * {@code AceException}.
     *
     * @param message the error message to show the user.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Prints a confirmation that a task was added, including the task itself and
     * the new total task count.
     *
     * @param task the task that was just added.
     * @param taskType a human-readable name for the task's type (e.g. "To-Do", "Deadline", "Event").
     * @param totalTasks the total number of tasks now in the list.
     */
    public void showTaskAdded(Task task, String taskType, int totalTasks) {
        System.out.println("Great! I've dealt a new " + taskType + " card to your hand!");
        System.out.println(task);
        System.out.printf("You now have a total of %d cards in your hand!%n", totalTasks);
    }

    /**
     * Prints a confirmation that a task was deleted, including the deleted task
     * and the new total task count.
     *
     * @param task the task that was just deleted.
     * @param totalTasks the total number of tasks now in the list.
     */
    public void showTaskDeleted(Task task, int totalTasks) {
        System.out.println("Mucked! This card is now out of your hand!");
        System.out.println(task);
        System.out.printf("You now have a total of %d cards in your hand!%n", totalTasks);
    }
}
