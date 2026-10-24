package orbit.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import orbit.task.Task;

/**
 * Reads console input and presents Orbit's user-facing messages.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to the process console.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI connected to the given streams.
     *
     * @param input command input stream
     * @param output response output stream
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Checks whether another command is available.
     *
     * @return true when another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command without changing its meaningful content.
     *
     * @return next input line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Orbit's greeting.
     */
    public void showWelcome() {
        String banner = "  ___       _     _ _   \n"
                + " / _ \\ _ __| |__ (_) |_ \n"
                + "| | | | '__| '_ \\| | __|\n"
                + "| |_| | |  | |_) | | |_ \n"
                + " \\___/|_|  |_.__/|_|\\__|\n";
        showLines(banner, SEPARATOR);
        showGreeting();
        showSeparator();
    }

    /**
     * Displays the greeting shared by both interfaces, without console decoration.
     */
    public void showGreeting() {
        showLines("Hello! I'm Orbit.", "What can I do for you?");
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        showLines("Bye. Hope to see you again soon!");
    }

    /**
     * Displays a task that was added.
     *
     * @param task added task
     * @param taskCount updated task count
     */
    public void showAddedTask(Task task, int taskCount) {
        showLines("Got it. I've added this task:", "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays the complete numbered task list.
     *
     * @param tasks tasks in display order
     */
    public void showTaskList(List<Task> tasks) {
        showNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Displays the tasks whose descriptions match a find command.
     *
     * @param tasks matching tasks in display order
     */
    public void showMatchingTasks(List<Task> tasks) {
        showNumberedTasks("Here are the matching tasks in your list:", tasks);
    }

    /**
     * Displays the complete task list after a chronological sort.
     *
     * @param tasks sorted tasks in display order
     */
    public void showSortedTasks(List<Task> tasks) {
        showNumberedTasks("Here are your tasks sorted chronologically:", tasks);
    }

    private void showNumberedTasks(String heading, List<Task> tasks) {
        showLines(heading);
        for (int i = 0; i < tasks.size(); i++) {
            showLines((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays a task that was marked as done.
     *
     * @param task updated task
     */
    public void showMarkedTask(Task task) {
        showLines("Nice! I've marked this task as done:", "  " + task);
    }

    /**
     * Displays a task that was marked as not done.
     *
     * @param task updated task
     */
    public void showUnmarkedTask(Task task) {
        showLines("OK, I've marked this task as not done yet:", "  " + task);
    }

    /**
     * Displays a task that was deleted.
     *
     * @param task removed task
     * @param taskCount updated task count
     */
    public void showDeletedTask(Task task, int taskCount) {
        showLines("Noted. I've removed this task:", "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays an error that the user can act on.
     *
     * @param message error explanation
     */
    public void showError(String message) {
        showLines("OOPS! " + message);
    }

    /**
     * Displays a separator after a command response.
     */
    public void showSeparator() {
        showLines(SEPARATOR);
    }

    /**
     * Prints any number of message lines in order, preserving the platform's line endings.
     *
     * @param lines message lines to print
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
