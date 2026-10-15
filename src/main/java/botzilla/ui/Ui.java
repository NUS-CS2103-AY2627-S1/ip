package botzilla.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import botzilla.task.Task;
import botzilla.task.TaskList;

/**
 * Handles all console input and output for the chatbot: reading user
 * commands and printing responses, confirmations, and error messages.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER =
            "    ____        __        _ ____     \n"
                    + "   / __ )____  / /_____  (_) / /___ _\n"
                    + "  / __  / __ \\/ __/_  / / / / / __ `/\n"
                    + " / /_/ / /_/ / /_  / /_/ / / / /_/ / \n"
                    + "/_____/\\____/\\__/ /___/_/_/_/\\__,_/  \n";

    private Scanner scanner;

    /**
     * Constructs a Ui that reads input from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Prints the startup banner and welcome greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(formatGreeting());
        System.out.println(DIVIDER);
    }

    /**
     * Formats the greeting shown when the chatbot starts up. Used by the
     * GUI to display an opening message, without the ASCII banner that
     * only makes sense in a console.
     *
     * @return the greeting message
     */
    public String formatGreeting() {
        return "Hello there! I'm Botzilla.\nWhat can I do for you on this fine day?";
    }

    /**
     * Prints a plain divider line, used to separate sections of output.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints a warning shown when saved tasks fail to load on startup.
     */
    public void showLoadingError() {
        System.out.println(" HEY THERE!!! I couldn't load your saved tasks, starting fresh.");
        System.out.println(DIVIDER);
    }

    /**
     * Formats the goodbye message shown when the user exits. Used both by
     * the console (which prints it) and the GUI (which displays it in a
     * dialog box).
     *
     * @return the goodbye message
     */
    public String formatGoodbye() {
        return "Cheers! Have a great day ahead!";
    }

    /**
     * Formats an error message for an invalid or unrecognized command.
     *
     * @param message the error description
     * @return the formatted error message
     */
    public String formatError(String message) {
        return " HEY THERE!!! " + message;
    }

    /**
     * Formats a confirmation after a task has been added.
     *
     * @param task      the task that was added
     * @param taskCount the total number of tasks after adding
     * @return the formatted confirmation message
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return " Gotcha. I've added this task:\n"
                + "   " + task + "\n"
                + " You have " + taskCount + " tasks in the list.";
    }

    /**
     * Formats a confirmation after a task has been deleted.
     *
     * @param task      the task that was removed
     * @param taskCount the total number of tasks after removal
     * @return the formatted confirmation message
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        return " Gotcha. I've removed this task:\n"
                + "   " + task + "\n"
                + " You have " + taskCount + " tasks in the list.";
    }

    /**
     * Formats the result of a mark/unmark operation.
     *
     * @param message the confirmation message returned by the task
     * @return the formatted confirmation message
     */
    public String formatMarkResult(String message) {
        return " " + message;
    }

    /**
     * Formats the result of a tag/untag operation. When multiple tags are
     * added or removed in one command, {@code message} contains one line
     * per tag, separated by newlines.
     *
     * @param message the confirmation message(s) returned by the task
     * @return the formatted confirmation message
     */
    public String formatTagResult(String message) {
        return " " + message.replace("\n", "\n ");
    }

    /**
     * Formats the full list of tasks, numbered from 1.
     *
     * @param tasks the task list to display
     * @return the formatted task list
     */
    public String formatList(TaskList tasks) {
        StringBuilder sb = new StringBuilder(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("\n ").append(i + 1).append(".").append(tasks.get(i));
        }
        return sb.toString();
    }

    /**
     * Formats the deadlines/events occurring on a given date, or a
     * fallback message if none are found.
     *
     * @param date    the queried date
     * @param matches the tasks occurring on that date
     * @return the formatted results
     */
    public String formatOnDate(LocalDate date, List<Task> matches) {
        StringBuilder sb = new StringBuilder(" Okay bestie, here's what's on "
                + date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + ":");
        if (matches.isEmpty()) {
            sb.append("\n All clear! You are free as a lark!");
        } else {
            for (Task task : matches) {
                sb.append("\n ").append(task);
            }
        }
        return sb.toString();
    }

    /**
     * Formats the tasks matching a search, or a fallback message if none
     * are found.
     *
     * @param matches Tasks whose name contains the search keyword.
     * @return the formatted results
     */
    public String formatFindResults(List<Task> matches) {
        if (matches.isEmpty()) {
            return " No matching tasks found!";
        }
        StringBuilder sb = new StringBuilder(" Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            sb.append("\n ").append(i + 1).append(".").append(matches.get(i));
        }
        return sb.toString();
    }

    /**
     * Reads a single line of user input from standard input.
     *
     * @return the raw input text
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes the underlying input scanner. Should be called once when
     * the application is shutting down.
     */
    public void close() {
        scanner.close();
    }
}
