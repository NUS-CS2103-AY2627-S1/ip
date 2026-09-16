package bobby;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/** Handles all console input and output for Bobby. */
public class Ui {
    /** Greeting shared by the console and graphical interfaces. */
    static final String WELCOME_MESSAGE = "Hiya! I'm Bobby, your cheerfully goofy task buddy."
            + System.lineSeparator()
            + "Throw me a task and I'll keep it from wandering off!";

    /** Separator printed between chatbot interactions. */
    private static final String SEPARATOR = "____________________________________________________________";
    /** ASCII-art banner printed at startup. */
    private static final String BANNER = " ____   ____  ____  ____  __   __\n"
            + "| __ ) / __ \\| __ )| __ ) \\ \\ / /\n"
            + "|  _ \\| |  | |  _ \\|  _ \\  \\ V /\n"
            + "| |_) | |__| | |_) | |_) |   | |\n"
            + "|____/ \\____/|____/|____/    |_|\n";

    /** Input source for user commands. */
    private final Scanner scanner;

    /** Output destination for messages shown by Bobby. */
    private final PrintStream output;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this(System.out);
    }

    /** Creates a user interface that writes messages to the given destination. */
    Ui(PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /** Shows Bobby's greeting and command prompt. */
    public void showWelcome() {
        showSeparator();
        output.print(BANNER);
        output.println(WELCOME_MESSAGE);
        showSeparator();
    }

    /**
     * Reads and trims the next command, or returns {@code null} when input ends.
     *
     * @return the next trimmed command, or {@code null} when input ends
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    /** Shows the visual separator used between interactions. */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /**
     * Shows an error message to the user.
     *
     * @param message error text to display
     */
    public void showError(String message) {
        output.println(message);
    }

    /**
     * Shows all tasks in their current order.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(TaskList tasks) {
        output.println("Ta-da! Here's your task parade:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks tasks found by the search, in their original order
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        output.println("Detective Bobby found these matching tasks:");
        if (matchingTasks.isEmpty()) {
            output.println("I checked under the couch. No matching tasks found!");
            return;
        }
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Boop! I've tucked this task into the list:");
        output.println(task);
        output.println("Bobby's task count is now " + taskCount + ".");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task task that was marked done
     */
    public void showTaskMarkedDone(Task task) {
        output.println("Woohoo! This task is officially done:");
        output.println("  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task task that was marked incomplete
     */
    public void showTaskMarkedNotDone(Task task) {
        output.println("Plot twist! This task is back in action:");
        output.println("  " + task);
    }

    /**
     * Shows confirmation that a task's priority was updated.
     *
     * @param task task whose priority was updated
     */
    public void showTaskPriorityUpdated(Task task) {
        output.println("Beep boop! I've updated this task's priority:");
        output.println("  " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task task that was removed
     * @param taskCount number of tasks after the removal
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Poof! This task has left the building:");
        output.println(task);
        output.println("Bobby's task count is now " + taskCount + ".");
    }

    /** Shows Bobby's farewell message. */
    public void showGoodbye() {
        output.println("Toodles! Bobby is off to recharge the silly batteries.");
        showSeparator();
    }
}
