package charliek.ui;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Consumer;

import charliek.command.CommandType;
import charliek.model.Task;

/**
 * Handles CharlieK's basic interaction with the user.
 *
 * <p>This class owns console input and all user-facing messages. It does not
 * decide how tasks are mutated or sorted.</p>
 */
public class Ui {
    /**
     * The separator printed between console messages.
     */
    private static final String LINE = "____________________________________________________________";

    /**
     * Commands shown by the general help display, in user-facing order.
     */
    private static final List<CommandType> HELP_COMMANDS = List.of(
            CommandType.TODO,
            CommandType.DEADLINE,
            CommandType.EVENT,
            CommandType.LIST,
            CommandType.FIND,
            CommandType.MARK,
            CommandType.UNMARK,
            CommandType.DELETE,
            CommandType.BYE);

    /**
     * The banner printed when CharlieK starts.
     */
    private static final String BANNER = "  ____ _                _ _      _  __\n"
            + " / ___| |__   __ _ _ __| (_) ___| |/ /\n"
            + "| |   | '_ \\ / _` | '__| | |/ _ \\ ' / \n"
            + "| |___| | | | (_| | |  | | |  __/ . \\ \n"
            + " \\____|_| |_|\\__,_|_|  |_|_|\\___|_|\\_\\\n";

    /**
     * Reads commands from the user's standard input.
     */
    private final Scanner scanner;

    /**
     * Receives rendered user-facing text, allowing the console and GUI to share command logic.
     */
    private final Consumer<String> output;

    /**
     * Receives rendered error text so output-only clients can highlight it.
     */
    private final Consumer<String> errorOutput;

    /**
     * Creates a UI connected to the standard console.
     */
    public Ui() {
        this(new Scanner(System.in), System.out::print, ignored -> { });
    }

    /**
     * Creates a UI that sends rendered messages to the supplied output sink.
     * This constructor is used by the JavaFX controller to reuse the existing
     * command and response behavior without writing to the console.
     *
     * @param output the destination for rendered user-facing text.
     */
    public Ui(Consumer<String> output) {
        this(null, output, ignored -> { });
    }

    /**
     * Creates an output-only UI with separate normal and error output sinks.
     *
     * @param output the destination for normal rendered text.
     * @param errorOutput the destination notified when an error is rendered.
     */
    public Ui(Consumer<String> output, Consumer<String> errorOutput) {
        this(null, output, errorOutput);
    }

    /**
     * Creates a UI with explicit input and output collaborators.
     */
    private Ui(Scanner scanner, Consumer<String> output, Consumer<String> errorOutput) {
        this.scanner = scanner;
        this.output = Objects.requireNonNull(output);
        this.errorOutput = Objects.requireNonNull(errorOutput);
    }

    /**
     * Shows the startup banner and any saved-task loading error.
     *
     * @param loadingError the loading error to show, or {@code null} when loading succeeded.
     */
    public void showWelcome(String loadingError) {
        showLine();
        print(BANNER);
        print("Hello! I'm CharlieK." + System.lineSeparator());
        print("What can I do for you? Let's make this task list great again!" + System.lineSeparator());
        showLine();
        if (loadingError != null) {
            showError(loadingError);
            showLine();
        }
    }

