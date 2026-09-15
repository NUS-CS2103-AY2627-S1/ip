package jaku.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jaku.task.Task;
import jaku.task.TaskList;

/**
 * Handles Jaku's console input and all text displayed to the user.
 */
public class Ui {
    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Jaku";

    /** Horizontal line used to separate the chatbot's replies from user input. */
    private static final String DIVIDER = "____________________________________________________________";

    /** ASCII-art banner spelling out the chatbot's name. */
    private static final String BANNER = "     _     _     _  __ _   _ \n"
            + "    | |   / \\   | |/ /| | | |\n"
            + " _  | |  / _ \\  | ' / | | | |\n"
            + "| |_| | / ___ \\ | . \\ | |_| |\n"
            + " \\___/ /_/   \\_\\|_|\\_\\ \\___/ ";

    /** Source of commands typed into Jaku's console. */
    private final Scanner scanner;

    /** Output captured while Jaku creates a response for another UI. */
    private StringBuilder capturedOutput;

    /** Creates a user interface that reads commands from the standard input stream. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another input line is available.
     *
     * @return true when a command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return the next input line without surrounding whitespace
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints Jaku's welcome message. */
    public void showWelcome() {
        showLine();
        printLine(BANNER);
        printLine("Hello there! I'm " + NAME + ".");
        printLine("How can I help you today?");
        showLine();
    }

    /**
     * Shows a one-line framed reply.
     *
     * @param lines text lines to show to the user
     */
    public void showResponse(String... lines) {
        showLine();
        for (String line : lines) {
            printLine(line);
        }
        showLine();
    }

    /**
     * Shows a framed multi-line reply.
     *
     * @param lines lines to show to the user, in order
     */
    public void showResponse(List<String> lines) {
        showLine();
        for (String line : lines) {
            printLine(line);
        }
        showLine();
    }

    /**
     * Shows every task in a list, numbered from one.
     *
     * @param tasks tasks to show
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            showResponse("Your list is empty for now.");
            return;
        }
        showNumberedTasks("Here are the tasks in your list:", tasks.asList());
    }

    /**
     * Shows the tasks that match a user's find keyword, numbered from one.
     *
     * @param matches matching tasks in display order
     */
    public void showMatchingTasks(List<Task> matches) {
        if (matches.isEmpty()) {
            showResponse("No matching tasks found.");
            return;
        }
        showNumberedTasks("Here are the matching tasks in your list:", matches);
    }

    /**
     * Shows a heading followed by the supplied tasks numbered from one.
     *
     * @param heading introductory text for the task list
     * @param tasks tasks to show in their current order
     */
    private void showNumberedTasks(String heading, List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        lines.add(heading);
        for (int i = 0; i < tasks.size(); i++) {
            lines.add((i + 1) + "." + tasks.get(i));
        }
        showResponse(lines);
    }

    /** Prints the farewell message shown before Jaku shuts down. */
    public void showGoodbye() {
        printLine("Bye for now. Hope to see you again soon!");
        showLine();
    }

    /** Prints the standard horizontal divider. */
    public void showLine() {
        printLine(DIVIDER);
    }

    /** Starts capturing response output rather than writing it to the console. */
    public void startCapturing() {
        capturedOutput = new StringBuilder();
    }

    /**
     * Stops capturing and returns the captured response.
     *
     * @return response generated while capture was active
     */
    public String stopCapturing() {
        String output = capturedOutput.toString();
        capturedOutput = null;
        return output.stripTrailing();
    }

    /**
     * Prints one complete response without adding another response frame.
     *
     * @param response already framed response to print
     */
    public void showRaw(String response) {
        if (!response.isEmpty()) {
            System.out.println(response);
        }
    }

    /** Writes one line to the current output destination. */
    private void printLine(String line) {
        if (capturedOutput == null) {
            System.out.println(line);
        } else {
            capturedOutput.append(line).append(System.lineSeparator());
        }
    }
}
