package caitlyn;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Handles Caitlyn's input and output for either the command line or graphical interface.
 */
public final class Ui {
    /** The separator printed around the application's conversations. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** The banner shown when Caitlyn starts. */
    private static final String BANNER = "  ____      _ _   _             \n"
            + " / ___|__ _(_) |_| |_   _ _ __  \n"
            + "| |   / _` | | __| | | | | '_ \\ \n"
            + "| |__| (_| | | |_| | |_| | | | |\n"
            + " \\____\\__,_|_|\\__|_|\\__, |_| |_|\n"
            + "                    |___/       \n";

    /** Source of commands entered by the user. */
    private final Scanner scanner;

    /** Destination for messages produced by Caitlyn. */
    private final Consumer<String> output;

    /** Destination for errors, allowing the GUI to distinguish them from replies. */
    private final Consumer<String> errorOutput;

    /** Whether to include the banner and separators used by the console. */
    private final boolean hasConsoleDecoration;

    /**
     * Creates a UI connected to the standard input and output streams.
     */
    public Ui() {
        this(new Scanner(System.in), message -> System.out.println(message));
    }

    /**
     * Creates a UI with a supplied input source.
     *
     * @param scanner source from which commands are read.
     */
    Ui(Scanner scanner) {
        this(requireScanner(scanner), message -> System.out.println(message));
    }

    /**
     * Creates a UI that writes responses to a supplied destination.
     *
     * @param output destination for responses produced by Caitlyn.
     */
    public Ui(Consumer<String> output) {
        this((Scanner) null, output);
    }

    /**
     * Creates a graphical output adapter without console banners or separators.
     *
     * @param output destination for ordinary responses.
     * @param errorOutput destination for errors, including startup loading failures.
     */
    public Ui(Consumer<String> output, Consumer<String> errorOutput) {
        this(null, output, errorOutput, false);
    }

    /**
     * Creates a UI with supplied input and output sources.
     *
     * @param scanner source from which commands are read, or {@code null} when input is not needed.
     * @param output destination for responses produced by Caitlyn.
     */
    private Ui(Scanner scanner, Consumer<String> output) {
        this(scanner, output, output, true);
    }

    /**
     * Configures input, response destinations, and console decoration independently.
     */
    private Ui(Scanner scanner, Consumer<String> output, Consumer<String> errorOutput,
            boolean hasConsoleDecoration) {
        if (output == null || errorOutput == null) {
            throw new IllegalArgumentException("The UI output destinations cannot be null.");
        }
        this.scanner = scanner;
        this.output = output;
        this.errorOutput = errorOutput;
        this.hasConsoleDecoration = hasConsoleDecoration;
    }

    /**
     * Returns the scanner after checking that it is available.
     */
    private static Scanner requireScanner(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("The UI scanner cannot be null.");
        }
        return scanner;
    }

    /**
     * Displays Caitlyn's greeting.
     */
    public void showWelcome() {
        showSeparator();
        if (hasConsoleDecoration) {
            output.accept(BANNER);
        }
        output.accept("Good day, master. I am Caitlyn, humbly at your service.");
        output.accept("How may I serve you today?");
        showSeparator();
    }

    /**
     * Returns whether another command is available.
     *
     * @return {@code true} when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner != null && scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command.
     *
     * @return the next command without leading or trailing whitespace.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI does not have an input source.");
        }
        return scanner.nextLine().trim();
    }

    /**
     * Displays the standard conversation separator.
     */
    public void showSeparator() {
        if (hasConsoleDecoration) {
            output.accept(SEPARATOR);
        }
    }

    /**
     * Displays Caitlyn's farewell message.
     */
    public void showFarewell() {
        output.accept("     Farewell, master. It has been my pleasure to serve you.");
    }

    /**
     * Displays an error encountered while loading saved tasks.
     */
    public void showLoadingError() {
        showError("I could not read data/duke.txt. Task changes are disabled to protect your saved data. "
                + "Repair the file or its access permissions, then restart Caitlyn.");
    }

    /**
     * Displays a user-facing command error.
     *
     * @param message the error text to display.
     */
    public void showError(String message) {
        errorOutput.accept("     " + message);
    }

    /**
     * Displays all tasks with their one-based list numbers.
     *
     * @param tasks the tasks to display.
     */
    public void showTasks(List<Task> tasks) {
        output.accept("     Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            output.accept("     " + (index + 1) + "." + tasks.get(index));
        }
    }

    /**
     * Displays matching tasks while retaining their original list numbers.
     *
     * @param tasks all tasks in their original list order.
     * @param matchingTasks tasks whose descriptions matched the search keyword.
     */
    public void showMatchingTasks(List<Task> tasks, List<Task> matchingTasks) {
        output.accept("     Here are the matching tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            if (matchingTasks.contains(tasks.get(index))) {
                output.accept("     " + (index + 1) + "." + tasks.get(index));
            }
        }
    }

    /**
     * Displays confirmation after a task is added.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks after adding the task.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.accept("     Got it. I've added this task:");
        output.accept("       " + task);
        output.accept("     Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation after a task is deleted.
     *
     * @param task the task that was deleted.
     * @param taskCount the number of tasks after deleting the task.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.accept("     Noted. I've removed this task:");
        output.accept("       " + task);
        output.accept("     Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation after a task's completion status changes.
     *
     * @param task the task whose status changed.
     * @param isMarkingDone whether the task was marked done rather than not done.
     */
    public void showTaskStatus(Task task, boolean isMarkingDone) {
        if (isMarkingDone) {
            output.accept("     As you wish, master. I have marked this task as done:");
        } else {
            output.accept("     Of course, master. I have marked this task as not done yet:");
        }
        output.accept("       " + task);
    }
}
