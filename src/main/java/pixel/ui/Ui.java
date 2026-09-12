package pixel.ui;

import java.util.Scanner;
import java.util.function.Consumer;

import pixel.task.Task;

/**
 * Handles console input and output for Pixel.
 */
public class Ui {
    private static final String LINE =
            "____________________________________________________________";
    private static final String BANNER = " ____  _          _ \n"
            + "|  _ \\(_)_  _____| |\n"
            + "| |_) | \\ \\/ / _ \\ |\n"
            + "|  __/| |>  <  __/ |\n"
            + "|_|   |_/_/\\_\\___|_|";

    private final Scanner scanner;
    private final Consumer<String> messageOutput;

    /**
     * Creates a UI connected to the process's standard input and output.
     */
    public Ui() {
        this(System.out::println);
    }

    /**
     * Creates a UI that sends messages to the supplied output handler.
     *
     * @param messageOutput Handler for messages produced by Pixel.
     */
    public Ui(Consumer<String> messageOutput) {
        scanner = new Scanner(System.in);
        this.messageOutput = messageOutput;
    }

    /** Displays Pixel's startup banner and greeting. */
    public void showWelcome() {
        showLine();
        showMessage(BANNER);
        showMessage("Hello! I'm Pixel.");
        showMessage("What can I do for you?");
        showLine();
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return Whether another command is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return The next trimmed command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays one message, followed by a line break.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        messageOutput.accept(message);
    }

    /**
     * Displays the separator used between command responses.
     */
    public void showLine() {
        showMessage(LINE);
    }

    /**
     * Displays the standard confirmation after a task is added.
     *
     * @param task Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showMessage("Got it. I've added this task:");
        showMessage("  " + task);
        showMessage("Now you have " + taskCount + " tasks in the list.");
    }
}