    /**
     * Returns whether another command is available from standard input.
     *
     * @return {@code true} when another input line is available.
     */
    public boolean hasNextCommand() {
        ensureConsoleInputAvailable();
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the next complete input line.
     */
    public String readCommand() {
        ensureConsoleInputAvailable();
        return scanner.nextLine();
    }

    /**
     * Prints the standard message separator.
     */
    public void showLine() {
        print(LINE + System.lineSeparator());
    }

    /**
     * Shows the normal goodbye message and its trailing separator.
     */
    public void showGoodbye() {
        print("     Bye. Keep carrying the flame!" + System.lineSeparator());
        showLine();
    }

    /**
     * Shows a user-facing error message.
     *
     * @param message the message to display.
     */
    public void showError(String message) {
        String renderedMessage = "     " + message + System.lineSeparator();
        print(renderedMessage);
        errorOutput.accept(renderedMessage);
    }

    /**
     * Shows the fallback message used for unexpected command-processing failures.
     */
    public void showProcessingError() {
        showError("I couldn't process that command. Please check the input and try again.");
    }

    /**
     * Shows every available command with its usage, description, and example.
     */
    public void showAvailableCommands() {
        print("     Available commands - the winning playbook" + System.lineSeparator() + System.lineSeparator());
        for (int commandIndex = 0; commandIndex < HELP_COMMANDS.size(); commandIndex++) {
            CommandType command = HELP_COMMANDS.get(commandIndex);
            print("     " + command.getUsage() + System.lineSeparator());
            print("       " + command.getDescription() + System.lineSeparator());
            print("       Example: " + command.getExample() + System.lineSeparator());
            if (commandIndex < HELP_COMMANDS.size() - 1) {
                print(System.lineSeparator());
            }
        }
    }

    /**
     * Shows detailed usage information for one command.
     *
     * @param command the command to describe.
     */
    public void showCommandHelp(CommandType command) {
        print("     Help: " + command.getKeyword() + System.lineSeparator() + System.lineSeparator());
        print("     Usage" + System.lineSeparator());
        print("       " + command.getUsage() + System.lineSeparator() + System.lineSeparator());
        print("     Description" + System.lineSeparator());
        print("       " + command.getDescription() + System.lineSeparator() + System.lineSeparator());
        print("     Example" + System.lineSeparator());
        print("       " + command.getExample() + System.lineSeparator());
    }

    /**
     * Shows an error for a command that has no help entry.
     *
     * @param commandKeyword the command keyword that was requested.
     */
    public void showUnknownHelpCommand(String commandKeyword) {
        showError("I do not have help for '" + commandKeyword
                + "'. Try 'help' to see the available commands.");
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task the added task.
     * @param taskCount the number of tasks after adding it.
     */
    public void showTaskAdded(Task task, int taskCount) {
        print("     Big win! I've added this task:" + System.lineSeparator());
        print("       " + task + System.lineSeparator());
        print("     Now you have " + taskCount + " tasks in the list." + System.lineSeparator());
    }

    /**
     * Shows that the requested task number does not exist.
     */
    public void showTaskDoesNotExist() {
        showError("That task does not exist. Next time, choose a winner.");
    }

    /**
     * Shows that the supplied task number is not a valid number.
     */
    public void showInvalidTaskNumber() {
        showError("Please provide a valid task number. Precision wins.");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task the marked task.
     */
    public void showTaskMarked(Task task) {
        print("     Another win! I've marked this task as done:" + System.lineSeparator());
        print("       " + task + System.lineSeparator());
    }

    /**
     * Shows that a task was already marked as done.
     *
     * @param task the already marked task.
     */
    public void showTaskAlreadyMarked(Task task) {
        print("     This task is already winning:" + System.lineSeparator());
        print("       " + task + System.lineSeparator());
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task the unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        print("     No surrender! I've marked this task as not done yet:" + System.lineSeparator());
        print("       " + task + System.lineSeparator());
    }

    /**
     * Shows that a task was already marked as not done.
     *
     * @param task the already unmarked task.
     */
    public void showTaskAlreadyUnmarked(Task task) {
        print("     This task is already not done - stay focused:" + System.lineSeparator());
        print("       " + task + System.lineSeparator());
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task the deleted task.
     * @param taskCount the number of tasks after deleting it.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        print("     Strong move. I've removed this task:" + System.lineSeparator());
        print("       " + task + System.lineSeparator());
        print("     Now you have " + taskCount + " tasks in the list." + System.lineSeparator());
    }

    /**
     * Shows the supplied tasks in their display order.
     *
     * @param tasks the tasks to display.
     */
    public void showTasks(List<Task> tasks) {
        showTaskList(tasks, "     Here are the tasks in your winning list:");
    }

    /**
     * Shows tasks that match a keyword search.
     *
     * @param tasks the matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        showTaskList(tasks, "     Here are the matching tasks in your winning list:");
    }

    /**
     * Shows a heading followed by tasks numbered in their display order.
     *
     * @param tasks the tasks to display.
     * @param heading the heading to print before the task list.
     */
    private void showTaskList(List<Task> tasks, String heading) {
        print(heading + System.lineSeparator());
        for (int taskIndex = 0; taskIndex < tasks.size(); taskIndex++) {
            print("     " + (taskIndex + 1) + "." + tasks.get(taskIndex) + System.lineSeparator());
        }
    }

    /**
     * Sends rendered text to the configured output destination.
     */
    private void print(String message) {
        output.accept(message);
    }

    /**
     * Fails clearly when console-only input methods are used by an output-only UI.
     */
    private void ensureConsoleInputAvailable() {
        if (scanner == null) {
            throw new IllegalStateException("This UI is configured for output only.");
        }
    }
}
