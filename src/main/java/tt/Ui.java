package tt;

import java.util.List;
import java.util.Scanner;

import tt.task.Task;

/**
 * Handles text-based communication between tt.TT and the user.
 */
public class Ui {
    private static final String DIVIDER = "________________________________________ \n";
    private static final String BANNER = """
                         --------  --------
                             -        -
                             -        -
                             -        -
                             -        -
                             -        -
                        """;
    private static final String MESSAGE_ADD_TASK = "Got it. I've added this task: \n";
    private static final String MESSAGE_BYE = "Bye. Hope to see you again soon!";
    private final Scanner scanner;

    /**
     * Creates a user interface connected to standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the initial greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm tt.TT.\nWhat can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Reads one complete command from the user.
     *
     * @return command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the current tasks.
     *
     * @param tasks tasks to display.
     */
    public void showTasks(List<Task> tasks) {
        showTaskList(tasks, "Here are the tasks in your list: ");
    }

    /**
     * Displays the tasks whose descriptions contain the search keyword.
     *
     * @param tasks matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        showTaskList(tasks, "Here are the matching tasks in your list:");
    }

    private void showTaskList(List<Task> tasks, String heading) {
        System.out.println(DIVIDER + heading);
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        showDivider();
    }

    /**
     * Displays the farewell message and closes standard input.
     */
    public void showBye() {
        System.out.println(DIVIDER + MESSAGE_BYE);
        showDivider();
        scanner.close();
    }

    /**
     * Displays confirmation that a task was marked or unmarked.
     *
     * @param task task whose completion state changed.
     * @param isDone whether the task is now complete.
     */
    public void showMarked(Task task, boolean isDone) {
        String message = isDone
                ? "Nice! I've marked this task as done: \n"
                : "OK, I've marked this task as not done yet: \n";
        System.out.println(DIVIDER + message + task + "\n" + DIVIDER);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param remaining number of tasks remaining.
     */
    public void showDeleted(Task task, int remaining) {
        System.out.println(DIVIDER + "Noted. I've removed this task:\n  " + task
                + "\nNow you have " + remaining + " tasks in the list.\n" + DIVIDER);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added.
     * @param total total number of tasks.
     */
    public void showAdded(Task task, int total) {
        System.out.println(DIVIDER + MESSAGE_ADD_TASK + "  " + task
                + "\nNow you have " + total + " tasks in the list. \n" + DIVIDER);
    }

    /**
     * Displays an error message without terminating the application.
     *
     * @param message error explanation to display.
     */
    public void showError(String message) {
        System.out.println(DIVIDER + message + "\n" + DIVIDER);
    }

    /**
     * Displays the standard output separator.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }
}
