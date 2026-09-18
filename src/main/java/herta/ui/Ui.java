package herta.ui;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Handles user interaction for Herta, including console input and output.
 */
public class Ui implements UiOutput {
    private static final String INDENT = "     ";
    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    }

    /**
     * Displays Herta's welcome screen.
     */
    public void showWelcome() {
        String banner = """
                 _   _           _
                | | | | ___ _ __| |_ __ _
                | |_| |/ _ \\ '__| __/ _` |
                |  _  |  __/ |  | || (_| |
                |_| |_|\\___|_|   \\__\\__,_|
                """;
        showSeparator();
        showMessages(banner, "Oh, you're here. I'm Herta.", "Well? What do you want?");
        showSeparator();
    }

    /**
     * Displays each supplied message using the standard message formatting.
     *
     * @param messages the messages to display.
     */
    private void showMessages(String... messages) {
        for (String message : messages) {
            showMessage(message);
        }
    }

    /**
     * Reads one command from standard input.
     *
     * @return the trimmed command, or {@code null} when standard input reaches EOF.
     */
    public String readCommand() {
        System.out.print("Your command? ");
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    /**
     * Displays the standard separator line.
     */
    public void showSeparator() {
        showMessage(SEPARATOR);
    }

    /**
     * Displays a message with Herta's standard indentation on every line.
     *
     * @param message the message to display.
     */
    @Override
    public void showMessage(String message) {
        String[] lines = message.split("\\R");
        for (String line : lines) {
            System.out.println(INDENT + line);
        }
    }

    /**
     * Displays Herta's goodbye message and closing separator.
     */
    @Override
    public void showGoodbye() {
        showMessages(GOODBYE_MESSAGE, SEPARATOR);
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }
}
