package steph;

import java.util.Scanner;

/**
 * Handles all interaction with the user: reading the commands typed on
 * standard input and printing Steph's responses to standard output.
 *
 * <p>Keeping every {@code System.in} / {@code System.out} access in this one
 * class means the rest of the program never touches the console directly, so
 * the wording and framing of the output -- or the input source itself -- can
 * change here without disturbing any command logic.
 */
public class Ui {

    /** Horizontal rule printed above and below every response block. */
    private static final String LINE = "____________________________________________________________";

    /** Name Steph introduces itself with. */
    private static final String NAME = "Steph";

    /** ASCII-art logo shown once at startup. */
    private static final String BANNER = " ____  _             _     \n"
            + "/ ___|| |_ ___ _ __ | |__  \n"
            + "\\___ \\| __/ _ \\ '_ \\| '_ \\ \n"
            + " ___) | ||  __/ |_) | | | |\n"
            + "|____/ \\__\\___| .__/|_| |_|\n"
            + "              |_|";

    private final Scanner scanner;

    /** Creates a Ui that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the logo and greeting shown when Steph starts. */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.printf("Hello! I'm %s.%nGlad to see you!%nWhat can I help you with?%n", NAME);
        System.out.println(LINE);
    }

    /** Prints the sign-off shown when the user types "bye" or the input ends. */
    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println("Goodbye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Returns whether there is another line of input to read. Becomes false
     * once input runs out (e.g. the end of a piped file), which ends the
     * command loop.
     *
     * @return True while a further command line is available.
     */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /**
     * Reads the next line of input, with leading and trailing whitespace
     * removed.
     *
     * @return The command line as typed, trimmed.
     */
    public String readCommand() {
        return this.scanner.nextLine().trim();
    }

    /**
     * Prints a response framed by a horizontal rule above and below. The
     * message may contain newlines; every line appears inside the same frame.
     *
     * @param message The text to show.
     */
    public void showMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    /**
     * Prints an error in the same frame as any other response, so a failed
     * command reads consistently with a successful one.
     *
     * @param message The error text to show.
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Reports that the save file could not be read, so Steph is starting from
     * an empty list.
     */
    public void showLoadingError() {
        showMessage("Sorry, I couldn't read the save file. Starting with an empty list.");
    }

    /** Releases the input resource. Call once, when the program is ending. */
    public void close() {
        this.scanner.close();
    }
}
