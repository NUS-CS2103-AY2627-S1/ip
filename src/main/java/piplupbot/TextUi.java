package piplupbot;

import java.util.Scanner;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents the bot's console face: it prints each reply between dividers, and
 * reads what the user types from the keyboard.
 *
 * <p>Everything here used to be {@link Ui} itself. It moved into this class
 * unchanged when the bot gained a window and {@code Ui} became the interface
 * both faces implement, so the console looks exactly as it did -- banner,
 * dividers, indentation, blank line after each reply -- and the text-UI tests
 * expect exactly what they expected before.</p>
 *
 * <p>Only this face reads input, because only the console has a stream of lines
 * to read from. The window works the other way round: it waits for the user to
 * press Enter, then hands the bot that one line.</p>
 *
 * <p>Unlike {@link Parser}, this is a class of instance methods rather than
 * static ones, because it owns a {@link Scanner} over standard input: that
 * scanner is state that has to live as long as the conversation, and one object
 * holding it is simpler than a static field that every method has to assume was
 * set up first.</p>
 */
public class TextUi implements Ui {
    /** Horizontal line that separates the bot's replies from the user's input. */
    private static final String DIVIDER =
            "    ____________________________________________________________";

    /** The name the bot draws for itself before it says anything. */
    private static final String BANNER =
            " ____  _       _             ____        _   \n"
            + "|  _ \\(_)_ __ | |_   _ _ __ | __ )  ___ | |_ \n"
            + "| |_) | | '_ \\| | | | | '_ \\|  _ \\ / _ \\| __|\n"
            + "|  __/| | |_) | | |_| | |_) | |_) | (_) | |_ \n"
            + "|_|   |_| .__/|_|\\__,_| .__/|____/ \\___/ \\__|\n"
            + "        |_|           |_|                    \n";

    /**
     * Reads the user's lines. It is created once and kept, because a
     * {@code Scanner} buffers what it has read ahead; making a new one per line
     * could drop input that the previous one had already taken from the stream.
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints one or more lines wrapped between horizontal lines.
     * Each line is indented so the bot's replies stand out from what the user typed.
     *
     * @param lines The lines of text to display.
     */
    @Override
    public void show(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println("     " + line);
        }
        System.out.println(DIVIDER);
        System.out.println();
    }

    /**
     * Draws the banner, then greets the user.
     * The banner is drawn with text characters, so only the console draws it;
     * a window already has a title bar to say whose it is.
     */
    @Override
    public void showWelcome() {
        System.out.println(BANNER);
        // Ui.super rather than plain super: the greeting is a default method of
        // the Ui interface, while plain super would mean the parent class, Object.
        Ui.super.showWelcome();
    }

    /**
     * Reports whether there is another line of input to read.
     * This is {@code false} once the input runs out, e.g. on Ctrl-D or at the
     * end of a piped file, which lets the main loop stop rather than block.
     *
     * @return {@code true} if {@link #readCommand()} has a line to return.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next line the user typed, with the spaces around it removed.
     *
     * @return The trimmed line, which may be empty if the user pressed Enter alone.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }
}
