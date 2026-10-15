package gnaix;

import java.util.Scanner;

import gnaix.task.Task;
import gnaix.task.TaskList;

/**
 * Handles interactions with the user.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";

    private static final String BANNER =
            "  ____ _   _    _    _____  __\n"
                    + " / ___| \\ | |  / \\  |_ _\\ \\/ /\n"
                    + "| |  _|  \\| | / _ \\  | | \\  /\n"
                    + "| |_| | |\\  |/ ___ \\ | | /  \\\n"
                    + " \\____|_| \\_/_/   \\_\\___/_/\\_\\";

    private final Scanner scanner;

    /**
     * Creates a user interface backed by standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the application welcome message.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello. I'm Gnaix.");
        System.out.println("What do you need?");
    }

    /**
     * Reads a command from standard input.
     *
     * @return User-entered command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the standard separator line.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a message to the user.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays a message confirming that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showMessages(
                "Fine. I've added this:",
                "  " + task,
                "You now have " + taskCount + " tasks.");
    }

    /**
     * Displays a message confirming that a task was marked as complete.
     *
     * @param task Task that was marked as complete.
     */
    public void showTaskCompleted(Task task) {
        showMessages(
                "There. It's done:",
                "  " + task);
    }

    /**
     * Displays a message confirming that a task was marked as incomplete.
     *
     * @param task Task that was marked as incomplete.
     */
    public void showTaskUncompleted(Task task) {
        showMessages(
                "Apparently we're undoing that.",
                "  " + task);
    }

    /**
     * Displays a message confirming that a task was deleted.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of tasks remaining after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showMessages(
                "Gone. I've removed this:",
                "  " + task,
                "You now have " + taskCount + " tasks left.");
    }

    /**
     * Displays all tasks in the task list with their corresponding task numbers.
     *
     * @param tasks Task list to display.
     */
    public void showTasks(TaskList tasks) {
        System.out.println("Here. Your current list:");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Displays the application goodbye message.
     */
    public void showGoodbye() {
        System.out.println(SEPARATOR);
        System.out.println("All right. Goodbye.");
        System.out.println("Try not to make more work for me.");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints each of the given messages on a separate line.
     *
     * @param messages Messages to print.
     */
    private void showMessages(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

}
