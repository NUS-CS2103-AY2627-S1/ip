package clsl.ui.console;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import clsl.task.Task;

/** Formats application messages and reads console commands for the Clsl application. */
public class Ui {
    private static final String BANNER =
              "  ____ _     _ \n"
            + " / ___| |___| |\n"
            + "| |   | / __| |\n"
            + "| |___| \\__ \\ |\n"
            + " \\____|_|___/_|\n";
    private static final String GREETING = "Hello! I'm Clsl.\n"
            + "What can I do for you?";
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Reads one complete command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays a formatted application message in the console. */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /** Returns the application's welcome message. */
    public String formatWelcome() {
        return BANNER + "\n" + GREETING;
    }

    /** Returns the application's goodbye message. */
    public String formatGoodbye() {
        return "\nBye. Hope to see you again soon!";
    }

    /** Returns a message for a problem loading saved tasks. */
    public String formatLoadingError(String message) {
        return "\n" + message + "\n";
    }

    /** Returns an error message for an invalid command or failed operation. */
    public String formatError(String message) {
        return "\nERROR: " + message + "\n";
    }

    /** Returns a formatted list of all tasks. */
    public String formatTaskList(List<Task> tasks) {
        return formatTasks("Here are your tasks in your list:", tasks);
    }

    /** Returns a formatted list of tasks whose descriptions match a keyword. */
    public String formatMatchingTasks(List<Task> tasks) {
        return formatTasks("Here are the matching tasks in your list:", tasks);
    }

    /** Returns confirmation that a task has been marked as complete. */
    public String formatTaskMarked(Task task) {
        return "\nNice! I've marked this task as Done:\n" + task + "\n";
    }

    /** Returns confirmation that a task has been marked as incomplete. */
    public String formatTaskUnmarked(Task task) {
        return "\nOK, I've marked this task as not done yet:\n" + task + "\n";
    }

    /** Returns confirmation that a task was added. */
    public String formatTaskAdded(Task task, int taskCount) {
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.\n";
    }

    /** Returns confirmation that a task was removed. */
    public String formatTaskDeleted(Task task, int taskCount) {
        return "\nNoted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.\n";
    }

    /** Returns a formatted list of tasks that occur on a given date. */
    public String formatTasksOn(LocalDate date, List<Task> tasks) {
        String result = formatTasks("Here are the tasks occurring on " + date + ":", tasks);
        return tasks.isEmpty() ? result + "none\n" : result;
    }

    private String formatTasks(String heading, List<Task> tasks) {
        StringBuilder result = new StringBuilder("\n" + heading);
        for (int i = 0; i < tasks.size(); i++) {
            result.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return result.append("\n").toString();
    }
}
