package seedu.mattchatbot;

import java.util.Scanner;

/**
 * Handles everything the user sees and types.
 * <p>
 * Extracted so that the wording and layout of the chatbot's replies live in
 * one place: the rest of the code decides what happened, and asks Ui to say
 * it. Changing how replies look, or moving them to a different interface
 * later, then touches only this class.
 * <p>
 * A Ui works in one of two modes. The console mode prints replies to standard
 * output and reads commands from standard input. The capturing mode, built by
 * {@link #forGui()}, keeps replies in memory for a caller to collect with
 * {@link #takeShownText()} instead. The GUI needs the second mode because a
 * window has to be handed the reply as a string to put in a chat bubble,
 * whereas the console can simply print it.
 */
public class Ui {

    /** Horizontal rule printed around each block of the bot's output. */
    private static final String DIVIDER =
            "____________________________________________________________";

    /** ASCII-art banner shown once when the program starts. */
    private static final String BANNER = """
            W   W  IIIII  N   N   SSS   TTTTT   OOO   N   N
            W   W    I    NN  N  S        T    O   O  NN  N
            W W W    I    N N N   SSS     T    O   O  N N N
            WW WW    I    N  NN      S    T    O   O  N  NN
            W   W  IIIII  N   N   SSS     T     OOO   N   N
            """;

    /** Where the user's input is read from, or null when capturing for a GUI. */
    private final Scanner scanner;

    /** Replies shown since the last collection, or null when printing to the console. */
    private final StringBuilder captured;

    /** Creates a Ui that talks to the user through the console. */
    public Ui() {
        this(new Scanner(System.in), null);
    }

    /**
     * Creates a Ui in one of its two modes.
     *
     * @param scanner  where commands are read from, or null when capturing
     * @param captured where replies are collected, or null when printing
     */
    private Ui(Scanner scanner, StringBuilder captured) {
        this.scanner = scanner;
        this.captured = captured;
    }

    /**
     * Returns a Ui that collects replies instead of printing them, for a caller
     * that displays them itself.
     * <p>
     * It has no scanner: a GUI delivers input by calling the chatbot directly,
     * so nothing here ever reads standard input.
     *
     * @return a Ui in capturing mode
     */
    public static Ui forGui() {
        return new Ui(null, new StringBuilder());
    }

    /**
     * Returns whether this Ui collects its replies rather than printing them.
     *
     * @return true if replies are being captured for a GUI
     */
    private boolean isCapturing() {
        return captured != null;
    }

    /**
     * Returns everything shown since this method was last called, and forgets
     * it, so that each reply a GUI collects covers exactly one command.
     *
     * @return the captured text, with no trailing newline
     * @throws IllegalStateException if this Ui prints to the console instead
     */
    public String takeShownText() {
        if (captured == null) {
            throw new IllegalStateException("This Ui prints its replies rather than capturing them.");
        }
        String text = captured.toString().strip();
        captured.setLength(0);
        return text;
    }

    /**
     * Returns whether there is another line of input to read.
     * <p>
     * False once the input stream ends, e.g. the user presses Ctrl-D or piped
     * input runs out, which lets the chatbot exit cleanly rather than crash.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        // Only the console mode has a scanner, so reaching here in capturing
        // mode would mean a GUI had started reading standard input behind the
        // user's back rather than feeding input in through getResponse.
        assert scanner != null : "hasNextCommand is for console mode; a GUI never reads stdin";
        return scanner.hasNextLine();
    }

    /**
     * Reads one line of input, with surrounding spaces removed.
     *
     * @return what the user typed
     */
    public String readCommand() {
        assert scanner != null : "readCommand is for console mode; a GUI never reads stdin";
        return scanner.nextLine().trim();
    }

    /**
     * Prints the banner and the welcome message, once per session.
     */
    public void showWelcome() {
        // The banner is ASCII art, so it only lines up in the console's
        // fixed-width font and is left out of the GUI's greeting.
        if (isCapturing()) {
            show("Good day. Winston, at your service.", "How may I be of assistance?");
        } else {
            show(BANNER, "Good day. Winston, at your service.", "How may I be of assistance?");
        }
    }

    /**
     * Prints the farewell message, the last thing a session prints.
     */
    public void showGoodbye() {
        show("Very good. I shall be here when you return.");
    }

    /**
     * Prints an error back to the user.
     *
     * @param message what went wrong, written for the user
     */
    public void showError(String message) {
        show(message);
    }

    /**
     * Shows the given lines as one block: printed between horizontal dividers
     * in console mode, so every reply looks the same, or appended to the
     * captured text in capturing mode, where the GUI draws its own boundary
     * around each reply and dividers would only be noise.
     *
     * @param lines the lines making up one reply
     */
    public void show(String... lines) {
        // A call with no lines would print a pair of dividers with nothing
        // between them, which is never something the chatbot means to say.
        assert lines.length > 0 : "every reply has at least one line";
        if (captured != null) {
            for (String line : lines) {
                captured.append(line).append(System.lineSeparator());
            }
            return;
        }
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
    }
}
