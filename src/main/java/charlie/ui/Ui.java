package charlie.ui;

import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Handles console input and presents messages to Charlie's user.
 */
public class Ui implements AutoCloseable {
    /** ASCII-art banner displayed when Charlie starts. */
    private static final String BOT_BANNER = "  ____ _   _    _    ____  _     ___ _____\n"
            + " / ___| | | |  / \\  |  _ \\| |   |_ _| ____|\n"
            + "| |   | |_| | / _ \\ | |_) | |    | ||  _|\n"
            + "| |___|  _  |/ ___ \\|  _ <| |___ | || |___\n"
            + " \\____|_| |_/_/   \\_\\_| \\_\\_____|___|_____|\n";
    /** Separator displayed around each command response. */
    private static final String BOT_HORIZONTAL_LINE =
            "____________________________________________________________";

    /** Indentation applied to Charlie's console output. */
    private static final String BOT_INDENT = "    ";

    /** Name used in Charlie's greeting. */
    private static final String BOT_NAME = "Charlie";

    /** Reads commands from standard input. */
    private final Scanner scanner;

    /** Receives each message produced by Charlie. */
    private final Consumer<String> outputConsumer;

    /** Indicates whether output should use console-specific formatting. */
    private final boolean isConsoleUi;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.outputConsumer = System.out::println;
        this.isConsoleUi = true;
    }

    /**
     * Creates a user interface that sends unindented messages to the given consumer.
     * This is used to collect command responses for the graphical interface.
     *
     * @param outputConsumer Consumer that receives each output line.
     */
    public Ui(Consumer<String> outputConsumer) {
        this.scanner = null;
        this.outputConsumer = outputConsumer;
        this.isConsoleUi = false;
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return True when another input line can be read.
     */
    public boolean hasNextCommand() {
        assert scanner != null : "Only the console UI can read commands";
        return scanner.hasNextLine();
    }

    /**
     * Reads the next complete command entered by the user.
     *
     * @return The next input line.
     */
    public String readCommand() {
        assert scanner != null : "Only the console UI can read commands";
        return scanner.nextLine();
    }

    /**
     * Displays Charlie's startup banner and greeting.
     */
    public void showIntro() {
        showHorizontalLine();
        for (String line : BOT_BANNER.split("\n")) {
            showMessage(line);
        }
        showMessages(
                "Oh, hello! I'm " + BOT_NAME + ".",
                "Ready to tackle some tasks together?");
        showHorizontalLine();
    }

    /**
     * Displays Charlie's farewell message.
     */
    public void showOutro() {
        showMessage("Bye for now! I'll guard the task nest until you return.");
        showHorizontalLine();
    }

    /**
     * Displays an error encountered while loading saved tasks.
     *
     * @param message Explanation of the loading problem.
     */
    public void showLoadingError(String message) {
        outputConsumer.accept("Error loading saved tasks: " + message);
    }

    /**
     * Displays one message using Charlie's standard indentation.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        String indent = isConsoleUi ? BOT_INDENT : "";
        outputConsumer.accept(indent + message);
    }

    /**
     * Displays multiple messages in the given order using Charlie's standard indentation.
     *
     * @param messages Messages to display.
     */
    public void showMessages(String... messages) {
        for (String message : messages) {
            showMessage(message);
        }
    }

    /**
     * Displays the current number of tasks using the correct singular or plural wording.
     *
     * @param taskCount Number of tasks currently stored.
     */
    public void showTaskCount(int taskCount) {
        String verb = taskCount == 1 ? "is" : "are";
        String noun = taskCount == 1 ? "task" : "tasks";
        showMessage("There " + verb + " now " + taskCount + " " + noun + " in our nest.");
    }

    /**
     * Displays the separator used to group each command and response.
     */
    public void showHorizontalLine() {
        if (isConsoleUi) {
            showMessage(BOT_HORIZONTAL_LINE);
        }
    }

    /**
     * Closes the standard-input scanner when Charlie exits.
     */
    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
