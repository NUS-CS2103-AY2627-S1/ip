package lloyd.ui;

import java.util.Scanner;

/**
 * Handles console input and output for the Lloyd chatbot.
 *
 * <p>Keeping console details in this class allows the main application to
 * focus on coordinating commands and tasks.</p>
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reports whether another command is available to read.
     *
     * @return {@code true} if standard input contains another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and removes surrounding whitespace.
     *
     * @return The next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays a chatbot response enclosed by divider lines.
     *
     * @param message Response to display.
     */
    public static void showResponse(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
        System.out.println();
    }

    /** Releases the console input reader when the chatbot stops. */
    public void close() {
        scanner.close();
    }
}
