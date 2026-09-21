package quackie.ui;

import java.io.PrintStream;
import java.util.Scanner;

import quackie.task.Task;
import quackie.task.TaskList;

/**
 * Handles Quackie's console input and output.
 */
public class Ui {
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = "                           _      _      \n"
            + "  __ _  _   _   __ _  ___| | __ (_)  ___ \n"
            + " / _` || | | | / _` |/ __| |/ / | | / _ \\ \n"
            + "| (_| || |_| || (_| | (__|   <  | ||  __/\n"
            + " \\__, | \\__,_| \\__,_|\\___|_|\\_\\ |_|\\___|\n"
            + "    |_|                                  ";

    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a console UI that reads commands from standard input. */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI with caller-supplied streams, which allows output to be captured in tests.
     *
     * @param scanner the source of user commands
     * @param output the destination for chatbot messages
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    /** Displays the welcome banner and prompt. */
    public void showWelcome() {
        showLine();
        showMessages(BANNER, "Hello! I'm Quackie.", "What can I do for you?");
        showLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the next command, or {@code null} when input has ended
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays the standard horizontal separator. */
    public void showLine() {
        output.println(SEPARATOR);
    }

    /** Displays the farewell message and trailing separator. */
    public void showBye() {
        output.println("Bye. Hope to see you again soon!");
        showLine();
    }

    /**
     * Displays an error message using Quackie's standard error prefix.
     *
     * @param message the explanation to show to the user
     */
    public void showError(String message) {
        output.println(" OOPS!!! " + message);
    }

    /**
     * Displays every task in the supplied list.
     *
     * @param tasks the tasks to display
     */
    public void showTasks(TaskList tasks) {
        showTaskList(" Here are the tasks in your list:", tasks);
    }

    /**
     * Displays the tasks matching a find command.
     *
     * @param tasks the matching tasks to display
     */
    public void showMatchingTasks(TaskList tasks) {
        showTaskList(" Here are the matching tasks in your list:", tasks);
    }

    /**
     * Displays a heading followed by a numbered task list.
     *
     * @param heading heading that describes the supplied tasks
     * @param tasks tasks to display
     */
    private void showTaskList(String heading, TaskList tasks) {
        output.println(heading);
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation for a newly added task.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        showMessages(
                " Got it. I've added this task:",
                "   " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Displays confirmation for a deleted task.
     *
     * @param task the task that was deleted
     * @param taskCount the number of tasks after the deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showMessages(
                " Noted. I've removed this task:",
                "   " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Builds the task-count sentence shown after adding or deleting a task.
     * Uses the singular noun only for exactly one task, e.g. "1 task" but "0 tasks" and "2 tasks".
     *
     * @param taskCount the number of tasks currently in the list
     * @return the grammatical task-count sentence, with Quackie's leading-space indentation
     */
    static String formatTaskCount(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        return " Now you have " + taskCount + " " + noun + " in the list.";
    }

    /**
     * Displays confirmation for an updated task.
     *
     * @param task the task after its details were replaced
     */
    public void showTaskUpdated(Task task) {
        showMessages(" Updated this task:", "   " + task);
    }

    /**
     * Displays confirmation for a task marked as done.
     *
     * @param task the task that was marked as done
     */
    public void showTaskMarked(Task task) {
        showMessages(" Nice! I've marked this task as done:", "   " + task);
    }

    /**
     * Displays confirmation for a task marked as not done.
     *
     * @param task the task that was marked as not done
     */
    public void showTaskUnmarked(Task task) {
        showMessages(" OK, I've marked this task as not done yet:", "   " + task);
    }

    /**
     * Displays any number of message lines in their supplied order.
     *
     * @param messages message lines to display
     */
    private void showMessages(String... messages) {
        for (String message : messages) {
            output.println(message);
        }
    }
}
