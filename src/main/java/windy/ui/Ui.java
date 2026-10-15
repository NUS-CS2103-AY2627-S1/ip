package windy.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import windy.task.Task;

/**
 * Handles all console input and output for the Windy application.
 */
public class Ui {
    private static final String NAME = "Windy";
    private static final String CONSOLE_CONTENT_INDENTATION = "     ";
    private static final String CONSOLE_SEPARATOR_INDENTATION = "    ";

    private final Scanner scanner;
    private final PrintStream output;
    private final String contentIndentation;
    private final String separatorIndentation;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this(new Scanner(System.in), System.out,
                CONSOLE_CONTENT_INDENTATION, CONSOLE_SEPARATOR_INDENTATION);
    }

    /**
     * Creates an output-only user interface that writes to the specified stream.
     *
     * @param output stream that receives application messages.
     */
    public Ui(PrintStream output) {
        this(new Scanner(InputStream.nullInputStream()), output,
                CONSOLE_CONTENT_INDENTATION, CONSOLE_SEPARATOR_INDENTATION);
    }

    private Ui(Scanner scanner, PrintStream output,
            String contentIndentation, String separatorIndentation) {
        this.scanner = scanner;
        this.output = output;
        this.contentIndentation = contentIndentation;
        this.separatorIndentation = separatorIndentation;
    }

    /**
     * Creates an output-only UI whose messages are formatted for a dialog box.
     *
     * @param output stream that receives application messages.
     * @return a UI without console-only indentation.
     */
    public static Ui createDialogUi(PrintStream output) {
        return new Ui(new Scanner(InputStream.nullInputStream()), output, "", "");
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return {@code true} if another line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command from standard input.
     *
     * @return the next user command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the application banner and greeting.
     */
    public void showWelcome() {
        showLine();
        showBanner();
        showMessages("Hey there~ I'm " + NAME + ". What story shall we write today?");
        showLine();
    }

    /**
     * Displays the farewell message.
     */
    public void showBye() {
        showMessages("Until our next song, may the wind carry you onward!");
        showLine();
    }

    /**
     * Displays an error message.
     *
     * @param message the message to display.
     */
    public void showError(String message) {
        showMessages(message);
    }

    /**
     * Displays every task with its one-based task number.
     *
     * @param tasks the tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        showMessages("Let's see what we've written so far:");
        for (int i = 0; i < tasks.size(); i++) {
            showMessages((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays the deleted task and the remaining task count.
     *
     * @param task the task that was removed.
     * @param taskCount the number of tasks remaining.
     */
    public void showDeleteTask(Task task, int taskCount) {
        showMessages(
                "Time for a change of tune! I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays confirmation that a task was marked as completed.
     *
     * @param task the updated task.
     */
    public void showTaskMarkedDone(Task task) {
        showTaskCompletionUpdate("And that's a verse complete! I've marked this task as done:", task);
    }

    /**
     * Displays confirmation that a task was marked as not completed.
     *
     * @param task the updated task.
     */
    public void showTaskMarkedNotDone(Task task) {
        showTaskCompletionUpdate("Ah, one more verse to write. I've marked this task as not done yet:", task);
    }

    private void showTaskCompletionUpdate(String confirmationMessage, Task task) {
        showMessages(confirmationMessage, "  " + task);
    }

    /**
     * Displays the added task and the updated task count.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks after the addition.
     */
    public void showAddTask(Task task, int taskCount) {
        showMessages(
                "A new verse for our story! I've added this task:",
                "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Displays matching tasks, or a message when none are found.
     *
     * @param tasks the matching tasks.
     * @param emptyMessage the message to display when no tasks match.
     */
    public void showFoundTasks(List<Task> tasks, String emptyMessage) {
        if (tasks.isEmpty()) {
            showMessages(emptyMessage);
            return;
        }
        showMessages("There are " + tasks.size() + " tasks that meet the requirements:");
        for (Task task : tasks) {
            showMessages(task.toString());
        }
    }

    /**
     * Displays the horizontal separator used between command responses.
     */
    public void showLine() {
        output.println(separatorIndentation + "______________________________________________");
    }

    /**
     * Displays each supplied message on a separate line.
     *
     * @param messages the messages to display.
     */
    private void showMessages(String... messages) {
        for (String message : messages) {
            output.println(contentIndentation + message);
        }
    }

    /**
     * Displays an undo or redo result.
     *
     * @param message the result message.
     */
    public void showHistoryResult(String message) {
        showMessages(message);
    }

    private void showBanner() {
        String banner = contentIndentation + "__        ___           _       \n"
                + contentIndentation + "\\ \\      / (_)_ __   __| |_   _ \n"
                + contentIndentation + " \\ \\ /\\ / /| | '_ \\ / _` | | | |\n"
                + contentIndentation + "  \\ V  V / | | | | | (_| | |_| |\n"
                + contentIndentation + "   \\_/\\_/  |_|_| |_|\\__,_|\\__, |\n"
                + contentIndentation + "                          |___/ \n";
        output.print(banner);
    }
}
