package lebron.ui;

import java.util.Scanner;

/**
 * Handles all interaction with the user: reading command lines from standard
 * input and printing formatted responses to standard output.
 *
 * <p>Keeping every {@code System.in}/{@code System.out} touch-point in one
 * class means the rest of the program never prints directly, so the look of
 * the interface (dividers, greeting, wording) can be changed here alone.
 */
public class Ui {
    /** Horizontal divider printed around each block of output. */
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _     _____ ____  ____   ___  _   _ \n"
            + "| |   | ____| __ )|  _ \\ / _ \\| \\ | |\n"
            + "| |   |  _| |  _ \\| |_) | | | |  \\| |\n"
            + "| |___| |___| |_) |  _ <| |_| | |\\  |\n"
            + "|_____|_____|____/|_| \\_\\\\___/|_| \\_|\n";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the startup banner and greeting, wrapped in divider lines. */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.print(BANNER);
        System.out.println("Hey, I'm Lebron, your coach for today.");
        System.out.println("What's the game plan?");
        System.out.println(LINE);
    }

    /** Reads the next line the user types. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints a single divider line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Prints one line of response text. */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /** Releases the input resource; call once when the program is done. */
    public void close() {
        scanner.close();
    }
}
