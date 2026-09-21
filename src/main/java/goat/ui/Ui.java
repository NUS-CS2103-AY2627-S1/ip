package goat.ui;

import java.util.Scanner;

/**
 * Handles everything Goat says to the user and everything the user types back.
 *
 * Collecting the input and output here means the rest of the program never
 * touches {@code System.out} or {@code System.in} directly. That keeps the
 * layout of a reply (the dividers, the indent, the "OOPS!!! " prefix) in one
 * place, and it is what lets the same command code serve two front ends.
 *
 * Every reply is collected into a buffer as well as being shown, and
 * {@link #takeResponse()} hands that buffer over as plain text. The terminal
 * version prints as it goes and ignores the buffer; the GUI prints nothing and
 * reads the buffer instead, putting the text into a speech bubble. Neither
 * {@link goat.command.Command} nor {@link goat.task.TaskList} knows which is in
 * use.
 *
 * The class is instance-based because it owns a {@link Scanner} that has to be
 * created and closed, and an object is the natural home for something with a
 * lifetime like that.
 */
public class Ui {
    /** Horizontal line used to separate the chatbot's replies in the terminal. */
    private static final String DIVIDER = "    " + "_".repeat(60);

    /** Left padding for reply text, so it sits just inside the divider. */
    private static final String INDENT = "     ";

    /** Prefix put in front of every error message shown to the user. */
    private static final String ERROR_PREFIX = "Hm. ";

    /** The logo shown once when the terminal version starts. */
    private static final String BANNER = "  ____   ___      _     _____ \n"
            + " / ___| / _ \\    / \\   |_   _|\n"
            + "| |  _ | | | |  / _ \\    | |  \n"
            + "| |_| || |_| | / ___ \\   | |  \n"
            + " \\____| \\___/ /_/   \\_\\  |_|  \n";

    /** Whether replies are printed to the terminal as well as buffered. */
    private final boolean isConsole;

    /** Reads the user's input from the terminal; null when a GUI is in charge. */
    private final Scanner scanner;

    /** What has been shown since the last {@link #takeResponse()}. */
    private final StringBuilder pendingResponse = new StringBuilder();

    /**
     * Creates a Ui that prints to the terminal and reads from it.
     */
    public Ui() {
        this(true);
    }

    private Ui(boolean isConsole) {
        this.isConsole = isConsole;
        // A GUI never reads standard input, and opening a Scanner on it would
        // claim a stream nothing is going to feed.
        this.scanner = isConsole ? new Scanner(System.in) : null;
    }

    /**
     * Creates a Ui for a graphical front end.
     *
     * It prints nothing and reads nothing. Replies are collected and handed
     * over by {@link #takeResponse()} for the caller to display however it
     * likes.
     *
     * @return a Ui that only buffers
     */
    public static Ui forGui() {
        return new Ui(false);
    }

    /**
     * Shows a reply.
     *
     * In the terminal the lines are wrapped between two horizontal rules, so
     * every message looks the same. Any number of lines is accepted, since a
     * reply such as the task listing spans several inside one pair of rules.
     *
     * @param lines the text to show to the user, one element per line
     */
    public void show(String... lines) {
        for (String line : lines) {
            pendingResponse.append(line).append(System.lineSeparator());
        }
        if (!isConsole) {
            return;
        }
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(INDENT + line);
        }
        System.out.println(DIVIDER);
        System.out.println();
    }

    /**
     * Returns everything shown since the previous call, and forgets it.
     *
     * The text is the reply's lines only: no dividers and no indent, since a
     * GUI draws its own frame around them.
     *
     * @return the replies collected so far, with trailing blank space removed
     */
    public String takeResponse() {
        String response = pendingResponse.toString().strip();
        pendingResponse.setLength(0);
        // The next reply must not carry this one along with it.
        assert pendingResponse.length() == 0 : "the response buffer was not cleared";
        return response;
    }

    /**
     * Shows the greeting, once, at start-up.
     *
     * The logo goes only to the terminal. It is drawn out of underscores and
     * backslashes, which line up in a monospaced terminal and do not in a
     * proportional label.
     */
    public void showWelcome() {
        if (isConsole) {
            System.out.println(BANNER);
        }
        show("Goat here.", "I keep the list. You do the climbing.");
    }

    /** Shows the parting message, once, just before the program ends. */
    public void showGoodbye() {
        show("Off up the hill. The list keeps.");
    }

    /**
     * Shows something that went wrong, in the same frame as any other reply.
     *
     * @param message the explanation to show, without the "OOPS!!! " prefix
     */
    public void showError(String message) {
        show(ERROR_PREFIX + message);
    }

    /**
     * Warns that the save file could not be read.
     *
     * This is its own method because the reassurance that follows the error is
     * fixed wording, not something the caller should have to supply.
     *
     * @param message why the file could not be read
     */
    public void showLoadingError(String message) {
        show(ERROR_PREFIX + message,
                "Starting with an empty list. Fix the file now if you want what was in it.");
    }

    /**
     * Reports whether there is another line of input waiting.
     *
     * Input can run out without a "bye", for example when commands are piped in
     * from a file, so the caller has to be able to check before reading.
     *
     * @return true if {@link #readCommand()} has a line to return
     */
    public boolean hasNextCommand() {
        return scanner != null && scanner.hasNextLine();
    }

    /**
     * Reads one line typed by the user.
     *
     * @return the line, with surrounding spaces removed
     */
    public String readCommand() {
        // hasNextCommand() is false for a GUI, so a caller that checks first
        // can never arrive here without a Scanner.
        assert scanner != null : "readCommand called on a Ui built for a GUI";
        return scanner.nextLine().trim();
    }

    /** Releases the terminal input once the program is finished with it. */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
