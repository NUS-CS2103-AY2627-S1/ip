package megatron.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import megatron.exception.EmptyCommandException;
import megatron.exception.EmptyDescriptionException;
import megatron.exception.InvalidTaskFormatException;
import megatron.exception.MegatronException;
import megatron.exception.UnknownCommandException;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.task.TaskMatch;

/**
 * Handles all console input and output used by Megatron.
 *
 * <p>This class keeps user-facing text in one place. The application logic
 * can therefore work with commands and tasks without knowing how they are
 * displayed.</p>
 */
public final class Ui {
    private static final String BANNER = "   __  ___              __              \n"
            + "  /  |/  /__ ___ ____ _/ /________  ___ \n"
            + " / /|_/ / -_) _ `/ _ `/ __/ __/ _ \\/ _ \\\n"
            + "/_/  /_/\\__/\\_, /\\_,_/\\__/_/  \\___/_//_/\n"
            + "           /___/                        ";
    private static final String DIVIDER = "____________________________________________________________";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to the standard input and output streams.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI using the supplied input and output streams.
     *
     * @param scanner The input scanner.
     * @param output The output stream.
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = Objects.requireNonNull(scanner);
        this.output = Objects.requireNonNull(output);
    }

    /**
     * Returns whether another complete command is available.
     *
     * @return True if another command line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next complete command line.
     *
     * @return The next command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the welcome banner shown when the application starts.
     */
    public void showWelcome() {
        showDivider();
        output.println(BANNER);
        output.println("Rawr! Megatron Griffin reporting for duty!");
        output.println("I was built to conquer the universe, but task management will do.");
        output.println("What command shall I execute?");
        showDivider();
    }

    /**
     * Displays the divider used between console responses.
     */
    public void showDivider() {
        output.println(DIVIDER);
    }

    /**
     * Displays the message shown when the user exits.
     */
    public void showGoodbye() {
        output.println("Retreat accepted. Try not to create more tasks while I'm gone!");
        showDivider();
    }

    /**
     * Displays every stored task in the order in which it was added.
     *
     * @param tasks The tasks to display.
     */
    public void showTasks(TaskList tasks) {
        if (tasks.size() == 0) {
            output.println("Your task empire is empty. Add a task before it gets awkward.");
            return;
        }

        output.println("Behold! The current state of your task empire:");
        int taskNumber = 1;
        for (Task task : tasks) {
            showTask(taskNumber, task);
            taskNumber++;
        }
    }

    /**
     * Displays tasks that match a find command.
     *
     * @param matchingTasks The matching tasks and their original list numbers.
     */
    public void showMatchingTasks(List<TaskMatch> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            output.println("No matching tasks detected. The search has conquered nothing.");
            return;
        }

        output.println("Target acquired! Here are the matching tasks:");
        for (TaskMatch match : matchingTasks) {
            showTask(match.taskNumber(), match.task());
        }
    }

    /**
     * Displays one task with the supplied one-based number.
     */
    private void showTask(int taskNumber, Task task) {
        output.println(taskNumber + "." + task.displayText());
    }

    /**
     * Displays the supported date/time inputs and interpretation rules.
     */
    public void showDatetimeInformation() {
        output.println("Initiating temporal intelligence. Supported formats:");
        output.println("Dates with a year: yyyy-MM-dd, d/M/yyyy");
        output.println("MMM d yyyy, MMMM d yyyy");
        output.println("d MMM yyyy, d MMMM yyyy");
        output.println("Dates without a year: MMM d, MMMM d");
        output.println("d MMM, d MMMM (current year is used)");
        output.println("Times: HHmm, H:mm, h[am|pm], h:mm[am|pm]");
        output.println("Examples: 2145, 21:45, 9pm, 9:45pm");
        output.println("Weekdays: mon/tue/wed/thu/fri/sat/sun");
        output.println("Full names are also accepted, for example monday 6pm.");
        output.println("Missing times default to 0000 (midnight).");
        output.println("A weekday resolves to its next available occurrence.");
        output.println("A time-only event end uses the event start date.");
        output.println("Example: event Exam /from 6 Jul 26 1200 /to 1400");
        output.println("The above sets an event occuring from 6 Jul 26 1200hrs to 6 Jul 26 1400hrs");
        output.println("Output format: dd MMM uu, HHmm'hrs' (example: 24 Aug 26, 2145hrs)");
    }

    /**
     * Displays confirmation after a task is added.
     *
     * @param task The added task.
     * @param taskCount The current number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("Command accepted! This task has joined my army:");
        output.println(task.displayText());
        output.println("My army now contains " + taskCount + " tasks.");
    }

    /**
     * Displays confirmation after a task's completion status changes.
     *
     * @param task The task whose status changed.
     * @param isMarkedDone Whether the task is now done.
     */
    public void showTaskMarked(Task task, boolean isMarkedDone) {
        if (isMarkedDone) {
            output.println("Victory! This task has fallen before my mighty intellect:");
        } else {
            output.println("Rebellion successful. This task escaped completion:");
        }
        output.println(task.displayText());
    }

    /**
     * Displays confirmation after a task is deleted.
     *
     * @param task The deleted task.
     * @param taskCount The current number of tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Target destroyed! This task has been removed:");
        output.println(task.displayText());
        output.println("My army now contains " + taskCount + " tasks.");
    }

    /**
     * Displays an error message for a rejected command.
     *
     * @param exception The command error to display.
     */
    public void showError(MegatronException exception) {
        if (exception instanceof UnknownCommandException) {
            output.println("That command is not part of my master plan. Try again.");
        } else if (exception instanceof EmptyCommandException
                || exception instanceof EmptyDescriptionException
                || exception instanceof InvalidTaskFormatException) {
            output.println("I need more details. My mind-reading module is still under construction."
                    + " " + exception.getMessage());
        } else {
            output.println("OOPS! Megatron says: " + exception.getMessage());
        }
    }

    /**
     * Closes the input stream owned by this UI.
     */
    public void close() {
        scanner.close();
    }
}
