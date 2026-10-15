package elora;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import elora.task.Task;
import elora.task.TaskList;

/**
 * Deals with all interaction with the user: reading input and printing
 * output. This is the only class that touches System.in/System.out
 * directly.
 */
public class Ui {
    private static final String LOGO = " _____ _\n"
            + "| ____| | ___  _ __ __ _\n"
            + "|  _| | |/ _ \\| '__/ _` |\n"
            + "| |___| | (_) | | | (_| |\n"
            + "|_____|_|\\___/|_|  \\__,_|\n";
    private static final String LINE = "____________________________________________________________";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private Scanner scanner;

    /**
     * Creates a Ui that reads user input from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Joins a fixed number of lines with newlines, for messages built
     * from a handful of known parts rather than a runtime-sized
     * collection.
     *
     * @param lines The lines to join, in order.
     * @return The lines joined with newline separators.
     */
    private static String joinLines(String... lines) {
        assert lines.length > 0 : "joinLines is only ever called with a literal, non-empty list of lines";
        return String.join("\n", lines);
    }

    /**
     * Returns the startup banner and greeting, for the console UI.
     *
     * @return The welcome message, including the ASCII banner.
     */
    public String welcomeMessage() {
        return joinLines(LOGO,
                "Hello! I'm Elora - part friend, part philosopher, part guide.",
                "What can I do for you?");
    }

    /**
     * Returns the startup greeting without the ASCII banner, for the GUI
     * to show as its first chat bubble (an ASCII-art banner would just
     * look like garbled text in a proportional-font speech bubble).
     *
     * @return The welcome message, without the ASCII banner.
     */
    public String chatWelcomeMessage() {
        return joinLines("Hi, I'm Elora - part friend, part philosopher, part guide.",
                "What can I do for you today?");
    }

    /**
     * Returns a warning listing save-file lines that couldn't be
     * understood and were skipped on load, so the user knows some data
     * may be missing instead of silently losing it.
     *
     * @param skippedLines The raw save-file lines that were skipped.
     * @return The formatted warning, e.g. to append to the welcome message.
     */
    public String corruptedLinesWarning(List<String> skippedLines) {
        StringBuilder message = new StringBuilder(
                "Heads up - your save file had " + skippedLines.size()
                + (skippedLines.size() == 1 ? " line" : " lines")
                + " I couldn't read, so I've left them out:");
        for (String line : skippedLines) {
            message.append("\n  ").append(line);
        }
        return message.toString();
    }

    /**
     * Prints the horizontal divider line used to frame each response.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Reads and returns the next full line of user input.
     *
     * @return The line of input entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns the farewell message shown when the user exits.
     *
     * @return The goodbye message.
     */
    public String goodbyeMessage() {
        return "Bye for now, friend. Until our paths cross again!";
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param totalCount The total number of tasks now in the list.
     * @return The task-added confirmation message.
     */
    public String taskAddedMessage(Task task, int totalCount) {
        return joinLines("Got it. I've added this task:",
                "  " + task,
                "Now you have " + totalCount + " tasks in the list.");
    }

    /**
     * Returns confirmation that a task was marked as done.
     *
     * @param task The task that was marked.
     * @return The task-marked confirmation message.
     */
    public String taskMarkedMessage(Task task) {
        return joinLines("Nice! I've marked this task as done:", "  " + task);
    }

    /**
     * Returns confirmation that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     * @return The task-unmarked confirmation message.
     */
    public String taskUnmarkedMessage(Task task) {
        return joinLines("OK, I've marked this task as not done yet:", "  " + task);
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task The task that was removed.
     * @param totalCount The total number of tasks remaining in the list.
     * @return The task-deleted confirmation message.
     */
    public String taskDeletedMessage(Task task, int totalCount) {
        return joinLines("Noted. I've removed this task:",
                "  " + task,
                "Now you have " + totalCount + " tasks in the list.");
    }

    /**
     * Returns every task in the given list, numbered from 1.
     *
     * @param tasks The task list to display.
     * @return The formatted task list message.
     */
    public String taskListMessage(TaskList tasks) {
        return numberedTaskListMessage("Here are the tasks in your list:", tasks);
    }

    /**
     * Returns every task in the given list, numbered from 1, after it
     * has been sorted by date.
     *
     * @param tasks The task list to display, already sorted.
     * @return The formatted, sorted task list message.
     */
    public String taskListSortedMessage(TaskList tasks) {
        return numberedTaskListMessage("Got it. I've sorted your tasks by date:", tasks);
    }

    /**
     * Formats a task list as a header line followed by every task,
     * numbered from 1. Shared by taskListMessage() and
     * taskListSortedMessage(), which only differ in their header.
     *
     * @param header The line introducing the list.
     * @param tasks The task list to display.
     * @return The formatted, numbered task list message.
     */
    private String numberedTaskListMessage(String header, TaskList tasks) {
        StringBuilder message = new StringBuilder(header);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Returns the tasks matching a keyword search, or a message saying
     * none matched.
     *
     * @param matches The tasks whose description matched the search.
     * @return The formatted matching-tasks message.
     */
    public String matchingTasksMessage(List<Task> matches) {
        StringBuilder message = new StringBuilder("Here are the matching tasks in your list:");
        if (matches.isEmpty()) {
            message.append("\n  No tasks match that yet.");
        } else {
            for (int i = 0; i < matches.size(); i++) {
                message.append("\n").append(i + 1).append(".").append(matches.get(i));
            }
        }
        return message.toString();
    }

    /**
     * Returns the tasks occurring on a given date, or a message saying
     * there are none.
     *
     * @param date The date being queried.
     * @param matches The tasks that occur on that date.
     * @return The formatted tasks-on-date message.
     */
    public String tasksOnDateMessage(LocalDate date, List<Task> matches) {
        StringBuilder message = new StringBuilder(
                "Here's what's happening on " + date.format(DISPLAY_DATE_FORMAT) + ":");
        if (matches.isEmpty()) {
            message.append("\n  Nothing on your list for that day.");
        } else {
            for (Task task : matches) {
                message.append("\n  ").append(task);
            }
        }
        return message.toString();
    }

    /**
     * Closes the scanner reading from standard input. Should be called
     * once, when the application is exiting.
     */
    public void closeScanner() {
        scanner.close();
    }
}
