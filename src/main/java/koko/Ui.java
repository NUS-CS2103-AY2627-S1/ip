package koko;

import java.util.List;
import java.util.Scanner;

/**
 * Handles interactions between koko.Koko and the user.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a UI using standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays koko.Koko's welcome message.
     */
    public void showWelcome() {
        String banner = " _  __     _          \n"
                + "| |/ /___ | | _____   \n"
                + "| ' // _ \\| |/ / _ \\  \n"
                + "| . \\ (_) |   < (_) | \n"
                + "|_|\\_\\___/|_|\\_\\___/  \n";

        System.out.println(banner);
        System.out.println("Chirp! I'm Koko the Taskbird. What shall we get flying today?");
    }

    /**
     * Reads a command entered by the user.
     *
     * @return the user's command, with leading and trailing whitespace removed
     */
    public String readCommand() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }

        return "bye";
    }

    /**
     * Displays the goodbye message.
     */
    public void showBye() {
        System.out.println("Fly high! Koko will keep the nest cozy. See you soon!");
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks the task list to display
     */
    public void showTasks(TaskList tasks) {
        showDivider();
        System.out.println("Nest check! Here are the tasks in your nest:");

        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + "." + tasks.get(index));
        }

        showDivider();
    }

    /**
     * Displays a message confirming that a task was marked as done.
     *
     * @param task the task that was marked as done
     */
    public void showMarkDone(Task task) {
        showDivider();
        System.out.println("Wing-tastic! This task is now done:");
        System.out.println("  " + task);
        showDivider();
    }

    /**
     * Displays a message confirming that a task was marked as not done.
     *
     * @param task the task that was marked as not done
     */
    public void showMarkNotDone(Task task) {
        showDivider();
        System.out.println("No flap—this task is back in flight:");
        System.out.println("  " + task);
        showDivider();
    }

    /**
     * Displays a message confirming that a task was deleted.
     *
     * @param task the task that was removed
     * @param remainingTasks the number of tasks remaining
     */
    public void showDeletedTask(Task task, int remainingTasks) {
        showDivider();
        System.out.println("Poof! I've sent this task flying from the nest:");
        System.out.println("  " + task);
        System.out.println("Now you have " + remainingTasks + " tasks in the nest.");
        showDivider();
    }

    /**
     * Displays a message confirming that a task was added.
     *
     * @param task the task that was added
     * @param numberOfTasks the number of tasks now in the list
     */
    public void showAddedTask(Task task, int numberOfTasks) {
        showDivider();
        System.out.println("Chirp-chirp! I've tucked this task into your nest:");
        System.out.println("  " + task);
        System.out.println("Now you have " + numberOfTasks + " tasks in the nest.");
        showDivider();
    }

    /**
     * Displays a message confirming that a task was updated.
     *
     * @param task the task after its details were updated
     */
    public void showUpdatedTask(Task task) {
        showDivider();
        System.out.println("Freshly fluffed! I've updated this task:");
        System.out.println("  " + task);
        showDivider();
    }

    /**
     * Displays an error message.
     *
     * @param message the error message
     */
    public void showError(String message) {
        showDivider();
        System.out.println("Squawk! " + message);
        showDivider();
    }

    /**
     * Displays the tasks matching a search keyword.
     *
     * @param tasks the matching tasks to display
     */
    public void showMatchingTasks(List<Task> tasks) {
        showDivider();
        System.out.println("Nest check! Here are the matching tasks in your nest:");

        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + "." + tasks.get(index));
        }

        showDivider();
    }

    /**
     * Displays the standard divider.
     */
    public void showDivider() {
        System.out.println("____________________________________________________________");
    }
}
