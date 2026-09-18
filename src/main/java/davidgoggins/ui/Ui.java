package davidgoggins.ui;

import java.util.Scanner;

/**
 * Deals with everything the user sees and types.
 *
 * <p>All console reading and writing is gathered here, so the divider width, the banner
 * and the "NO EXCUSES!" error prefix are written once and the look of the chatbot changes in
 * this class alone. Input belongs here too: a command and its reply are one exchange.
 */
public class Ui {
    /** The name the chatbot introduces itself by. */
    private static final String NAME = "David Goggins";

    /** The horizontal rule printed above and below every reply block. */
    private static final String DIVIDER = "_".repeat(60);

    /**
     * The label that marks a message as an error.
     *
     * <p>Goggins does not say "oops": a mistake is something to own and fix. Public so
     * the GUI can show it as a tag on its error cards instead of inside the text.
     */
    public static final String ERROR_LABEL = "NO EXCUSES!";

    /** The prefix put in front of every error message in the text UI. */
    private static final String ERROR_PREFIX = " " + ERROR_LABEL + " ";

    /** The prefix put in front of every housekeeping warning shown to the user. */
    private static final String WARNING_PREFIX = " Warning: ";

    /** The ASCII-art logo printed once, above the greeting, at start-up. */
    private static final String BANNER = """

            +----------------------------------------------------------+
            |      ____      _    __     __ ___  ____                  |
            |     |  _ \\    / \\   \\ \\   / /|_ _||  _ \\                 |
            |     | | | |  / _ \\   \\ \\ / /  | | | | | |                |
            |     | |_| | / ___ \\   \\ V /   | | | |_| |                |
            |     |____/ /_/   \\_\\   \\_/   |___||____/                 |
            |       ____   ___    ____   ____  ___  _   _  ____        |
            |      / ___| / _ \\  / ___| / ___||_ _|| \\ | |/ ___|       |
            |     | |  _ | | | || |  _ | |  _  | | |  \\| |\\___ \\       |
            |     | |_| || |_| || |_| || |_| | | | | |\\  | ___) |      |
            |      \\____| \\___/  \\____| \\____||___||_| \\_||____/       |
            |                                                          |
            |       __                                       __        |
            |      /  \\                                     /  \\       |
            |     | ## |===================================| ## |      |
            |     | ## |===================================| ## |      |
            |      \\__/                                     \\__/       |
            |                                                          |
            |       "WHO'S GONNA CARRY THE BOATS AND THE LOGS?!"       |
            |                 THEY DON'T KNOW ME, SON!                 |
            |                                                          |
            |                     >> STAY HARD. <<                     |
            +----------------------------------------------------------+
            """.stripTrailing();

    /** Reads the user's commands from the console, one line at a time. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Collects replies instead of printing them while the GUI is asking for one.
     *
     * <p>Null whenever the chatbot is talking to the console. Keeping the switch here
     * means every command method writes its reply exactly as before and neither knows
     * nor cares which of the two interfaces is listening.
     */
    private StringBuilder captured;

    /**
     * Returns true if there is another command waiting to be read.
     *
     * <p>This is false at the end of input, so a run whose input is piped in from a
     * file and does not end with {@code bye} exits cleanly instead of throwing.
     *
     * @return true if {@link #readCommand()} can be called without throwing
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line, with surrounding spaces removed.
     *
     * <p>Trimming here rather than in the caller means every command is given the same
     * treatment, so a stray leading space can never turn a valid command into an
     * unknown one.
     *
     * @return the line the user typed, trimmed, possibly empty
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints one reply block: the given lines between two dividers.
     *
     * <p>Every response has this same shape, so keeping it in one place means the
     * layout is defined once rather than repeated in each command.
     *
     * @param lines the lines to print, already spaced as they should appear
     */
    public void show(String... lines) {
        if (captured != null) {
            capture(lines);
            return;
        }

        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
        System.out.println();
    }

