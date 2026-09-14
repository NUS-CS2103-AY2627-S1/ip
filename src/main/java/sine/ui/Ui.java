package sine.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import sine.task.Task;
import sine.task.TaskList;

/**
 * Handles all console input and output for the Sine chatbot.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = " ____  _            \n"
            + "/ ___|(_)_ __   ___ \n"
            + "\\___ \\| | '_ \\ / _ \\\n"
            + " ___) | | | | |  __/\n"
            + "|____/|_|_| |_|\\___|";

    private final Scanner scanner;
    private final PrintStream output;
    private final boolean isSeparatorShown;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this(System.out, true);
    }

    /**
     * Creates a UI that writes response text without console separators.
     *
     * @param output Destination for response text.
     */
    public Ui(PrintStream output) {
        this(output, false);
    }

    private Ui(PrintStream output, boolean isSeparatorShown) {
        this.scanner = new Scanner(System.in);
        this.output = output;
        this.isSeparatorShown = isSeparatorShown;
    }

    /**
     * Shows the chatbot banner and greeting.
     */
    public void showWelcome() {
        showSeparator();
        output.println(BANNER);
        output.println("Hello! I'm Sine.");
        output.println(Messages.COMMAND_HELP);
        showSeparator();
    }

    /**
     * Checks whether another command is available from the input stream.
     *
     * @return True if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and starts its response section.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        String command = scanner.nextLine();
        showSeparator();
        return command;
    }

    /**
     * Shows the normal farewell.
     */
    public void showGoodbye() {
        output.println(" Bye. I'll be here if you need me :)");
        showSeparator();
    }

    /**
     * Shows every task with its one-based task number.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        output.println(" TODO list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
        showSeparator();
    }

    /**
     * Shows tasks that match a find command.
     *
     * @param tasks Matching tasks to display.
     */
    public void showFoundTasks(List<Task> tasks) {
        output.println(" Here is what I found:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
        showSeparator();
    }

    /**
     * Shows confirmation that a task was removed.
     *
     * @param task Removed task.
     * @param taskCount Number of remaining tasks.
     */
    public void showDeletedTask(Task task, int taskCount) {
        output.println(" Roger that. I've removed this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
        showSeparator();
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task Updated task.
     */
    public void showUnmarkedTask(Task task) {
        output.println(" Roger that. I've marked this task as not done yet:");
        output.println("   " + task);
        showSeparator();
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task Updated task.
     */
    public void showMarkedTask(Task task) {
        output.println(" Great work! I've marked this task as done:");
        output.println("   " + task);
        showSeparator();
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Number of current tasks.
     */
    public void showAddedTask(Task task, int taskCount) {
        output.println(" Got it. I've added this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
        showSeparator();
    }

    /**
     * Shows the response for an unknown command.
     */
    public void showUnknownCommand() {
        output.println(" Sorry, I don't recognize that command."
                + " Please use one of the commands listed at startup.");
        showSeparator();
    }

    /**
     * Shows a user-command validation error.
     *
     * @param message Explanation of the invalid command.
     */
    public void showCommandError(String message) {
        output.println(" Error :( " + message);
        showSeparator();
    }

    /**
     * Warns that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        output.println(" Warning: I couldn't load your saved tasks."
                + " Starting with an empty list.");
        showSeparator();
    }

    /**
     * Warns that a task-list change could not be saved.
     */
    public void showSavingError() {
        output.println(" Error :( I couldn't save that change."
                + " It is available only for this session.");
        showSeparator();
    }

    /**
     * Shows which task change was reversed in memory.
     *
     * @param details Action description and affected task.
     */
    public void showUndo(String details) {
        output.println("Understood. I undid the last task change.");
        output.println(details);
        showSeparator();
    }

    private void showSeparator() {
        if (isSeparatorShown) {
            output.println(SEPARATOR);
        }
    }
}
