package littledaisy.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import littledaisy.task.Task;
import littledaisy.task.TaskList;

/** Handles all console input and output for littleDaisy. */
public class Ui {
    private static final String BOT_NAME = "littleDaisy";
    private static final String DIVIDER =
            "    ____________________________________________________________";
    private static final String[] BANNER = {
        " _ _ _   _   _     ___       _         ",
        "| (_) |_| |_| |___|   \\ __ _(_)____  _ ",
        "| | |  _|  _| / -_) |) / _` | (_-< || |",
        "|_|_|\\__|\\__|_\\___|___/\\__,_|_/__/\\_, |",
        "                                  |__/ "
    };

    private final PrintStream output;
    private final Scanner scanner;

    /** Creates a UI using the process's standard input and output. */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied streams.
     *
     * @param input source of user commands
     * @param output destination for chatbot responses
     */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /** Shows the application banner and greeting. */
    public void showWelcome() {
        for (String row : BANNER) {
            output.println(row);
        }
        output.println();
        showResponse(getWelcomeMessage());
    }

    /**
     * Returns whether another command can be read.
     *
     * @return {@code true} if another input line is available
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line.
     *
     * @return raw user command
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows all tasks as a numbered list.
     *
     * @param tasks tasks to display
     */
    public void showList(TaskList tasks) {
        showResponse(getListMessage(tasks));
    }

    /**
     * Shows all tasks matching a find command as a numbered list.
     *
     * @param tasks matching tasks to display
     */
    public void showMatches(TaskList tasks) {
        showResponse(getMatchesMessage(tasks));
    }

    /**
     * Shows confirmation that a task was marked.
     *
     * @param task task whose status changed
     */
    public void showMarked(Task task) {
        showResponse(getMarkedMessage(task));
    }

    /**
     * Shows confirmation that a task was unmarked.
     *
     * @param task task whose status changed
     */
    public void showUnmarked(Task task) {
        showResponse(getUnmarkedMessage(task));
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task added task
     * @param taskCount resulting task count
     */
    public void showAdded(Task task, int taskCount) {
        showResponse(getAddedMessage(task, taskCount));
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task deleted task
     * @param taskCount resulting task count
     */
    public void showDeleted(Task task, int taskCount) {
        showResponse(getDeletedMessage(task, taskCount));
    }

    /**
     * Shows a recoverable user-facing error.
     *
     * @param message error explanation
     */
    public void showError(String message) {
        showResponse(getErrorMessage(message));
    }

    /** Shows the sign-off message. */
    public void showGoodbye() {
        showResponse(getGoodbyeMessage());
    }

    /** Closes the input scanner. */
    public void close() {
        scanner.close();
    }

    /** Returns the greeting shared by the text and graphical interfaces. */
    public String getWelcomeMessage() {
        return joinLines("Hello! I'm " + BOT_NAME + ".", "What can I do for you?");
    }

    /** Returns a numbered rendering of all tasks. */
    public String getListMessage(TaskList tasks) {
        return getTasksMessage("Here are the tasks in your list:", tasks);
    }

    /** Returns a numbered rendering of tasks matched by a find command. */
    public String getMatchesMessage(TaskList tasks) {
        return getTasksMessage("Here are the matching tasks in your list:", tasks);
    }

    /** Returns confirmation that a task was marked. */
    public String getMarkedMessage(Task task) {
        return joinLines("Nice! I've marked this task as done:", "  " + task);
    }

    /** Returns confirmation that a task was unmarked. */
    public String getUnmarkedMessage(Task task) {
        return joinLines("OK, I've marked this task as not done yet:", "  " + task);
    }

    /** Returns confirmation that a task was added. */
    public String getAddedMessage(Task task, int taskCount) {
        return getTaskChangeMessage("Got it. I've added this task:", task, taskCount);
    }

    /** Returns confirmation that a task was deleted. */
    public String getDeletedMessage(Task task, int taskCount) {
        return getTaskChangeMessage("Noted. I've removed this task:", task, taskCount);
    }

    /** Returns a user-facing error message. */
    public String getErrorMessage(String message) {
        return "OOPS!!! " + message;
    }

    /** Returns a concise reference for every supported command. */
    public String getHelpMessage() {
        return joinLines(
                "Here are the commands I understand:",
                "  todo <description>",
                "  deadline <description> /by <yyyy-MM-dd>",
                "  event <description> /from <start> /to <end>",
                "  list",
                "  find <keyword>",
                "  mark <task number>",
                "  unmark <task number>",
                "  delete <task number>",
                "  help",
                "  bye");
    }

    /** Returns the sign-off message. */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /** Prints one already-formatted response between text-UI dividers. */
    public void showResponse(String response) {
        showMessage(response.split("\\R", -1));
    }

    /** Returns the supplied tasks under a heading as a numbered list. */
    private String getTasksMessage(String heading, TaskList tasks) {
        String[] lines = new String[tasks.size() + 1];
        lines[0] = heading;
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + tasks.get(i);
        }
        return joinLines(lines);
    }

    /** Returns a task change and the resulting list size. */
    private String getTaskChangeMessage(String message, Task task, int taskCount) {
        return joinLines(message,
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /** Joins any number of response lines using the platform line separator. */
    private String joinLines(String... lines) {
        return String.join(System.lineSeparator(), lines);
    }

    /** Prints the supplied lines, indented, between dividers. */
    private void showMessage(String... lines) {
        output.println(DIVIDER);
        for (String line : lines) {
            output.println("     " + line);
        }
        output.println(DIVIDER);
        output.println();
    }
}
