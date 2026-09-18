package rem.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import rem.task.Task;

/**
 * Handles console input and output for Rem.
 */
public class Ui implements AutoCloseable {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String BANNER = " ____                      \n" // Used Codex to generate ASCII
            + "|  _ \\    ___    _ __ ___  \n"
            + "| |_) |  / _ \\  | '_ ` _ \\ \n"
            + "|  _ <  |  __/  | | | | | |\n"
            + "|_| \\_\\  \\___|  |_| |_| |_|\n";

    private final Scanner scanner;
    private final Consumer<String> output;
    private boolean hasError;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        output = message -> message.lines().forEach(line -> System.out.println("Rem: " + line));
    }

    /**
     * Creates a message-only UI for a graphical conversation.
     *
     * @param output Consumer receiving each response line.
     */
    public Ui(Consumer<String> output) {
        scanner = null;
        this.output = output;
    }

    /**
     * Displays Rem's welcome message.
     *
     * @param hasLoadError Whether loading the saved tasks failed.
     */
    public void showWelcome(boolean hasLoadError) {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println("Rem: Hi! I'm Rem.");
        System.out.println("Rem: I can help! Then maybe a nap.");
        if (hasLoadError) {
            System.out.println("Rem: Oh. I couldn't load your saved tasks. I'm showing an empty list.");
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Prompts for and reads the next command.
     *
     * @return The command entered by the user, or {@code null} if the input stream is closed.
     */
    public String readCommand() {
        System.out.print("Me: ");
        System.out.flush();
        if (!scanner.hasNextLine()) {
            return null;
        }
        String command = scanner.nextLine();
        System.out.println(SEPARATOR);
        return command;
    }

    /**
     * Displays one message spoken by Rem.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        output.accept(message);
    }

    /**
     * Reports an error while preserving the console's existing message format.
     *
     * @param message Explanation of the failure.
     */
    public void showError(String message) {
        hasError = true;
        showMessage(message);
    }

    public boolean hasError() {
        return hasError;
    }

    /**
     * Displays each supplied message in order, or nothing if no messages are supplied.
     *
     * @param messages Lines spoken by Rem.
     */
    public void showMessages(String... messages) {
        for (String message : messages) {
            showMessage(message);
        }
    }

    /**
     * Displays tasks as a one-based numbered list.
     *
     * @param tasks Tasks to display in their current order.
     */
    public void showNumberedTasks(List<Task> tasks) {
        for (int taskIndex = 0; taskIndex < tasks.size(); taskIndex++) {
            showMessage((taskIndex + 1) + "." + tasks.get(taskIndex));
        }
    }

    /**
     * Displays the separator between command responses.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Closes the input scanner.
     */
    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