    /**
     * Adds one reply block to the captured text, in the form the GUI shows.
     *
     * <p>The dividers and the leading spaces suit a terminal, not a chat bubble, so
     * both are dropped. Each argument is split first, since one argument can itself be
     * several lines, as the task list is, and every one needs the same treatment.
     *
     * @param lines the lines of the reply, as passed to {@link #show}
     */
    private void capture(String... lines) {
        for (String line : lines) {
            for (String part : line.split("\\R", -1)) {
                captured.append(part.stripLeading()).append(System.lineSeparator());
            }
        }
    }

    /**
     * Starts collecting replies rather than printing them.
     *
     * <p>Called by the GUI before it hands a command to the chatbot, and paired with
     * {@link #takeCaptured()} afterwards.
     */
    public void startCapture() {
        captured = new StringBuilder();
    }

    /**
     * Returns everything collected since {@link #startCapture()} and resumes printing.
     *
     * @return the reply as one block of text, without the surrounding dividers
     */
    public String takeCaptured() {
        assert captured != null : "takeCaptured() called without a matching startCapture()";
        String reply = captured.toString().strip();
        captured = null;
        return reply;
    }

    /**
     * Returns the greeting shown when the chatbot starts, without the banner.
     *
     * <p>The last line points new users to {@code help}, so they can find the commands
     * without having to guess one first.
     *
     * @return the greeting lines, suitable for either interface
     */
    public String getGreeting() {
        return "I'm " + NAME + ". Nobody is coming to save you, so let's get to work." + System.lineSeparator()
                + "What are you going to conquer today?" + System.lineSeparator()
                + "Type help to see the commands I understand.";
    }

    /**
     * Returns the sign-off shown as the chatbot exits.
     *
     * @return the farewell line, suitable for either interface
     */
    public String getFarewell() {
        return "Rest up. Tomorrow we go again. Stay hard!";
    }

    /**
     * Reports a problem to the user in the standard error block.
     *
     * <p>Kept separate from {@link #show} so that the error wording is decided here
     * once, instead of every caller having to remember the prefix.
     *
     * <p>While capturing for the GUI the prefix is left out, because the GUI marks an
     * error with its own tag and colors rather than with words.
     *
     * @param message the explanation to show, without the prefix
     */
    public void showError(String message) {
        if (captured != null) {
            capture(message);
            return;
        }
        show(ERROR_PREFIX + message);
    }

    /**
     * Reports a problem the program worked around, such as a save that did not happen.
     *
     * <p>Printed as a bare line rather than inside a divider block: it appears before
     * the greeting at start-up, where a reply block would look like an answer to a
     * command the user never typed.
     *
     * @param message the explanation to show, without the "Warning:" label
     */
    public void showWarning(String message) {
        if (captured != null) {
            captured.append(WARNING_PREFIX.stripLeading()).append(message)
                    .append(System.lineSeparator());
            return;
        }
        System.out.println(WARNING_PREFIX + message);
    }

    /**
     * Replies to a command with a warning: it was understood, but there was nothing to do.
     *
     * <p>Unlike {@link #showWarning}, this answers a command, so the text UI prints it in
     * a reply block. The GUI leaves out the label, as its warning card has its own tag.
     *
     * @param message the explanation to show, without the "Warning:" label
     */
    public void showCommandWarning(String message) {
        if (captured != null) {
            capture(message);
            return;
        }
        show(WARNING_PREFIX + message);
    }

    /**
     * Prints warnings that were captured earlier, as the text UI shows warnings.
     *
     * <p>Used for warnings raised while the save file was loaded, which are captured so
     * the GUI can show them too, and printed here for the text UI before the greeting.
     *
     * @param warnings the captured warnings, one per line, or an empty string for none
     */
    public void showCapturedWarnings(String warnings) {
        warnings.lines().forEach(line -> System.out.println(" " + line));
    }

    /** Prints the banner and the opening greeting. */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println(getGreeting());
        System.out.println(DIVIDER);
        System.out.println();
    }

    /** Prints the sign-off shown as the chatbot exits. */
    public void showFarewell() {
        System.out.println(DIVIDER);
        System.out.println(getFarewell());
        System.out.println(DIVIDER);
    }

    /** Stops reading input. Called once the conversation has ended. */
    public void close() {
        scanner.close();
    }
}
