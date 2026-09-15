package seedu.goat.ui;

import java.util.Scanner;

/**
 * Presents the text interface: reads typed commands and prints replies.
 * <p>
 * This class decides only how a reply looks, never what it says. The wording is chosen by
 * the chatbot and handed here as text, which is what lets the same replies be shown in a
 * window instead without any of this formatting following them there.
 */
public class Ui {

    /** Horizontal rule printed around each block of output. */
    private static final String LINE =
            "____________________________________________________________";

    /** ASCII-art banner shown once at startup. */
    private static final String BANNER = """
              ____   ___      _     _____
             / ___| / _ \\    / \\   |_   _|
            | |  _ | | | |  / _ \\    | |
            | |_| || |_| | / ___ \\   | |
             \\____| \\___/ /_/   \\_\\  |_|
            """;

    /** Reads the user's commands from standard input. */
    private final Scanner scanner;

    /** Creates a user interface reading from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the banner shown once when the program starts. */
    public void showBanner() {
        System.out.println(BANNER);
    }

    /**
     * Returns whether there is another command waiting to be read.
     * <p>
     * False at end of input, so piped input and Ctrl-D end the conversation cleanly
     * rather than looping forever.
     *
     * @return true if another line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command, with surrounding whitespace removed.
     *
     * @return the line the user typed, trimmed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints one reply, wrapped in horizontal rules and indented.
     * <p>
     * Each message is split on its newlines so that a reply built elsewhere as a single
     * block of text is indented line by line, rather than only on its first line.
     *
     * @param messages parts of the reply, printed in order.
     */
    public void show(String... messages) {
        System.out.println(LINE);
        for (String message : messages) {
            for (String line : message.split("\n", -1)) {
                System.out.println(" " + line);
            }
        }
        System.out.println(LINE);
    }
}
