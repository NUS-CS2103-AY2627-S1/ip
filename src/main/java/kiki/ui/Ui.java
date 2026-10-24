package kiki.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import kiki.task.Task;
import kiki.task.TaskList;

/**
 * Handles all console input and output for Kiki.
 */
public class Ui {
    private static final String LINE = "   -----------------------------";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a new Ui that reads user input from standard input.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a new Ui that reads from and writes to the supplied streams.
     *
     * @param input Source of user commands.
     * @param output Destination for chatbot responses.
     */
    public Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Prints the startup banner and greeting.
     */
    public void printWelcome() {
        output.print("""
                ██╗  ██╗██╗██╗  ██╗██╗
                ██║ ██╔╝██║██║ ██╔╝██║
                █████╔╝ ██║█████╔╝ ██║
                ██╔═██╗ ██║██╔═██╗ ██║
                ██║  ██╗██║██║  ██╗██║
                ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝╚═╝
                """);
        output.println("Hello! I'm Kiki.");
        output.println("I'll help you keep track of your tasks.");

        output.println(LINE);
    }

    /**
     * Reads and trims the next line of user input.
     *
     * @return The trimmed input line.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints the farewell message shown when the user exits.
     */
    public void printGoodbye() {
        printBox("All set for now. See you next time.");
    }

    /**
     * Prints a message surrounded by horizontal divider lines.
     *
     * @param message Message to print.
     */
    public void printBox(String message) {
        output.println(LINE);
        output.println("   " + message);
        output.println(LINE);
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Total number of tasks after adding.
     */
    public void printAddedTask(Task task, int taskCount) {
        output.println(LINE);
        output.println("   Added to your list:");
        output.println("    " + task);
        output.println("   You now have " + taskCount + " tasks in your list.");
        output.println(LINE);
    }

    /**
     * Prints confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     */
    public void printMarked(Task task) {
        output.println(LINE);
        output.println("    Marked as complete:");
        output.println("    " + task);
        output.println(LINE);
    }

    /**
     * Prints confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void printUnmarked(Task task) {
        output.println(LINE);
        output.println("    Reopened this task:");
        output.println("    " + task);
        output.println(LINE);
    }

    /**
     * Prints confirmation that a task was removed.
     *
     * @param removedTask Task that was removed.
     * @param taskCount Total number of tasks after removing.
     */
    public void printDeleted(Task removedTask, int taskCount) {
        output.println(LINE);
        output.println("   Removed from your list:");
        output.println("     " + removedTask);
        output.println("   Now you have " + taskCount + " tasks in the list.");
        output.println(LINE);
    }

    /**
     * Prints confirmation that the task list was sorted.
     *
     * @param taskList Sorted task list to print.
     */
    public void printSorted(TaskList taskList) {
        output.println(LINE);
        output.println("   Here is your list, sorted by date and time:");

        for (int i = 0; i < taskList.size(); i++) {
            output.println("   " + (i + 1) + ". " + taskList.get(i));
        }

        output.println(LINE);
    }

    /**
     * Prints every task in the given list, numbered from 1.
     *
     * @param taskList Task list to print.
     */
    public void printList(TaskList taskList) {
        output.println(LINE);
        output.println("   Here is your task list:");

        for (int i = 0; i < taskList.size(); i++) {
            output.println("   " + (i + 1) + ". " + taskList.get(i));
        }

        output.println(LINE);
    }

    /**
     * Prints the tasks whose description matched a find keyword.
     *
     * @param matches Matching tasks, in list order.
     */
    public void printMatchingTasks(List<Task> matches) {
        output.println(LINE);
        output.println("   Here are the tasks that match:");

        if (matches.isEmpty()) {
            output.println("   No matching tasks found.");
        } else {
            for (int i = 0; i < matches.size(); i++) {
                output.println("   " + (i + 1) + ". " + matches.get(i));
            }
        }

        output.println(LINE);
    }

    /**
     * Prints the deadlines/events that fall within a checked date range.
     *
     * @param rangeStart First date of the checked range, inclusive.
     * @param rangeEnd Last date of the checked range, inclusive.
     * @param isWeek Whether the range is a week (changes the header wording).
     * @param matches Tasks that fall within the range, in display order.
     */
    public void printTasksInRange(LocalDate rangeStart, LocalDate rangeEnd, boolean isWeek,
            List<Task> matches) {
        output.println(LINE);

        if (isWeek) {
            output.println("   Here's what's happening from " + rangeStart.format(DISPLAY_DATE_FORMAT)
                    + " to " + rangeEnd.format(DISPLAY_DATE_FORMAT) + ":");
        } else {
            output.println("   Here's what's happening on " + rangeStart.format(DISPLAY_DATE_FORMAT) + ":");
        }

        if (matches.isEmpty()) {
            output.println("   Nothing scheduled.");
        } else {
            for (int i = 0; i < matches.size(); i++) {
                output.println("   " + (i + 1) + ". " + matches.get(i));
            }
        }

        output.println(LINE);
    }

    /**
     * Closes the input scanner. Call once, when the program is exiting.
     */
    public void close() {
        scanner.close();
    }
}
