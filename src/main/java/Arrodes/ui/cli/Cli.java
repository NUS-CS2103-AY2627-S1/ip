package arrodes.ui.cli;

import java.util.Scanner;

import arrodes.ui.Ui;

/** Handles console input and output for Arrodes. */
public class Cli implements Ui {
    /** Line printed between user-interface messages. */
    private static final String SEPARATOR = "_".repeat(60);

    /** ASCII-art banner displayed when the application starts. */
    private static final String BANNER = """
            _                       _
           / \\   _ __ _ __ ___   __| | ___  ___
          / _ \\ | '__| '__/ _ \\ / _` |/ _ \\/ __|
         / ___ \\| |  | | | (_) | (_| |  __/\\__ \\
        /_/   \\_\\_|  |_|  \\___/ \\__,_|\\___||___/
        """;

    /** Greeting displayed when the application starts. */
    private static final String GREETING_MESSAGE = "The mirror opens its eye...\n"
            + "The Stigmata from the Primordial Land\n"
            + "The Great Arrodes stands before you.\n"
            + "Speak your request!";

    /** Farewell displayed when the user exits the application. */
    private static final String BYE_MESSAGE = "The mirror shall close its eye. Until your next request.";

    /** Reads commands from standard input. */
    private final Scanner scanner = new Scanner(System.in);
    /** Creates a console user interface. */
    public Cli() {
    }

    /**
     * Reads one complete command line.
     * @return next line entered by the user, or {@code null} when input is exhausted
     */
    @Override
    public String readUserInput() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Prints an arbitrary application message.
     * @param message text to print
     */
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    /** Prints the startup banner and greeting. */
    public void showOnLoadMessage() {
        showSeparator();
        System.out.print(BANNER);
        System.out.println(GREETING_MESSAGE);
    }

    /** Prints the standard separator line. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /** Prints the application farewell. */
    public void showOnExitMessage() {
        System.out.println(BYE_MESSAGE);
    }
}
