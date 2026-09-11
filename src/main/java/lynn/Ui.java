package lynn;

import java.io.PrintStream;
import java.util.Scanner;

/** Handles all command-line input and output for Lynn. */
class Ui {
    private static final String LINE = "____________________________________________________________";

    private static final String BANNER = " _                          \n"
            + "| |    _   _ _ __  _ __    \n"
            + "| |   | | | | '_ \\| '_ \\   \n"
            + "| |___| |_| | | | | | | |  \n"
            + "|_____|\\__, |_| |_|_| |_|  \n"
            + "       |___/               \n";

    private final Scanner scanner;

    private final PrintStream output;

    Ui() {
        this(System.out);
    }

    /** Creates a user interface that writes messages to the supplied stream. */
    Ui(PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    String readCommand() {
        return scanner.nextLine();
    }

    /** Prints any number of lines in the order they are supplied. */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }

    void showWelcome() {
        showLines(LINE, BANNER, "Hello! I'm Lynn, your upbeat study companion.",
                "Let's make progress one small step at a time!", "How can I help you today?", LINE);
    }

    void showLoadingError(String message) {
        showError(message);
    }

    void showError(String message) {
        showLines("OOPS! " + message, LINE);
    }

    void showGoodbye() {
        showLines("Bye. Hope to see you again soon!", LINE);
    }

    void showTaskList(TaskList tasks) {
        showLines("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println((i + 1) + "." + tasks.get(i));
        }
        showLines(LINE);
    }

    /** Displays the commands Lynn supports and a short description of each one. */
    void showHelp() {
        showLines("Here are Lynn's commands:",
                "  todo <description> - add a to-do task.",
                "  deadline <description> /by <yyyy-MM-dd> - add a deadline.",
                "  event <description> /from <start> /to <end> - add an event.",
                "  list - show all tasks.",
                "  find <keyword> - find matching tasks.",
                "  mark <number> / unmark <number> - update a task's status.",
                "  delete <number> - remove a task.",
                "  help - show this help message.",
                "  bye - exit Lynn.", LINE);
    }

    void showMatchingTasks(Task[] matchingTasks) {
        showLines("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.length; i++) {
            output.println((i + 1) + "." + matchingTasks[i]);
        }
        showLines(LINE);
    }

    void showTaskAdded(Task task, int taskCount) {
        showLines("Got it. I've added this task:", "  " + task,
                "Now you have " + taskCount + " tasks in the list.", LINE);
    }

    void showTaskMarked(Task task) {
        showLines("Nice! I've marked this task as done:", "  " + task, LINE);
    }

    void showTaskUnmarked(Task task) {
        showLines("OK, I've marked this task as not done yet:", "  " + task, LINE);
    }

    void showTaskDeleted(Task task, int taskCount) {
        showLines("Noted. I've removed this task:", "  " + task,
                "Now you have " + taskCount + " tasks in the list.", LINE);
    }
}
