package downtowngurl.ui;

import java.io.PrintStream;
import java.util.Scanner;

import downtowngurl.task.Task;
import downtowngurl.task.TaskList;

/**
 * Handles text interactions with the user.
 */
public class Ui {
    private static final String CHATBOT_NAME = "Downtown Gurl";
    private static final String DIVIDER = "<*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*><*>";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI helper that reads commands from standard input.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a UI helper that writes output to the given stream.
     *
     * @param output Stream used to show messages.
     */
    public Ui(PrintStream output) {
        this.scanner = new Scanner(System.in);
        this.output = output;
    }

    /**
     * Prints the greeting shown when the chatbot starts.
     */
    public void showWelcome() {
        String banner = """
                 ____                      _                       ____           __\s
                |  _ \\  _____      ___ __ | |_ _____      ___ __  / ___|_   _ _ __| |
                | | | |/ _ \\ \\ /\\ / / '_ \\| __/ _ \\ \\ /\\ / / '_ \\| |___| | | ' __|| |
                | |_| | (_) \\ V  V /| | | | || (_) \\ V  V /| | | | |_| | |_| | |  | |
                |____/ \\___/ \\_/\\_/ |_| |_|\\__\\___/ \\_/\\_/ |_| |_|\\____|\\__,_|_|  |_|""";
        this.output.println(DIVIDER);
        this.output.println(banner);
        this.output.println(DIVIDER);
        this.output.println("Hey I'm " + CHATBOT_NAME + ".");
        this.output.println("I'm here to give you a reality check "
                + "and help you manifest that life you've been dreaming.");
        this.output.println(DIVIDER);
        this.output.println("Darling what's up?");
    }

    /**
     * Returns whether the user has another command ready.
     *
     * @return true if another command can be read, false otherwise.
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     *
     * @return Full command entered by the user.
     */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /**
     * Prints the goodbye message shown when the chatbot exits.
     */
    public void showGoodbye() {
        this.output.println("That's bombz. Byes!");
    }

    /**
     * Prints all tasks currently in the list.
     *
     * @param tasks Current task list.
     */
    public void showTaskList(TaskList tasks) {
        tasks.sortByDate();
        this.output.println("Here's your tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            this.output.println(" " + (i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Prints the tasks that matched a find command.
     *
     * @param tasks Matching tasks.
     */
    public void showMatchingTasks(TaskList tasks) {
        if (tasks.size() == 0) {
            this.output.println("No matching tasks found, bestie.");
            return;
        }
        this.output.println("Here are the matching tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            this.output.println(" " + (i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Prints the result of marking or unmarking a task.
     *
     * @param message Confirmation message.
     * @param task Updated task.
     */
    public void showUpdatedTask(String message, Task task) {
        this.output.println(message);
        this.output.println("  " + task);
    }

    /**
     * Prints a confirmation for the newly added task.
     *
     * @param addedTask Task that was added.
     * @param taskCount Number of tasks after adding the task.
     */
    public void showAddedTask(Task addedTask, int taskCount) {
        this.output.println("Gotcha. Noted it downz:");
        this.output.println("  " + addedTask);
        this.output.println("Now you got " + taskCount + " tasks in the roster.");
    }

    /**
     * Prints a confirmation for the deleted task.
     *
     * @param removedTask Task that was removed.
     * @param taskCount Number of tasks after removing the task.
     */
    public void showDeletedTask(Task removedTask, int taskCount) {
        this.output.println("Sure~ I've removed this task:");
        this.output.println("  " + removedTask);
        this.output.println("Now you got " + taskCount + " tasks in the list.");
    }

    /**
     * Prints an error message using the same divider style as normal chatbot replies.
     *
     * @param message Error message to show.
     */
    public void showError(String message) {
        this.output.println(message);
    }

    /**
     * Prints a divider line between chatbot interactions.
     */
    public void showLine() {
        this.output.println(DIVIDER);
    }
}
