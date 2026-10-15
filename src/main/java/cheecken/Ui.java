package cheecken;

import java.util.List;
import java.util.function.Consumer;

/**
 * Handles all user-facing console output.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";

    private final Consumer<String> output;

    /**
     * Creates the console output adapter.
     */
    public Ui() {
        this(System.out::println);
    }

    /**
     * Sends formatted messages to the supplied output destination.
     * @param output destination for formatted output messages
     */
    public Ui(Consumer<String> output) {
        this.output = output;
    }

    /**
     * Prints the welcome banner.
     */
    public void showWelcome() {
        output.accept(" _____ _                    _              \n"
                + "/  __ \\ |                  | |             \n"
                + "| /  \\/ |__   ___  ___  ___| | _____ _ __  \n"
                + "| |   | '_ \\ / _ \\/ _ \\/ __| |/ / _ \\ '_ \\ \n"
                + "| \\__/\\ | | |  __/  __/ (__|   <  __/ | | |\n"
                + " \\____/_| |_|\\___|\\___|\\___|_|\\_\\___|_| |_|\n"
                + SEPARATOR + "\nHello! I'm \033[3mCHEECKEN\033[0m.\n"
                + "What can I do for you?\n" + SEPARATOR);
    }

    /**
     * Prints the farewell message.
     */
    public void showBye() {
        output.accept(SEPARATOR + "\nBye. Hope to see you again soon!\n" + SEPARATOR);
    }

    /**
     * Prints the current task list.
     * @param tasks tasks in display or storage order
     */
    public void showList(List<Task> tasks) {
        output.accept(SEPARATOR + "\nHere are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.accept((i + 1) + "." + tasks.get(i));
        }
        output.accept(SEPARATOR);
    }

    /**
     * Displays tasks matching a search keyword, or a no-match message.
     * @param matches matching tasks in display order
     */
    public void showFind(List<Task> matches) {
        output.accept(SEPARATOR);
        if (matches.isEmpty()) {
            output.accept("There are no matching tasks in your list.");
        } else {
            output.accept("Here are the matching tasks in your list:");
            for (int i = 0; i < matches.size(); i++) {
                output.accept((i + 1) + "." + matches.get(i));
            }
        }
        output.accept(SEPARATOR);
    }

    /**
     * Prints a task-created confirmation.
     * @param task task to process
     * @param count number of tasks after the addition
     */
    public void showAdded(Task task, int count) {
        output.accept(SEPARATOR + "\nGot it. I've added this task:\n  " + task
                + "\nNow you have " + count + " tasks in the list.\n" + SEPARATOR);
    }

    /**
     * Prints a task status or deletion confirmation.
     * @param message message to display or append
     * @param task task to process
     */
    public void showTaskMessage(String message, Task task) {
        output.accept(SEPARATOR + "\n" + message + "\n  " + task + "\n" + SEPARATOR);
    }

    /**
     * Prints a formatted error message.
     * @param exception exception whose message is displayed
     */
    public void showError(Exception exception) {
        output.accept(SEPARATOR);
        output.accept(exception.getMessage());
    }
}
