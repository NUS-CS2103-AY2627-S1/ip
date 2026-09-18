package kenbot;

import java.util.Scanner;

/**
 * Deals with everything the user sees and types.
 *
 * <p>This class makes no decisions. It reads a line of text and prints blocks
 * of text; understanding what the text means, and acting on it, is someone
 * else's job. Keeping it separate means the wording and layout of every message
 * can be changed in one place, and a graphical window could later replace this
 * class without touching the rest of the program.</p>
 */
public class Ui {

    /** Separator printed above and below every block of output. */
    private static final String LINE =
            "____________________________________________________________";

    /** ASCII art shown once when the program starts. */
    private static final String BANNER = " _  __          _           _        \n"
            + "| |/ /___ _ __ | |__   ___ | |_      \n"
            + "| ' // _ \\ '_ \\| '_ \\ / _ \\| __|     \n"
            + "| . \\  __/ | | | |_) | (_) | |_      \n"
            + "|_|\\_\\___|_| |_|_.__/ \\___/ \\__|     \n"
            + "\n"
            + "                 Kenbot";

    /**
     * The welcome message, without the banner.
     *
     * <p>Kept apart from {@link #BANNER} because the ASCII art only reads
     * correctly in a fixed-width console; a window shows these words alone.</p>
     */
    private static final String GREETING = "Yo, I'm Kenbot.\nWhat are we getting done today?";

    private final Scanner scanner = new Scanner(System.in);

    /** Creates a user interface that reads from and writes to the console. */
    public Ui() {
    }

    /** Prints the banner and welcome message shown when the program starts. */
    public void showGreeting() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println(GREETING);
        System.out.println(LINE + "\n");
    }

    /**
     * Returns the welcome message on its own, for a caller that does its own
     * printing.
     *
     * @return the words Kenbot opens with
     */
    public String getGreeting() {
        return GREETING;
    }

    /**
     * Returns whether there is another line of input waiting.
     *
     * <p>Checked before reading, so that input running out ends the program
     * quietly instead of throwing.</p>
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next line the user typed.
     *
     * @return the line, with surrounding spaces removed
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints one block of output, wrapped in separator lines.
     *
     * @param body the text to show, which may span several lines
     */
    public void show(String body) {
        System.out.println(LINE);
        System.out.println(body);
        System.out.println(LINE);
    }

    /**
     * Prints a problem for the user to read.
     *
     * <p>This looks the same as {@link #show} today. It is a separate method
     * because showing a result and reporting a problem are different
     * intentions, so one can later be made to look different from the other
     * without hunting through every place that prints.</p>
     *
     * @param message the problem to describe
     */
    public void showError(String message) {
        show(message);
    }

    /** Stops reading input. */
    public void close() {
        scanner.close();
    }
}
