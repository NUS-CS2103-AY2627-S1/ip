package will;

import java.util.Scanner;

/**
 * Deals with all interactions with the user: the startup banner and
 * greeting, the goodbye message, reading commands, and the framed
 * message/line formatting used throughout the program. Extracted out
 * of {@link Will} so that anything about "how we talk to the user"
 * lives in one place.
 */
public class Ui {
    /**
     * Leading indent placed before every message printed inside the
     * response frame. Named (rather than a literal repeated at each
     * call site) so that {@link Will#stripCliFraming(String)} can
     * recognise exactly the lines this class indents, instead of
     * hardcoding a second copy of the same spacing.
     */
    public static final String MESSAGE_INDENT = "     ";

    private static final String LOGO = " __        _____ _     _     \n"
            + " \\ \\      / /_ _| |   | |    \n"
            + "  \\ \\ /\\ / / | || |   | |    \n"
            + "   \\ V  V /  | || |___| |___ \n"
            + "    \\_/\\_/  |___|_____|_____|\n";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the startup banner followed by the greeting. */
    public void showWelcome() {
        showLine();
        System.out.println(LOGO);
        showMessage("What's up!!! I'm Will.");
        showMessage("How may I assist you?");
    }

    /** Prints the farewell message shown when the user types "bye". */
    public void showGoodbye() {
        showMessage("Seee yaaaa! Meet again soon!");
    }

    /** Prints the horizontal divider line used to frame each response. */
    public void showLine() {
        System.out.println("    ____________________________________________________________");
    }

    /** Prints a single indented message line inside the response frame. */
    public void showMessage(String message) {
        System.out.println(MESSAGE_INDENT + message);
    }

    /** Prints an error message in the same "OOPS!!!" style used throughout. */
    public void showError(String message) {
        showMessage("OOPS!!! " + message);
    }

    /**
     * Reads one line of command input, silently retrying on a blank
     * (or whitespace-only) line instead of returning it as a command —
     * a blank line isn't a typo worth an "OOPS!!!", it's just waiting
     * for the next real line. Callers never need to special-case it.
     */
    public String readCommand() {
        while (true) {
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
        }
    }
}
