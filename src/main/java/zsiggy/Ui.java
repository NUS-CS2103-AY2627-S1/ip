package zsiggy;

import java.util.Scanner;

import zsiggy.task.Task;

/**
 * Handles all interaction between Zsiggy and the user.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a user interface that reads input from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays Zsiggy's welcome message.
     */
    public void showWelcome() {
        System.out.println("Zsiggy here. Make it quick.");
        System.out.println("What mess do you need me to sort out today?");
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return The user's input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Displays all currently stored tasks.
     *
     * @param tasks The array containing tasks.
     * @param taskCount The number of tasks to display.
     */
    public void showTaskList(Task[] tasks, int taskCount) {
        System.out.println("Fine. Here's what you've dumped on me:");

        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + ". " + tasks[i]);
        }
    }

    /**
     * Displays a message confirming that a task was marked as done.
     *
     * @param task The task that was marked.
     */
    public void showMarkedTask(Task task) {
        System.out.println(
                "Wait, you actually finished something? Wonders never cease."
        );
        System.out.println("Marked this one done:");
        System.out.println(task);
    }

    /**
     * Displays a message confirming that a task was unmarked.
     *
     * @param task The task that was unmarked.
     */
    public void showUnmarkedTask(Task task) {
        System.out.println("Caught you faking it, huh?");
        System.out.println("Whatever, it's unmarked now:");
        System.out.println(task);
    }

    /**
     * Displays information about a deleted task.
     *
     * @param task The deleted task.
     * @param taskCount The number of remaining tasks.
     */
    public void showDeletedTask(Task task, int taskCount) {
        System.out.println("Finally, one less thing cluttering your life:");
        System.out.println(task);
        System.out.println(
                "Now you've got " + taskCount + " task(s) left."
        );
    }

    /**
     * Displays confirmation that a todo was added.
     *
     * @param task The added todo.
     */
    public void showTodoAdded(Task task) {
        System.out.println("Got it. Added to your never-ending pile:");
        System.out.println(task);
    }

    /**
     * Displays confirmation that a deadline was added.
     *
     * @param task The added deadline.
     */
    public void showDeadlineAdded(Task task) {
        System.out.println("Tick-tock. Added this ticking time bomb:");
        System.out.println(task);
    }

    /**
     * Displays confirmation that an event was added.
     *
     * @param task The added event.
     */
    public void showEventAdded(Task task) {
        System.out.println("Locked it into your schedule:");
        System.out.println(task);
    }

    /**
     * Displays an error message.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println("Oi. " + message);
    }

    /**
     * Displays Zsiggy's goodbye message.
     */
    public void showExit() {
        System.out.println("Hmph. Bye. Go drink your green milk tea.");
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Displays tasks that match a search keyword.
     *
     * @param tasks The matching tasks to display.
     */
    public void showFoundTasks(Task[] tasks) {
        System.out.println("Fine. Here are the matching tasks:");

        for (int i = 0; i < tasks.length; i++) {
            System.out.println((i + 1) + ". " + tasks[i]);
        }
    }
}
