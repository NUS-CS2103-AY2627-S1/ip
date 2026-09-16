package arin.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import arin.task.Task;

/**
 * Handles all console input and output for Arin.
 */
public class Ui {
    private static final String DIVIDER = "________________________________";

    private final Scanner scanner;
    private final PrintStream output;
    private final boolean shouldShowDividers;

    /**
     * Creates a UI that reads from standard input and writes to standard output.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI with specified input and output streams.
     *
     * @param scanner Source of user commands.
     * @param output Destination for messages.
     */
    public Ui(Scanner scanner, PrintStream output) {
        this(scanner, output, true);
    }

    /**
     * Creates a message formatter with optional console dividers.
     *
     * @param scanner Source of user commands.
     * @param output Destination for messages.
     * @param shouldShowDividers Whether messages need console separators.
     */
    public Ui(Scanner scanner, PrintStream output, boolean shouldShowDividers) {
        this.scanner = scanner;
        this.output = output;
        this.shouldShowDividers = shouldShowDividers;
    }

    /**
     * Displays a compact reference to the supported commands.
     */
    public void showHelp() {
        output.println("Your next task starts here:");
        output.println("todo <description>");
        output.println("deadline <description> /by <yyyy-MM-dd>");
        output.println("event <description> /from <start> /to <end>");
        output.println("list | find <keyword> | sort name | sort deadline");
        output.println("mark <number> | unmark <number> | delete <number> | help | bye");
        output.println("Use numbers from the latest list, not the search results. Changes save automatically.");
    }

    private void showDivider() {
        if (shouldShowDividers) {
            output.println(DIVIDER);
        }
    }

    /**
     * Displays Arin's welcome message.
     */
    public void showWelcome() {
        showDivider();
        output.println("    _         _       ");
        output.println("   / \\   _ __(_)_ __  ");
        output.println("  / _ \\ | '__| | '_ \\ ");
        output.println(" / ___ \\| |  | | | | |");
        output.println("/_/   \\_\\_|  |_|_| |_|");
        output.println("Good afternoon.");
        output.println("My name is Arin.");
        output.println("And I am a Wilderness Explorer in Tribe 54, Sweat Lodge 12.");
        output.println("Are you in need of any assistance today, sir?");
        showDivider();
    }

    /**
     * Reads one complete command entered by the user.
     *
     * @return The entered command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays all tasks with their one-based positions.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        showNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks Tasks that match the search keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showNumberedTasks("Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Displays confirmation that a task was marked complete.
     *
     * @param task Completed task to display.
     */
    public void showMarkedTask(Task task) {
        showTaskMessage("Nice! I've marked this task as done:", task);
    }

    /**
     * Displays confirmation that a task was marked incomplete.
     *
     * @param task Incomplete task to display.
     */
    public void showUnmarkedTask(Task task) {
        showTaskMessage("OK, I've marked this task as not done yet:", task);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Added task to display.
     * @param taskCount Number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        showDivider();
        output.println("Got it. I've added this task:");
        output.println(task);
        output.println("Now you have " + taskCount + " tasks in the list.");
        showDivider();
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task Deleted task to display.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showDeletedTask(Task task, int taskCount) {
        showDivider();
        output.println("Noted. I've removed this task:");
        output.println("  " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
        showDivider();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        showDivider();
        output.println("Oops! " + message);
        showDivider();
    }

    /**
     * Displays Arin's goodbye message.
     */
    public void showGoodbye() {
        showDivider();
        output.println("Bye. Hope to see you again soon!");
        showDivider();
    }

    /**
     * Displays a heading and numbered tasks using the shared list layout.
     *
     * @param heading Heading describing the displayed tasks.
     * @param tasks Tasks to display in their existing order.
     */
    private void showNumberedTasks(String heading, List<Task> tasks) {
        showDivider();
        output.println(heading);
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + ". " + tasks.get(i));
        }
        showDivider();
    }

    /**
     * Displays a task message surrounded by the standard divider.
     *
     * @param message Message to display.
     * @param task Task to display.
     */
    private void showTaskMessage(String message, Task task) {
        showDivider();
        output.println(message);
        output.println(task);
        showDivider();
    }
}
