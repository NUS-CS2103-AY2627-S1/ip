package duck.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import duck.task.Task;

/**
 * Handles all console input and output for the chatbot.
 */
public class Ui {
    /** Horizontal divider used to separate chatbot responses. */
    private static final String SEPARATOR =
            "____________________________________________________________";

    /** Duck's startup logo. */
    private static final String BANNER = " ____             _    \n"
            + "|  _ \\ _   _  ___| | __\n"
            + "| | | | | | |/ __| |/ /\n"
            + "| |_| | |_| | (__|   < \n"
            + "|____/ \\__,_|\\___|_|\\_\\\n";

    /** Greeting shared by the console and graphical interfaces. */
    private static final String GREETING = "Quack! I'm Duck.\n"
            + "What shall we get done today?";

    /** Command summary shown when the user asks Duck for help. */
    private static final String COMMAND_GUIDE = "Here are the commands I can help with:\n"
            + "  help\n"
            + "  todo DESCRIPTION\n"
            + "  deadline DESCRIPTION /by DATE\n"
            + "  event DESCRIPTION /from START /to END\n"
            + "  list\n"
            + "  find KEYWORD\n"
            + "  mark NUMBER\n"
            + "  unmark NUMBER\n"
            + "  delete NUMBER\n"
            + "  bye";

    /** Reads commands from the console. */
    private final Scanner scanner;

    /** Destination for chatbot output. */
    private final PrintStream output;

    /** Creates a UI connected to the standard input stream. */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied input and output streams.
     *
     * @param input Source of user commands.
     * @param output Destination for chatbot messages.
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(Objects.requireNonNull(input));
        this.output = Objects.requireNonNull(output);
    }

    /** Shows the startup logo and greeting. */
    public void showWelcome() {
        showLines(SEPARATOR, BANNER, GREETING, SEPARATOR);
    }

    /**
     * Returns the greeting used at the start of a Duck session.
     *
     * @return Duck's greeting without console decoration
     */
    public static String getGreeting() {
        return GREETING;
    }

    /**
     * Returns whether another command is available from the console.
     *
     * @return true when another command can be read
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads and normalizes the next command.
     *
     * @return next command with surrounding whitespace removed
     */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /** Shows the divider between chatbot responses. */
    public void showSeparator() {
        this.output.println(SEPARATOR);
    }

    /** Shows the farewell message. The command loop prints the closing divider. */
    public void showGoodbye() {
        this.output.println("Goodbye! Keep your ducks in a row!");
    }

    /** Shows the commands that Duck understands. */
    public void showHelp() {
        this.output.println(COMMAND_GUIDE);
    }

    /**
     * Shows all tasks with one-based list numbers.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            this.output.println("The pond is clear—there are no tasks yet.");
            return;
        }
        this.output.println("Here are the tasks in your pond:");
        showNumberedTasks(tasks);
    }

    /**
     * Shows tasks whose descriptions match a find keyword, preserving their full-list numbers.
     *
     * @param allTasks All tasks in their full-list order.
     * @param matchingTasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> allTasks, List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            this.output.println("No matching tasks surfaced in the pond.");
            return;
        }
        this.output.println("Here are the matching tasks in your pond:");
        for (Task matchingTask : matchingTasks) {
            int taskIndex = allTasks.indexOf(matchingTask);
            if (taskIndex < 0) {
                throw new IllegalArgumentException("Matching tasks must belong to the full task list.");
            }
            this.output.println((taskIndex + 1) + "." + matchingTask);
        }
    }

    /** Shows tasks with one-based numbers relative to the supplied list. */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            this.output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        showLines(
                "✓ Nicely done! I've marked this task as complete:",
                "  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        showLines(
                "✓ No problem—this task is active again:",
                "  " + task);
    }

    /**
     * Shows confirmation and the new task count after adding a task.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLines(
                "✓ Got it—this task is now under my wing:",
                String.valueOf(task),
                getTaskCountMessage(taskCount));
    }

    /**
     * Shows confirmation and the new task count after deleting a task.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showLines(
                "✓ Removed! That task has left the pond:",
                "  " + task,
                getTaskCountMessage(taskCount));
    }

    /**
     * Shows an error without exposing exception-handling details to this class.
     *
     * @param message User-facing explanation of the error.
     */
    public void showError(String message) {
        this.output.println("Quack? " + message);
    }

    /** Returns a grammatically correct summary of the number of saved tasks. */
    private String getTaskCountMessage(int taskCount) {
        String taskNoun = taskCount == 1 ? "task" : "tasks";
        return "You now have " + taskCount + " " + taskNoun + " in your pond.";
    }

    /**
     * Shows each supplied line in order.
     *
     * @param lines Lines to show.
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            this.output.println(line);
        }
    }
}
