package meowmeow.ui;

import meowmeow.task.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Builds the text shown to the user and reads commands in console mode.
 * Formatting methods return strings so that the same text can be printed
 * to the console or displayed in a graphical interface.
 */
public class Ui {
    private static final String LINE = "    ____________________________________________________________";
    private static final String TASK_INDENT = "  ";
    private static final DateTimeFormatter DATE_HEADING =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final String COMMANDS =
            "todo, deadline, event, list, mark, unmark, delete, on, find, bye";
    private final Scanner scanner;

    /**
     * Constructs a user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns the next line of input typed by the user, with surrounding
     * whitespace removed.
     *
     * @return Trimmed line of user input.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Releases the input scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Prints the response to the console with divider lines.
     *
     * @param response Text to print, which may span several lines.
     */
    public void printToConsole(String response) {
        System.out.println(LINE);
        for (String line : response.split("\n")) {
            System.out.println("     " + line);
        }
        System.out.println(LINE);
    }

    /**
     * Returns the lines joined into a single response.
     *
     * @param lines Lines of text to show the user.
     * @return The lines separated by new lines.
     */
    public String formatMessage(String... lines) {
        return String.join("\n", lines);
    }


    /**
     * Returns the logo and greeting shown when the chatbot starts.
     *
     * @return Welcome text.
     */
    public String formatWelcome() {
        return formatMessage("Hello! I'm MeowMeow.",
                "What can I do for you? Meow :>",
                "",
                "I understand: " + COMMANDS);    }

    /**
     * Returns the farewell message shown before the chatbot exits.
     *
     * @return Farewell text.
     */
    public String formatFarewell() {
        return formatMessage("Bye. Hope to see you again soon! Meow :>");
    }


    /**
     * Returns the given error message formatted for display.
     *
     * @param message Explanation of what went wrong.
     * @return Error text.
     */
    public String formatError(String message) {
        return message;
    }

    /**
     * Returns a warning that the save file could not be read.
     *
     * @param message Explanation of the loading failure.
     * @return Warning text.
     */
    public String formatLoadingError(String message) {
        return formatMessage(message + " Starting with an empty list.");
    }

    /**
     * Returns a message followed by the task it refers to.
     *
     * @param message Text describing what happened to the task.
     * @param task Task the message refers to.
     * @return Message text with the task on its own line.
     */
    public String formatTaskMessage(String message, Task task) {
        return formatMessage(message, TASK_INDENT + task);
    }

    /**
     * Returns confirmation that a task was added, along with the new list size.
     *
     * @param task Task that was added.
     * @param totalCount Number of tasks in the list after the addition.
     * @return Confirmation text.
     */
    public String formatAdded(Task task, int totalCount) {
        return formatTaskCountMessage("Meow! Got it. I've added this task:", task, totalCount);
    }

    /**
     * Returns confirmation that a task was removed, along with the new list size.
     *
     * @param task Task that was removed.
     * @param totalCount Number of tasks remaining in the list.
     * @return Confirmation text.
     */
    public String formatRemoved(Task task, int totalCount) {
        return formatTaskCountMessage("Meow! Noted. I've removed this task:", task, totalCount);
    }

    /**
     * Returns the given tasks as a numbered list, or a message if there are none.
     *
     * @param tasks Tasks to display.
     * @return Numbered list text.
     */
    public String formatList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            return "Your list is empty. Nothing to do yet!";
        }
        return formatNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Returns the tasks occurring on the given date, or a message if there are none.
     *
     * @param date Date the tasks were filtered by.
     * @param matches Tasks occurring on that date.
     * @return Numbered list text with a date heading.
     */
    public String formatTasksOn(LocalDate date, ArrayList<Task> matches) {
        String heading = "Tasks on " + date.format(DATE_HEADING) + ":";
        if (matches.isEmpty()) {
            return formatMessage(heading, "Nothing scheduled. Enjoy the free time! Meow :>");
        }
        return formatNumberedTasks(heading, matches);
    }

    /**
     * Returns the message shown when a command word is not recognised.
     *
     * @param commandWord Word the user typed.
     * @return Error text naming the word and listing the known commands.
     */
    public String formatUnknownCommand(String commandWord) {
        return formatMessage("I don't know what \"" + commandWord + "\" means.",
                "I understand: " + COMMANDS);
    }

    /**
     * Returns the tasks matching a search, or a message if there are none.
     *
     * @param matches Tasks whose descriptions matched the keyword.
     * @return Numbered list text of matching tasks.
     */
    public String formatMatches(ArrayList<Task> matches) {
        if (matches.isEmpty()) {
            return "No matching tasks found. Meow?";
        }
        return formatNumberedTasks("Here are the matching tasks in your list:", matches);
    }

    /**
     * Returns the given tasks as numbered lines beneath the given heading.
     *
     * @param heading Line introducing the list.
     * @param tasks Tasks to number, in display order.
     * @return Heading followed by one numbered line per task.
     */
    private String formatNumberedTasks(String heading, ArrayList<Task> tasks) {
        StringBuilder builder = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            builder.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return builder.toString();
    }

    /**
     * Returns a message about a task, followed by the resulting list size.
     *
     * @param message Text describing what happened to the task.
     * @param task Task the message refers to.
     * @param totalCount Number of tasks in the list afterwards.
     * @return Message text with the task and the new list size.
     */
    private String formatTaskCountMessage(String message, Task task, int totalCount) {
        return formatMessage(message,
                TASK_INDENT + task,
                "Now you have " + totalCount + " task(s) in the list.");
    }
}