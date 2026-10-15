package pulbot.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Task;
import pulbot.task.TaskList;

/** Handles Pulbot's console input and output. */
public class Ui {
    private static final String INDENT = "    ";
    private static final String SEPARATOR = INDENT + "_".repeat(80);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final String WELCOME_MESSAGE = """
            Hello! I'm Pulbot.
            Enter your tasks and I will add them to your list.
              Type 'todo <description>' to add a todo.
              Type 'deadline <description> /by <when>' to add a deadline (d/M/yyyy HHmm).
              Type 'event <description> /from <start> /to <end>' to add an event (d/M/yyyy HHmm).
              Type 'list' to view your list.
              Type 'find <keyword>' to search your task descriptions.
              Type 'on <date>' to view deadlines and events on a date (d/M/yyyy).
              Type 'mark <number>' to mark a task as done.
              Type 'unmark <number>' to unmark a task.
              Type 'delete <number>' to remove a task.
              Type 'bye' to exit.
            """.strip();
    private final PrintStream output;
    private final Scanner scanner;

    /** Creates a console user interface backed by standard input. */
    public Ui() {
        this(System.in, System.out);
    }

    /** Creates a console user interface backed by the supplied streams. */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /** Displays Pulbot's startup instructions. */
    public void showWelcome() {
        output.print(getWelcomeMessage());
    }

    /** Returns Pulbot's startup instructions. */
    public static String getWelcomeMessage() {
        String lineSeparator = System.lineSeparator();
        String indentedMessage = WELCOME_MESSAGE.replace("\n", lineSeparator + INDENT + " ");
        return getBanner() + lineSeparator
                + INDENT + " " + indentedMessage + lineSeparator
                + SEPARATOR + lineSeparator + lineSeparator;
    }

    /** Returns the welcome prompt without terminal formatting. */
    public static String getWelcomePrompt() {
        return WELCOME_MESSAGE;
    }

    /** Returns the Pulbot ASCII banner. */
    public static String getBanner() {
        return """
                        ##### ##                ###         ##### ##
                     ######  /###                ###     ######  /##
                    /#   /  /  ###                ##    /#   /  / ##                  #
                   /    /  /    ###               ##   /    /  /  ##                 ##
                       /  /      ##               ##       /  /   /                  ##
                      ## ##      ## ##   ####     ##      ## ##  /        /###     ########
                      ## ##      ##  ##    ###  / ##      ## ## /        / ###  / ########
                    /### ##      /   ##     ###/  ##      ## ##/        /   ###/     ##
                   / ### ##     /    ##      ##   ##      ## ## ###    ##    ##      ##
                      ## ######/     ##      ##   ##      ## ##   ###  ##    ##      ##
                      ## ######      ##      ##   ##      #  ##     ## ##    ##      ##
                      ## ##          ##      ##   ##         /      ## ##    ##      ##
                      ## ##          ##      /#   ##     /##/     ###  ##    ##      ##
                      ## ##           ######/ ##  ### / /  ########     ######       ##
                 ##   ## ##            #####   ##  ##/ /     ####        ####         ##
                ###   #  /                             #
                 ###    /                               ##
                  #####/
                    ###
                """;
    }

    /** Returns whether another input line is available. */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /** Reads and returns the next input line. */
    public String nextLine() {
        return scanner.nextLine();
    }

    /** Displays a separator between interaction turns. */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /** Displays a blank line between interaction turns. */
    public void showBlankLine() {
        output.println();
    }

    /** Displays the exit message. */
    public void showBye() {
        output.println(INDENT + " So soon? Just say you hate me. Bye.");
    }

    /** Displays an error message. */
    public void showError(String message) {
        output.println(INDENT + " \u001B[31m \u26A0 ERROR \u26A0 \u001B[0m" + message);
    }

    /** Displays confirmation for an added task. */
    public void showAddedTask(Task task, int taskCount) {
        output.println(INDENT + " I have added this task:");
        output.println(INDENT + "   " + task);
        showTaskCount(taskCount);
    }

    /** Displays the current number of tasks. */
    public void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println(INDENT + " Now you have " + taskCount + " " + taskWord + " in the list.");
    }

    /** Displays confirmation for a marked task. */
    public void showMarked(Task task) {
        output.println(INDENT + " I've marked this task as done:");
        output.println(INDENT + "   " + task);
    }

    /** Displays confirmation for an unmarked task. */
    public void showUnmarked(Task task) {
        output.println(INDENT + " I've unmarked this task:");
        output.println(INDENT + "   " + task);
    }

    /** Displays confirmation for a deleted task. */
    public void showDeleted(Task task, int taskCount) {
        output.println(INDENT + " I have deleted this task:");
        output.println(INDENT + "   \u001B[31m" + task + "\u001B[0m");
        showTaskCount(taskCount);
    }

    /** Displays every task in the supplied list. */
    public void showList(TaskList tasks) {
        if (tasks.isEmpty()) {
            output.println(INDENT + " Your list is empty.");
            return;
        }
        output.println(INDENT + " Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(INDENT + " " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays tasks whose descriptions contain the supplied keyword. */
    public void showMatchingTasks(TaskList tasks, String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        List<Integer> matchingIndices = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription()
                        .toLowerCase(Locale.ENGLISH).contains(normalizedKeyword))
                .boxed()
                .toList();

        if (matchingIndices.isEmpty()) {
            output.println(INDENT + " No matching tasks found.");
            return;
        }
        output.println(INDENT + " Here are the matching tasks in your list:");
        matchingIndices.forEach(index ->
                output.println(INDENT + " " + (index + 1) + "." + tasks.get(index)));
    }

    /** Displays deadlines and events occurring on the supplied date. */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (occursOnDate(task, date)) {
                if (!found) {
                    output.println(INDENT + " Tasks on " + formatDate(date) + ":");
                }
                output.println(INDENT + " " + (i + 1) + "." + task);
                found = true;
            }
        }
        if (!found) {
            output.println(INDENT + " No deadlines or events on " + formatDate(date) + ".");
        }
    }

    /** Closes the console input scanner. */
    public void close() {
        scanner.close();
    }

    private String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /** Returns whether a deadline or event occurs on the supplied date. */
    private boolean occursOnDate(Task task, LocalDate date) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy().toLocalDate().equals(date);
        }
        if (task instanceof Event event) {
            LocalDate startDate = event.getFrom().toLocalDate();
            LocalDate endDate = event.getTo().toLocalDate();
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        }
        return false;
    }
}
