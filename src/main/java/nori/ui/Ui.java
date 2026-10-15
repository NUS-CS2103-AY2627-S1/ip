package nori.ui;

import java.util.Scanner;

import nori.task.Task;
import nori.task.TaskList;

/**
 * Reads commands from the user and displays Nori's responses.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String WELCOME_GREETING = "Hello! I'm Nori.";
    private static final String WELCOME_PROMPT = "What can I do for you?";
    private static final String HELP_MESSAGE = String.join("\n",
            "Nori command guide",
            "",
            "ADD TASKS",
            "  • todo DESCRIPTION",
            "  • deadline DESCRIPTION /by DATE_TIME",
            "  • event DESCRIPTION /from START /to END",
            "",
            "MANAGE TASKS",
            "  • list - Show all tasks",
            "  • mark TASK_NUMBER - Mark as done",
            "  • unmark TASK_NUMBER - Mark as not done",
            "  • delete TASK_NUMBER - Remove a task",
            "  • find KEYWORD - Search descriptions",
            "",
            "GENERAL",
            "  • help - Show this guide",
            "  • bye - Exit Nori",
            "",
            "DATE_TIME",
            "  yyyy-MM-dd HHmm  or  d/M/yyyy HHmm");
    private static final String BANNER = " _   _            _ \n"
            + "| \\ | | ___  _ __(_) \n"
            + "|  \\| |/ _ \\| '__| |\n"
            + "| |\\  | (_) | |  | |\n"
            + "|_| \\_|\\___/|_|  |_|\n";

    private final Scanner scanner;

    /** Creates a command-line UI connected to standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Displays Nori's greeting. */
    public void showWelcome() {
        showDivider();
        showResponse(formatLines(BANNER, formatWelcome()));
        showDivider();
    }

    /** Returns Nori's welcome message with each part on a separate line. */
    public String formatWelcome() {
        return formatLines(WELCOME_GREETING, WELCOME_PROMPT);
    }

    /** Returns Nori's welcome message on one line. */
    public String formatInlineWelcome() {
        return WELCOME_GREETING + " " + WELCOME_PROMPT;
    }

    /** Returns guidance for all supported commands. */
    public String formatHelp() {
        return HELP_MESSAGE;
    }

    /** Returns whether another command is available from the input stream. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next command. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays a response to a command. */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Formats all tasks with one-based list numbers.
     *
     * @param tasks tasks to include.
     * @return formatted task list
     */
    public String formatTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            return "Your list is empty. Start with: todo read a book";
        }
        return formatNumberedTasks("Here are the tasks in your list:", tasks);
    }

    /**
     * Formats tasks that match a find command.
     *
     * @param tasks matching tasks to include.
     * @return formatted matching task list
     */
    public String formatMatchingTasks(TaskList tasks) {
        if (tasks.size() == 0) {
            return "No matching tasks. Search is case-sensitive; try another keyword.";
        }
        return formatNumberedTasks("Here are the matching tasks in your list:", tasks);
    }

    /** Formats tasks with one-based list numbers after a heading. */
    private String formatNumberedTasks(String heading, TaskList tasks) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            response.append('\n').append(i + 1).append('.').append(tasks.get(i));
        }
        return response.toString();
    }

    /**
     * Formats confirmation that a task was added.
     *
     * @return formatted confirmation
     */
    public String formatTaskAdded(Task task, int taskCount) {
        return formatLines(
                "Got it. I've added this task:",
                "  " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Formats confirmation that a task was deleted.
     *
     * @return formatted confirmation
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        return formatLines(
                "Noted. I've removed this task:",
                "  " + task,
                formatTaskCount(taskCount));
    }

    /**
     * Formats confirmation that a task's completion state changed.
     *
     * @return formatted confirmation
     */
    public String formatTaskStatusChanged(Task task, boolean isDone) {
        String message = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return formatLines(message, "  " + task);
    }

    /** Formats a recoverable error. */
    public String formatError(String message) {
        return "OOPS!!! " + message;
    }

    /** Displays the response separator. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Displays Nori's farewell. */
    public void showGoodbye() {
        showResponse(formatGoodbye());
        showDivider();
    }

    /** Returns Nori's farewell. */
    public String formatGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /** Formats the task count with correct singular or plural wording. */
    private String formatTaskCount(int taskCount) {
        return "Now you have " + taskCount + (taskCount == 1
                ? " task in the list." : " tasks in the list.");
    }

    /** Combines any number of response lines with newline separators. */
    private String formatLines(String... lines) {
        return String.join("\n", lines);
    }
}
