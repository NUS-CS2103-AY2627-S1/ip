package meka.ui;

import java.io.PrintStream;
import java.util.Scanner;

import meka.task.Task;
import meka.task.TaskList;

/**
 * Handles all console input and output for MEKA.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = "███╗   ███╗███████╗██╗  ██╗ █████╗\n"
            + "████╗ ████║██╔════╝██║ ██╔╝██╔══██╗\n"
            + "██╔████╔██║█████╗  █████╔╝ ███████║\n"
            + "██║╚██╔╝██║██╔══╝  ██╔═██╗ ██╔══██║\n"
            + "██║ ╚═╝ ██║███████╗██║  ██╗██║  ██║\n"
            + "╚═╝     ╚═╝╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝\n";
    /**
     * Message shown when persisted tasks cannot be loaded safely.
     */
    private static final String LOAD_ERROR_MESSAGE =
            "I could not load the saved tasks, so I started with an empty task list.";
    /**
     * Message shown when task changes cannot be persisted.
     */
    private static final String SAVE_ERROR_MESSAGE =
            "I could not save the task list. Your changes are available only for this session.";

    /** Source of commands entered by the user. */
    private final Scanner scanner;

    /** Destination for messages shown to the user. */
    private final PrintStream output;

    /**
     * Creates a console user interface that reads from standard input.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a user interface that writes to the specified output stream.
     *
     * @param output destination for user-visible messages.
     */
    public Ui(PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /**
     * Returns whether another command is available to read.
     *
     * @return true when standard input has another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and removes surrounding whitespace.
     *
     * @return command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Shows MEKA's banner and greeting.
     */
    public void showWelcome() {
        showLine();
        printLines(
                BANNER,
                " Hello! I'm MEKA.",
                " What can I do for you?");
    }

    /**
     * Shows the message displayed when MEKA exits.
     */
    public void showGoodbye() {
        output.println(" Bye. Hope to see you again soon!");
    }

    /**
     * Shows a horizontal divider between interactions.
     */
    public void showLine() {
        output.println(SEPARATOR);
    }

    /**
     * Shows every task with its one-based task number.
     *
     * @param tasks tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        int taskNumber = 1;
        for (Task task : tasks) {
            output.println(" " + taskNumber + ". " + task);
            taskNumber++;
        }
    }

    /**
     * Shows search results with consecutive task numbers.
     *
     * @param matches Matching tasks in their original order.
     */
    public void showMatchingTasks(TaskList matches) {
        output.println(" Here are the matching tasks in your list:");
        showTaskList(matches);
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount current number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        printLines(
                " Got it. I've added this task:",
                "   " + task,
                " Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task task that was marked.
     */
    public void showTaskMarked(Task task) {
        printLines(
                " Nice! I've marked this task as done:",
                "   " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        printLines(
                " OK, I've marked this task as not done yet:",
                "   " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of tasks remaining.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        printLines(
                " Noted. I've removed this task:",
                "   " + task,
                " Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Shows confirmation that all active tasks were archived.
     *
     * @param archivedTaskCount number of tasks moved to the archive.
     */
    public void showTasksArchived(int archivedTaskCount) {
        String taskLabel = archivedTaskCount == 1 ? "task" : "tasks";
        printLines(
                " Noted. I've archived all " + archivedTaskCount + " " + taskLabel + ".",
                " Now you have 0 tasks in the list.");
    }

    /**
     * Shows that there are no active tasks available to archive.
     */
    public void showNoTasksToArchive() {
        output.println(" There are no tasks to archive.");
    }

    /**
     * Shows an error caused by an invalid command.
     *
     * @param message explanation to show the user.
     */
    public void showError(String message) {
        output.println(" " + message);
    }

    /**
     * Warns that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        showError(LOAD_ERROR_MESSAGE);
    }

    /**
     * Warns that changes could not be saved.
     */
    public void showSavingError() {
        showError(SAVE_ERROR_MESSAGE);
    }

    /**
     * Prints each supplied line in order.
     *
     * @param lines lines to print.
     */
    private void printLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
