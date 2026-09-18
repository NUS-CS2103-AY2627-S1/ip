package bot.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import bot.parser.TaskDateTime;
import bot.task.Task;

/**
 * Handles all interaction with the console: printing the startup/shutdown
 * messages, and reading each line of input. Per-command response text
 * (task list, confirmations, errors) is built by the {@code formatX}
 * methods below and returned as a String rather than printed directly, so
 * the same wording can be shown either on the console (by {@link bot.Bot})
 * or in a GUI dialog box (by {@link bot.gui.MainWindow}).
 */
public class Ui {
    private static final String DIVIDER = "    ____________________________________________________________";
    /** The assistant's name and personality anchor. */
    private static final String NAME = "Pip";
    private static final String BANNER = " ____   ___  _____ \n"
            + "| __ ) / _ \\|_   _|\n"
            + "|  _ \\| | | | | |  \n"
            + "| |_) | |_| | | |  \n"
            + "|____/ \\___/  |_|  \n";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the startup banner and greeting. */
    public void showWelcome() {
        showLine();
        System.out.print(BANNER);
        System.out.println(formatGreeting());
        showLine();
    }

    /** Formats the greeting shown at startup, without the console-only banner art. */
    public String formatGreeting() {
        return joinLines("     Hello! I'm " + NAME + ".", "     What can I do for you?");
    }

    /** Prints the closing message shown just before the program exits. */
    public void showFarewell() {
        showLine();
        System.out.println(formatFarewell());
        showLine();
    }

    /** Formats the closing message shown just before the program exits. */
    public String formatFarewell() {
        return joinLines("     Pip signing off! Keep conquering that task list.");
    }

    /** Prints a bare divider line, used to separate one command's output from the next. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /** Reads one line of user input. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : "bye";
    }

    /** Releases the console input resource; call once, when the program is exiting. */
    public void close() {
        scanner.close();
    }

    /**
     * Formats an "OOPS!!!" style message describing a problem with the
     * user's command or a failed save.
     */
    public String formatError(String message) {
        return joinLines("     " + message);
    }

    /** Prints the message for when the saved task file couldn't be loaded at startup. */
    public void showLoadingError(String message) {
        System.out.println(joinLines("     " + message));
        showLine();
    }

    /**
     * Prints a warning for each saved-task line that couldn't be parsed at startup, if any.
     * Prints nothing at all when {@code warnings} is empty.
     */
    public void showLoadWarnings(List<String> warnings) {
        if (warnings.isEmpty()) {
            return;
        }
        System.out.println("     Note: some saved tasks were skipped because the data file looks corrupted:");
        for (String warning : warnings) {
            System.out.println("       - " + warning);
        }
        showLine();
    }

    /** Formats the full numbered task list for the {@code list} command. */
    public String formatTaskList(List<Task> tasks) {
        return formatNumberedTasks("     Here are the tasks in your list:", tasks);
    }

    /** Formats the numbered tasks matching a keyword search, for the {@code find} command. */
    public String formatMatchingTasks(List<Task> matchingTasks) {
        return formatNumberedTasks("     Here are the matching tasks in your list:", matchingTasks);
    }

    /** Formats the numbered tasks occurring on a date, for the {@code on} command. */
    public String formatTasksOnDate(LocalDate date, List<Task> matchingTasks) {
        return formatNumberedTasks("     Here are the tasks on " + TaskDateTime.formatDateOnly(date) + ":",
                matchingTasks);
    }

    /** Formats the full numbered task list after reordering it, for the {@code sort} command. */
    public String formatSorted(List<Task> tasks) {
        return formatNumberedTasks("     Here are your tasks, sorted by date:", tasks);
    }

    /** Formats a lead-in line followed by each task in {@code tasks}, numbered from 1. */
    private String formatNumberedTasks(String leadIn, List<Task> tasks) {
        StringBuilder result = new StringBuilder(leadIn);
        for (int i = 0; i < tasks.size(); i++) {
            result.append('\n').append("     ").append(i + 1).append(".").append(tasks.get(i));
        }
        return result.toString();
    }

    /** Formats the confirmation shown after a task is added. */
    public String formatAdded(Task task, int taskCount) {
        return formatTaskCountUpdate("Got it. I've added this task:", task, taskCount);
    }

    /** Formats the confirmation shown after a task is deleted. */
    public String formatRemoved(Task task, int taskCount) {
        return formatTaskCountUpdate("Noted. I've removed this task:", task, taskCount);
    }

    /** Formats the confirmation shown after a task is marked done. */
    public String formatMarked(Task task) {
        return joinLines("     Nice! I've marked this task as done:", "       " + task);
    }

    /** Formats the confirmation shown after a task is marked not done. */
    public String formatUnmarked(Task task) {
        return joinLines("     OK, I've marked this task as not done yet:", "       " + task);
    }

    /**
     * Formats the shared "here's what changed, here's the task, here's the
     * new count" structure used by both {@link #formatAdded} and
     * {@link #formatRemoved}.
     */
    private String formatTaskCountUpdate(String leadIn, Task task, int taskCount) {
        String taskWord = (taskCount == 1) ? "task" : "tasks";
        return joinLines("     " + leadIn, "       " + task,
                "     Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /**
     * Joins each given line with a newline, in order. A variable number of
     * arguments lets each caller pass exactly the lines it has without
     * building a List first.
     */
    private static String joinLines(String... lines) {
        return String.join("\n", lines);
    }
}
