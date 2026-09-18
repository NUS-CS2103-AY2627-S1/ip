package chud.ui;

import java.util.Scanner;

/**
 * Handles all interaction with the user: printing messages to the console in Chud's standard
 * format, and reading typed commands from standard input.
 */
public class Ui {
    private static final String HORIZONTAL_LINE = "    ____________________________________________________________";
    private static final String BANNER = "  ____ _               _ \n"
            + " / ___| |__  _   _  __| |\n"
            + "| |   | '_ \\| | | |/ _` |\n"
            + "| |___| | | | |_| | (_| |\n"
            + " \\____|_| |_|\\__,_|\\__,_|\n";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the startup banner and greeting, framed by horizontal lines. */
    public void showWelcome() {
        System.out.println(HORIZONTAL_LINE);
        System.out.print(BANNER);
        showMessage("yo it's Chud 🗿", "what we lockin in today?");
        System.out.println(HORIZONTAL_LINE);
    }

    /** Prints the horizontal-line separator that frames every response. */
    public void showLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Prints one or more lines at the standard message indent, e.g. "     Here are ...", each
     * on its own line. A single argument prints one line; several print each in turn, so a
     * command with more than one fixed line to say (e.g. a greeting) can say it in one call
     * instead of one call per line.
     */
    public void showMessage(Object... contents) {
        for (Object content : contents) {
            System.out.println("     " + content);
        }
    }

    /** Prints one line of output at the deeper indent used to display a task under a message. */
    public void showIndentedMessage(Object content) {
        System.out.println("       " + content);
    }

    /**
     * Prints an error message in Chud's standard "bruh" format. The rest of the message stays
     * plain and instructional (not slang) so the actual problem/fix is still easy to read.
     */
    public void showError(String message) {
        System.out.println("     bruh 💀 " + message);
    }

    /**
     * Reads the next line of typed input, trimmed of surrounding whitespace so that stray spaces
     * (e.g. "bye " or "  list") don't stop commands from being recognized. If input has run out
     * (e.g. piped input with no trailing "bye", or the user pressing Ctrl+D) this returns "bye"
     * so the program exits gracefully instead of crashing with a NoSuchElementException.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "bye";
    }
}
