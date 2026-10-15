package jassabot.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import jassabot.task.Task;

/**
 * Formats shared messages for the console or a GUI response collector.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";
    private static final String BANNER = "   _                         ____        _\n"
            + "  | | __ _ ___ ___  __ _    | __ )  ___ | |_\n"
            + "  | |/ _` / __/ __|/ _` |   |  _ \\ / _ \\| __|\n"
            + "  | | (_| \\__ \\__ \\ (_| |   | |_) | (_) | |_\n"
            + " _|_|\\__,_|___/___/\\__,_|   |____/ \\___/ \\__|";

    private final Scanner scanner;
    private final Consumer<String> output;
    private final boolean hasConsoleDecorations;

    /**
     * Creates a UI connected to the process's standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out::println;
        hasConsoleDecorations = true;
    }

    /**
     * Creates a message formatter that sends plain response lines to a GUI collector.
     *
     * @param output Destination for each formatted line.
     */
    public Ui(Consumer<String> output) {
        scanner = new Scanner("");
        this.output = output;
        hasConsoleDecorations = false;
    }

    /**
     * Displays a complete command response with the console's original dividers.
     *
     * @param response Message returned by the chatbot.
     */
    public void showResponse(String response) {
        showResponseStart();
        output.accept(response);
        showResponseStart();
    }

    /**
     * Returns whether another command can be read without consuming it.
     *
     * @return {@code true} when another input line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return Next command line without surrounding whitespace.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the application banner and greeting. */
    public void showWelcome() {
        showResponseStart();
        if (hasConsoleDecorations) {
            output.accept(BANNER);
        }
        showLines("Hello, I'm JassaBot.", "Let's make room for a little progress today.",
                "Type help to see available commands.");
        showResponseStart();
    }

    /** Displays the command reference shared by the console and GUI. */
    public void showHelp() {
        showLines("JassaBot commands:",
                "todo DESCRIPTION - Add a task.",
                "deadline DESCRIPTION /by DATE [TIME] - Add a deadline.",
                "event DESCRIPTION /from DATE [TIME] /to DATE [TIME] - Add an event.",
                "list - Show all tasks and their numbers.",
                "find KEYWORD - Find descriptions containing KEYWORD, ignoring case.",
                "mark NUMBER - Mark a task as done.",
                "unmark NUMBER - Mark a task as not done.",
                "delete NUMBER - Delete a task.",
                "help - Show this help.",
                "bye - Exit JassaBot.",
                "",
                "Replace uppercase placeholders with your values. [TIME] is optional.",
                "Commands are lowercase. Dates: yyyy-MM-dd or d/M/yyyy. Time: 24-hour HHmm.",
                "Use numbers from list for mark, unmark, and delete; find renumbers its results.",
                "",
                "Examples:",
                "deadline return book /by 2019-12-02 1800",
                "event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        showResponseStart();
    }

    /**
     * Sends each message line to the configured output in order.
     *
     * @param lines Message lines to display.
     */
    private void showLines(String... lines) {
        for (String line : lines) {
            output.accept(line);
        }
    }

    /**
     * Displays non-fatal problems found while loading saved tasks.
     *
     * @param warnings User-facing storage warnings.
     */
    public void showLoadingWarnings(List<String> warnings) {
        for (String warning : warnings) {
            output.accept("WARNING: " + warning);
        }
        if (!warnings.isEmpty()) {
            showResponseStart();
        }
    }

    /** Displays the divider that begins a response to a command. */
    public void showResponseStart() {
        if (hasConsoleDecorations) {
            output.accept(DIVIDER);
        }
    }

    /**
     * Displays every task in its current list position.
     *
     * @param tasks Tasks currently stored by the application.
     */
    public void showTaskList(List<Task> tasks) {
        output.accept(tasks.isEmpty() ? "Your garden is clear. Enjoy the breathing room."
                : "Here's what's growing in your task list:");
        showNumberedTasks(tasks);
        showResponseStart();
    }

    /**
     * Displays tasks whose descriptions matched a search keyword.
     *
     * @param tasks matching tasks in their original list order
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.accept(tasks.isEmpty() ? "No matching tasks this time. Try another keyword."
                : "Here's what I found in your task garden:");
        showNumberedTasks(tasks);
        showResponseStart();
    }

    /**
     * Displays tasks as a one-based numbered list.
     *
     * @param tasks tasks to display in the supplied order
     */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output.accept((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was marked as completed.
     *
     * @param task Task that was marked as completed.
     */
    public void showTaskMarked(Task task) {
        showLines("A little progress, a little growth. Task completed:", "  " + task);
        showResponseStart();
    }

    /**
     * Displays confirmation that a task was marked as incomplete.
     *
     * @param task Task that was marked as incomplete.
     */
    public void showTaskUnmarked(Task task) {
        showLines("Room to grow. This task is marked as not done:", "  " + task);
        showResponseStart();
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task Task that was deleted.
     * @param numberOfTasks Number of tasks remaining in the list.
     */
    public void showTaskDeleted(Task task, int numberOfTasks) {
        showLines("Removed this task. More room for what matters:",
                "  " + task,
                formatTaskCount(numberOfTasks));
        showResponseStart();
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param numberOfTasks Number of tasks now in the list.
     */
    public void showTaskAdded(Task task, int numberOfTasks) {
        showLines("Planted a new task:",
                "  " + task,
                formatTaskCount(numberOfTasks));
        showResponseStart();
    }

    /**
     * Returns a task count with the correct singular or plural noun.
     */
    private String formatTaskCount(int numberOfTasks) {
        String taskWord = numberOfTasks == 1 ? "task" : "tasks";
        return "Your garden now holds " + numberOfTasks + " " + taskWord + ".";
    }

    /** Displays the shared invalid-task-number response. */
    public void showInvalidTaskNumber() {
        output.accept("Please enter a valid task number.");
        showResponseStart();
    }

    /**
     * Displays a recoverable command error.
     *
     * @param message User-facing explanation of the error.
     */
    public void showError(String message) {
        output.accept(message);
        showResponseStart();
    }

    /** Displays the normal farewell requested by the {@code bye} command. */
    public void showGoodbye() {
        output.accept("Bye for now. Take your time, and keep growing.");
        showResponseStart();
    }

    /** Displays the farewell used when the input stream closes. */
    public void showInputClosed() {
        showResponseStart();
        output.accept("Input closed. Take care, and keep growing.");
        showResponseStart();
    }
}
