package leo;

import java.util.Scanner;

/**
 * Handles interactions between Leo and the user.
 */
public class Ui {
    private static final String LINE =
            "____________________________________________________________";

    // Created lazily (on the first readCommand() call) rather than in the
    // constructor, so that a Ui created only for its output side (e.g. by
    // GuiUi, which is never asked to read a command) does not open a
    // Scanner on System.in that it will never use or close. See A-Varargs
    // vs Level-10.
    private Scanner scanner;

    /**
     * Reads the next command entered by the user.
     *
     * @return the user's command
     */
    public String readCommand() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner.nextLine().trim();
    }

    /**
     * Displays Leo's banner and greeting.
     */
    public void showWelcome() {
        String banner = " _                \n"
                + "| |    ___  ___   \n"
                + "| |   / _ \\/ _ \\  \n"
                + "| |__|  __/ (_) | \n"
                + "|_____\\___|\\___/  \n";

        showMessage(
                banner,
                LINE,
                "Hello! I'm Leo. 🦁",
                "Think of me as your friendly task sidekick - how may I help today?",
                LINE
        );
    }

    /**
     * Displays one or more messages to the user, one per line, in the
     * order given. Accepting a variable number of arguments (varargs)
     * lets a single call replace what would otherwise be several
     * separate {@code showMessage} calls whenever a command needs to
     * show more than one line of related output (e.g. "task added" +
     * the task itself + the new task count) - see the call sites in
     * {@link Leo} for examples. A single message still works exactly
     * as before, since Java treats one argument as a one-element array.
     *
     * @param messages message(s) to display, each on its own line
     */
    public void showMessage(String... messages) {
        for (String message : messages) {
            print(message);
        }
    }

    /**
     * Displays an error message to the user. By default this is
     * exactly the same as {@link #showMessage}; {@link GuiUi} overrides
     * it to additionally remember that an error occurred, so the GUI
     * can show error replies differently from normal ones (see
     * {@link Reply}) - the console version has no such distinction to
     * make, so it just falls back to the plain {@link #showMessage}
     * behaviour.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Displays Leo's goodbye message.
     */
    public void showGoodbye() {
        // A-BetterGui: the exact "Hope to see you again soon..." line
        // stays unchanged (it's part of the app's existing behaviour,
        // not something worth risking on a rewrite) - the extra line
        // just adds a bit of warmth on top.
        showMessage(
                LINE,
                "Hope to see you again soon! Have a great day ahead.",
                "✨ Catch you on the next task!",
                LINE
        );
    }

    /**
     * Closes the input scanner, if one was ever opened.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Writes one line of output. This is the single place all of Ui's
     * other display methods funnel through, so that a subclass (like
     * {@link GuiUi}) can override just this one method to redirect
     * Leo's replies into a GUI instead of the console, without having
     * to duplicate any of the command-handling logic in {@link Leo}
     * that decides what those replies should say.
     *
     * @param line the line of text to write
     */
    protected void print(String line) {
        System.out.println(line);
    }
}