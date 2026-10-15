package ralph.ui;

import java.util.List;

import ralph.model.Task;

/**
 * Handles user interaction: printing messages and simple prompts.
 */
public class Ui {
    private static final String SEP = "____________________________________________________________";

    /**
     * Prints an ASCII banner welcoming the user.
     */
    private static final String BANNER = """
         ____       _      _        ____    _   _
        |  _ \\     / \\    | |      |  _ \\  | | | |
        | |_) |   / _ \\   | |      | |_) | | |_| |
        |  _ <   / ___ \\  | |___   |  __/  |  _  |
        |_| \\_\\ /_/   \\_\\ |_____|  |_|     |_| |_|
        Hello! I'm Ralph.
        """;

    public void printBanner() {
        System.out.println(BANNER);
    }

    /**
     * Returns the banner text used by the CLI so the GUI can reuse it.
     *
     * @return the banner string
     */
    public String getBanner() {
        return BANNER;
    }

    /**
     * Prints a simple prompt asking the user for input.
     */
    public void printPrompt() {
        System.out.println("What can I do for you?");
    }

    /**
     * Prints a visual separator line used between interactions.
     */
    public void printSeparator() {
        System.out.println(SEP);
    }

    /**
     * Notifies the user that there was an error loading saved tasks.
     */
    public void showLoadingError() {
        System.out.println(" Warning: could not load saved tasks.");
    }

    /**
     * Prints an error message to the user.
     *
     * @param msg the error message to display
     */
    public void showError(String msg) {
        System.out.println(" Oh no! " + msg);
    }

    /**
     * Prints a farewell message when the application is exiting.
     */
    public void showExit() {
        System.out.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Prints the list of tasks to the user. If the list is empty, prints a suitable message.
     *
     * @param tasks the tasks to display
     */
    public void printTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println(" No tasks.");
            return;
        }
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints a filtered list of tasks that match a search keyword.
     *
     * @param tasks the matching tasks to display
     */
    public void showMatchingTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println(" No matching tasks found.");
            return;
        }
        System.out.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Notifies the user that a task was added and shows the current total.
     *
     * @param task  the task that was added
     * @param total the total number of tasks after adding
     */
    public void showAdded(Task task, int total) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + total + " tasks in the list.");
    }

    /**
     * Notifies the user that a task was removed and shows the current total.
     *
     * @param task  the task that was removed
     * @param total the total number of tasks after removal
     */
    public void showRemoved(Task task, int total) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + total + " tasks in the list.");
    }

    /**
     * Notifies the user that a task was marked as done.
     *
     * @param task the task that was marked
     */
    public void showMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /**
     * Notifies the user that a task was marked as not done.
     *
     * @param task the task that was unmarked
     */
    public void showUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }
}
