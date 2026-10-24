package echo.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import echo.task.Task;

/**
 * Handles all interaction with the user: reading raw command lines from
 * the console and printing every message Echo shows (banner, task
 * confirmations, errors, listings). Echo's main loop delegates all
 * console I/O here instead of calling System.out/Scanner directly.
 */
public class Ui {
    /**
     * Farewell message shown when the user exits. Public so Echo#getResponse
     * can return the exact same text for its "bye" shortcut without
     * duplicating the literal.
     */
    public static final String GOODBYE_MESSAGE = "Goodbye! Hope to echo with you again soon.";

    /**
     * Prefix every error message starts with. Public so the GUI can detect
     * an error reply (e.g. to style it differently) without duplicating
     * this literal.
     */
    public static final String ERROR_PREFIX = "OOPS!!!";

    private static final String LINE =
            "____________________________________________________________";

    private final Scanner sc;

    /** Creates a Ui that reads commands from standard input. */
    public Ui() {
        this.sc = new Scanner(System.in);
    }

    /** Prints the divider line used to separate sections of output. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Prints the startup banner and greeting. */
    public void showWelcome() {
        String banner = " _____      _           \n"
                + "| ____|___ | |__   ___  \n"
                + "|  _| / __|| '_ \\ / _ \\ \n"
                + "| |__| (__ | | | | (_) |\n"
                + "|_____\\___||_| |_|\\___/ \n";

        showLine();
        System.out.print(banner);
        System.out.println("Hello! I'm Echo.");
        System.out.println("Toss a task my way and I'll make sure it comes back around when you need it.");
        showLine();
    }

    /** Prints the farewell message shown when the user exits. */
    public void showGoodbye() {
        showLine();
        System.out.println(GOODBYE_MESSAGE);
        showLine();
    }

    /**
     * Reads one raw line of input from the user.
     */
    public String readCommand() {
        return sc.nextLine();
    }

    /** Closes the underlying input scanner. */
    public void close() {
        sc.close();
    }

    /** Prints an error message, prefixed to stand out as a failure. */
    public void showError(String message) {
        System.out.println(ERROR_PREFIX + " " + message);
    }

    /**
     * Prints each given line, in order. A varargs parameter fits here
     * better than a {@code List<String>} would, since every caller
     * already knows exactly how many lines it has (2 or 3, always a
     * fixed number for a given message) and passes them as literals
     * rather than building a collection first.
     */
    private void printLines(String... lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

    /**
     * Prints every task in the given list, numbered from 1, or a
     * persona-appropriate message if the list is empty.
     */
    public void showList(List<Task> items) {
        if (items.isEmpty()) {
            System.out.println("Your list is empty for now. Nothing to echo back yet.");
            return;
        }
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + "." + items.get(i));
        }
    }

    /**
     * Prints confirmation that a task was added, plus the new task count.
     *
     * @param t The task that was added.
     * @param newCount The list's size after adding it.
     */
    public void showTaskAdded(Task t, int newCount) {
        printLines("Echoed! I've added this task:",
                "  " + t,
                taskCountLine(newCount));
    }

    /**
     * Prints confirmation that a task was removed, plus the new task
     * count.
     *
     * @param t The task that was removed.
     * @param newCount The list's size after removing it.
     */
    public void showTaskRemoved(Task t, int newCount) {
        printLines("Noted. This task has been cleared from the list:",
                "  " + t,
                taskCountLine(newCount));
    }

    /**
     * Formats the "you now have N task(s)" line with correct singular/plural
     * wording, shared by {@link #showTaskAdded} and {@link #showTaskRemoved}.
     */
    private String taskCountLine(int count) {
        return "Now you have " + count + " task" + (count == 1 ? "" : "s") + " in the list.";
    }

    /** Prints confirmation that a task was marked as done. */
    public void showTaskMarked(Task t) {
        printLines("Loud and clear, that one's done:", "  " + t);
    }

    /** Prints confirmation that a task was marked as not done. */
    public void showTaskUnmarked(Task t) {
        printLines("Alright, that one goes back to pending:", "  " + t);
    }

    /**
     * Prints every task in the given list as occurring on the given
     * date, numbered from 1, or a "(none)" fallback if the list is
     * empty.
     *
     * @param date The date being queried (used only for the header text).
     * @param matches Tasks that occur on that date.
     */
    public void showTasksOnDate(LocalDate date, List<Task> matches) {
        System.out.println("Here are the tasks occurring on "
                + date.format(Task.DISPLAY_DATE_FORMAT) + ":");
        if (matches.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Prints confirmation that the given number of tasks were archived
     * and the active list is now empty, or a "nothing to archive"
     * message if the count is zero.
     *
     * @param count How many tasks were archived.
     */
    public void showArchived(int count) {
        if (count == 0) {
            System.out.println("There's nothing to archive. Your list is already empty.");
            return;
        }
        System.out.println("Archived " + count + " task(s). All clear for now.");
    }

    /**
     * Prints every task in the given list as a keyword match, numbered
     * from 1, or a "(none)" fallback if the list is empty.
     */
    public void showMatchingTasks(List<Task> matches) {
        System.out.println("Here are the matching tasks in your list:");
        if (matches.isEmpty()) {
            System.out.println("(none)");
            return;
        }
        for (int i = 0; i < matches.size(); i++) {
            System.out.println((i + 1) + "." + matches.get(i));
        }
    }
}
