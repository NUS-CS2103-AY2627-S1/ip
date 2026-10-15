package clarry.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import clarry.task.Task;

/**
 * Handles all console input and output for Clarry.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Clarry's welcome message. */
    public void showWelcome() {
        String banner = "  _____ _\n"
                + " / ____| |\n"
                + "| |    | | __ _ _ __ _ __ _   _\n"
                + "| |    | |/ _` | '__| '__| | | |\n"
                + "| |____| | (_| | |  | |  | |_| |\n"
                + " \\_____|_|\\__,_|_|  |_|  \\__,  |\n"
                + "                          __/  |\n"
                + "                         |____/\n";
        printLines(
                banner,
                DIVIDER,
                " " + getWelcomeMessage().replace("\n", "\n "),
                DIVIDER);
    }

    /** Returns the shared greeting for the console and GUI. */
    public static String getWelcomeMessage() {
        return "Hi, I'm Clarry, your little task shark!\nWhat are we tackling today?";
    }

    /** Reads one full command line from the user. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : "bye";
    }

    /** Displays an error message. */
    public void showError(String message) {
        showResponse(getErrorMessage(message));
    }

    /** Displays response text between the standard divider lines. */
    public void showResponse(String response) {
        printLines(DIVIDER, response, DIVIDER);
    }

    /** Returns Clarry's goodbye response. */
    public String getGoodbyeMessage() {
        return " See you next tide! You've got this.";
    }

    /** Returns guidance for every command supported by Clarry. */
    public String getHelpMessage() {
        return " Need a course to follow? Here are my commands:\n"
                + " help - show this help message\n"
                + " list - show all tasks\n"
                + " todo DESCRIPTION - add a todo\n"
                + " deadline DESCRIPTION /by YYYY-MM-DD - add a deadline\n"
                + " event DESCRIPTION /from YYYY-MM-DD HH:mm /to YYYY-MM-DD HH:mm - add an event\n"
                + " on YYYY-MM-DD - show tasks occurring on a date\n"
                + " find KEYWORD - find tasks by description\n"
                + " mark NUMBER - mark a task as done\n"
                + " unmark NUMBER - mark a task as not done\n"
                + " delete NUMBER - delete a task\n"
                + " bye - exit Clarry";
    }

    /** Returns a formatted list of all tasks. */
    public String getListMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return " Your radar is clear! Add a task with 'todo DESCRIPTION'.";
        }
        return getTaskListMessage(" Here's what's on your radar:", tasks);
    }

    /** Returns a formatted list of tasks on the specified date. */
    public String getTasksOnDateMessage(LocalDate date, List<Task> tasks) {
        if (tasks.isEmpty()) {
            return " Clear waters! No tasks on " + date + ".";
        }
        return getTaskListMessage(" Here's what's on your radar for " + date + ":", tasks);
    }

    /** Returns a formatted list of tasks matching a search. */
    public String getFoundTasksMessage(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return " Nothing spotted! Try another keyword.";
        }
        return getTaskListMessage(" Here's what I spotted:", tasks);
    }

    /** Returns a task-addition response. */
    public String getAddedMessage(Task task, int taskCount) {
        return " Got it! Safely aboard your task list:\n"
                + "   " + task + "\n"
                + getTaskCountMessage(taskCount);
    }

    /** Returns a task-deletion response. */
    public String getDeletedMessage(Task task, int taskCount) {
        return " All clear! I've removed this task:\n"
                + "   " + task + "\n"
                + getTaskCountMessage(taskCount);
    }

    /** Returns a task-completion response. */
    public String getMarkedMessage(Task task) {
        return " Fin-tastic! One less thing to tackle:\n   " + task;
    }

    /** Returns a task-incomplete response. */
    public String getUnmarkedMessage(Task task) {
        return " Back on your radar:\n   " + task;
    }

    /** Keeps the task count readable for both one task and multiple tasks. */
    private String getTaskCountMessage(int taskCount) {
        return " You have " + taskCount + (taskCount == 1 ? " task" : " tasks") + " aboard.";
    }

    /** Returns an error response. */
    public String getErrorMessage(String message) {
        return " " + message;
    }

    /** Displays an error when saving fails. */
    public void showSaveError() {
        showError("Could not save tasks to disk.");
    }

    /** Displays an error when loading fails. */
    public void showLoadError() {
        showError("Could not load saved tasks.");
    }

    /** Displays an error for a malformed saved task. */
    public void showCorruptedLineError() {
        showError("Skipping a corrupted line in the save file.");
    }

    /** Builds a numbered task-list response with the supplied heading. */
    private String getTaskListMessage(String heading, List<Task> tasks) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n ").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }

    /** Prints each supplied line in the order received. */
    private void printLines(String... lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
