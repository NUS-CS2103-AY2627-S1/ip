package toot.ui;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import toot.task.Task;

/**
 * Handles all console input and user-facing output for Toot.
 */
public class Ui {
    private static final String BANNER = " _____           _\n"
            + "|_   _|__   ___ | |_\n"
            + "  | |/ _ \\ / _ \\| __|\n"
            + "  | | (_) | (_) | |_\n"
            + "  |_|\\___/ \\___/ \\__|\n";
    private static final String HORIZONTAL_LINE =
            "⋆｡°✩ ──────────────────────────────────────────────── ✩°｡⋆";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to standard input and standard output.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI with explicit streams, primarily to support focused tests.
     *
     * @param scanner Source of user commands.
     * @param output Destination for user-facing messages.
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Reports whether another command is available to read.
     *
     * @return {@code true} when input has another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command line.
     *
     * @return Raw user command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the application banner and greeting.
     */
    public void showWelcome() {
        showLines(
                HORIZONTAL_LINE,
                BANNER,
                "Hewwo!! I'm Toot, ur teeny-tiny computey baby! ૮₍ ˶•⤙•˶ ₎ა",
                "Gib me a command... Toot do a BIG twy!! (•̀ᴗ•́)و");
    }

    /**
     * Shows the farewell message.
     */
    public void showGoodbye() {
        output.println("Otay bye-bye! Toot go eepy now... zZz (｡-ω-)ﾉ");
    }

    /**
     * Shows supported commands, index rules, and example task inputs.
     */
    public void showHelp() {
        showLines(
                "Toot command guide:",
                "todo DESCRIPTION - Add a todo.",
                "deadline DESCRIPTION /by YYYY-MM-DD - Add a deadline.",
                "event DESCRIPTION /from START /to END - Add an event.",
                "list - Show all tasks and their numbers.",
                "find KEYWORD - Find descriptions (ignoring case).",
                "mark NUMBER - Mark a task done.",
                "unmark NUMBER - Mark a task not done.",
                "delete NUMBER - Delete a task.",
                "help - Show this guide.",
                "bye - Exit.",
                "Use task numbers from list for mark, unmark, and delete.",
                "Examples: todo read book; deadline return book /by 2026-09-14",
                "Example: event meeting /from Mon 2pm /to Mon 3pm");
    }

    /**
     * Shows a user-facing error.
     *
     * @param message Error explanation.
     */
    public void showError(String message) {
        output.println("Oh crumbs! " + message);
    }

    /**
     * Shows every task with its one-based list number.
     *
     * @param tasks Tasks in display order.
     */
    public void showTasks(List<Task> tasks) {
        output.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows tasks matching a find command with one-based result numbers.
     *
     * @param tasks Matching tasks in display order.
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.println("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task Added task.
     * @param taskCount Updated task count.
     */
    public void showAddedTask(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        showLines(
                "Toot addeded:",
                "  " + task,
                "Toot has " + taskCount + " " + taskWord + " in the list now! (｡•̀ᴗ-)✧");
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task Updated task.
     */
    public void showMarkedTask(Task task) {
        showLines(
                "Nice! I've marked this task as done:",
                "  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task Updated task.
     */
    public void showUnmarkedTask(Task task) {
        showLines(
                "OK, I've marked this task as not done yet:",
                "  " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task Deleted task.
     * @param taskCount Updated task count.
     */
    public void showDeletedTask(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        showLines(
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /**
     * Shows the divider line.
     */
    public void showLine() {
        output.println(HORIZONTAL_LINE);
    }

    /**
     * Shows the divider line followed by a blank line.
     */
    public void showLineWithBlankLine() {
        showLines(HORIZONTAL_LINE, "");
    }

    /**
     * Prints each supplied message on its own line.
     *
     * @param lines Messages to print in order.
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
