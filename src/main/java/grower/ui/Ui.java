package grower.ui;

import java.util.List;
import java.util.Scanner;

import grower.tasks.Task;

/**
 * Handles command-line input and output for the application.
 */
public class Ui {
    /** Divider printed before and after each CLI command. */
    private static final String SEPARATOR = "-------------------------------------------------------------";

    /** Output produced by the current command for retrieval by the GUI. */
    private final StringBuilder output = new StringBuilder();

    /** Highest attention level recorded for the current command. */
    private ResponseType responseType = ResponseType.NORMAL;

    /** Reads commands entered through the command-line interface. */
    private final Scanner scanner;

    /** Whether recorded output should also be printed to the console. */
    private final boolean shouldPrintOutput;

    /**
     * Creates a command-line user interface that reads from standard input.
     */
    public Ui() {
        this(true);
    }

    /**
     * Creates an interface that can buffer command output until saving succeeds.
     *
     * @param shouldPrintOutput Whether to print messages to standard output.
     */
    public Ui(boolean shouldPrintOutput) {
        this.shouldPrintOutput = shouldPrintOutput;
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message and artwork.
     */
    public void showWelcome() {
        display("⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡠⠖⠒⠢⣄⣀⡀⣀⣀⠀⡠⠔⠒⠒⢤⡀⠀⠀⠀⠀⠀⠀\n"
                + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡴⡇⠀⠀⠀⠁⠠⡋⠀⠀⠙⠦⠀⠀⠀⠀⣧⠤⣀⠀⠀⠀⠀\n"
                + "⠀⠀⠀⠀⠀⠀⠀⡠⠖⠊⠑⠲⣄⣀⣠⠖⠘⠛⠀⠀⠀⠀⠀⠀⠀⠀⠁⠀⢸⠇⠀⠀⠀\n"
                + "⠀⠀⠀⠀⠀⠀⣸⣇⡀⠀⠀⠈⠁⠀⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⠋⠲⣄⠀⠀\n"
                + "⠀⠀⠀⠀⣠⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡼⠂⠀\n"
                + "⠀⠀⠀⢀⣧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠀⠀⢱⠀⠀⠀⠀⠀⠀⠀⠐⠺⡄⠀⠀\n"
                + "⠀⡠⠊⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠠⡀⠀⢀⡼⠀⠀⠀⠀⠀⠀⠀⠀⢀⡇⠀⠀\n"
                + "⢰⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⠈⠉⠁⡹⠀⠀⠀⣄⣀⡠⠟⢘⣯⣀⠀⠀\n"
                + "⠸⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⡷⠺⡍⠒⣿⣀⣠⡀⠀⠀⠀⠀⠀⠈⠀⠈⡷⠀\n"
                + "⠀⢸⠚⠉⠀⠀⠀⠀⠀⠀⠀⠀⢀⣶⠺⡁⠀⠙⠚⠀⠁⡏⢧⣀⡄⠀⠀⠀⠀⠐⠒⣇⠀\n"
                + "⠀⠸⣄⣀⣰⠀⠀⠀⠀⠀⠀⠲⣟⣿⡦⣷⠀⠀⠀⠀⢠⠁⣸⣿⣷⢶⡆⢀⣤⡀⣠⡾⠁\n"
                + "⠀⠀⠀⠀⠱⣀⠀⢀⡱⠄⠤⠜⠋⠻⡄⠀⠀⠀⠀⠀⣸⣴⡿⣏⠀⢀⣭⣁⣀⡽⠁⠀⠀\n"
                + "⠀⠀⠀⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠀⠸⠀⠀⠀⠀⠀⣿⡼⠁⠀⠉⠉⠀⠀⠀⠀⠀⠀⠀\n"
                + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡆⠀⠀⠀⠀⢿⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n"
                + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣧⠀⠀⠀⠀⠸⡀⠀⠀⠀⠀⠀ ___  ____   __   _  _      ____  ____\n"
                + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡼⠁⠀⠀⠀⠀⠈⣇⠀⠀⠀⠀ / __)(  _ \\ /  \\ / )( \\ ___(  __)(  _ \\\n"
                + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡴⠒⢋⣁⡀⠀⠀⠀⠀⠀⠘⠢⢄⣀ ( (_ \\ )   /(  O )\\ /\\ /(___)) _)  )   /\n"
                + "⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠉⠉⠁⠉⠙⠒⠤⣘⣗⠒⠒⠒⠚⠛⠃\\___/(__\\_) \\__/ (_/\\_)    (____)(__\\_)\n"
        );
        display("Goodday to you, I am Grow-er, your accountability partner! \n"
                + "i'm here to support your growth! What can I do for you today \n");
    }

    /**
     * Returns the next line of user input.
     *
     * @return User input, or null when the input stream ends.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Displays a separator between command interactions.
     */
    public void showSeparator() {
        display(SEPARATOR);
    }

    /**
     * Displays the task that was added.
     *
     * @param task Task that was added.
     */
    public void showTaskAdded(Task task) {
        display("Added:\n" + task + ". Our list grows!");
    }

    /**
     * Displays the task that was deleted.
     *
     * @param task Task that was deleted.
     */
    public void showTaskDeleted(Task task) {
        display("Removed:\n" + task + "\nHasty does it.");
    }

    /**
     * Displays the task that was marked as completed.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        display("Fine work, young one! Marking as done:", task.toString());
    }

    /**
     * Displays the task that was marked as not completed.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        display(
                "All trees take time to grow, "
                        + "lets work on this next time. Marking as undone:", task.toString());
    }

    /**
     * Displays all tasks with their one-based task numbers.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            display("Time to get to work! Grow the list.");
            return;
        }

        displayNumberedTasks(tasks);
    }

    /**
     * Displays an error message.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        responseType = ResponseType.ERROR;
        display(message);
    }

    /**
     * Displays an advisory without overriding an error from the same command.
     *
     * @param message Advisory text to display.
     */
    public void showWarning(String message) {
        if (responseType != ResponseType.ERROR) {
            responseType = ResponseType.WARNING;
        }
        display(message);
    }

    /**
     * Displays a message.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        display(message);
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        display("May the shade of many trees provide comfort as you depart!");
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param tasks Matching tasks to display.
     */
    public void showSearchResults(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showWarning("No results!!!!");
            return;
        }

        displayNumberedTasks(tasks);
    }

    /**
     * Displays tasks in the supplied order with consecutive one-based numbers.
     */
    private void displayNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            display((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Records and displays one or more response messages.
     *
     * @param messages Messages to record in the supplied order.
     */
    private void display(String... messages) {
        for (String message : messages) {
            if (!output.isEmpty()) {
                output.append(System.lineSeparator());
            }

            output.append(message);
            if (shouldPrintOutput) {
                System.out.println(message);
            }
        }
    }

    /**
     * Publishes a buffered command result after persistence has succeeded.
     *
     * @param message Command output.
     * @param type Attention level of the command output.
     */
    public void showResponse(String message, ResponseType type) {
        responseType = type;
        display(message);
    }

    /**
     * Removes output left by the previous command.
     */
    public void clearOutput() {
        output.setLength(0);
        responseType = ResponseType.NORMAL;
    }

    /**
     * Returns the current response's attention level for GUI styling.
     *
     * @return Current response type.
     */
    public ResponseType getResponseType() {
        return responseType;
    }

    /**
     * Returns output produced by the current command.
     *
     * @return Current response text.
     */
    public String getOutput() {
        return output.toString();
    }
}
