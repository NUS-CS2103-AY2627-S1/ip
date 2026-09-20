package duchess.ui;

import java.util.Scanner;

/** Handles all console input and output for Duchess. */
public class Ui {
    /** The separator printed between Duchess messages. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** The banner displayed when Duchess starts. */
    private static final String BANNER = """
            +------------------------+
            |        Duchess         |
            +------------------------+
            """;

    /** Reads commands entered through the console. */
    private final Scanner scanner;

    /** Creates a user interface that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Duchess's welcome message. */
    public void showWelcome() {
        showSeparator();
        System.out.print(BANNER);
        System.out.println("Hello! I'm Duchess.");
        System.out.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} when another input line is available
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next command line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the separator between messages. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a complete response produced by Duchess's command processor.
     *
     * @param message the response to display
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
}
