package nori.ui;

import java.util.Scanner;

/**
 * Handles Nori's console output.
 */
public class Ui {
    /** Leading spaces that align a response under the banner. */
    private static final String INDENT = "    ";
    /** The unindented rule drawn above and below a response. */
    private static final String DIVIDER_LINE = "____________________________________________________________";
    /** The indented form of {@link #DIVIDER_LINE}. */
    private static final String DIVIDER = INDENT + DIVIDER_LINE;
    /** The ASCII-art wordmark shown once at startup. */
    private static final String BANNER = "  _   _  ____  _____  _____ \n"
            + " | \\ | |/ __ \\|  __ \\|_   _|\n"
            + " |  \\| | |  | | |__) | | |  \n"
            + " | . ` | |  | |  _  /  | |  \n"
            + " | |\\  | |__| | | \\ \\ _| |_ \n"
            + " |_| \\_|\\____/|_|  \\_\\_____|\n\n\n";
    /** The command summary shown by the help command. */
    private static final String[] HELP_LINES = {
        "Noot noot! Here is what my flippers understand:",
        "todo <description>",
        "deadline <description> /by yyyy-MM-dd",
        "event <description> /from <start> /to <end>",
        "on yyyy-MM-dd",
        "schedule",
        "schedule yyyy-MM-dd",
        "list",
        "list /from yyyy-MM-dd /to yyyy-MM-dd",
        "find <keyword>",
        "mark <task number>",
        "unmark <task number>",
        "delete <task number>",
        "help",
        "bye",
        "Penguin tip: use yyyy-MM-dd in an event's /from or /to so \"on\" can find it.",
        "Penguin tip: add a time like 0900, 9:30 or 2pm to an event's /from to place it on the schedule."
    };
    /** Reads command lines from standard input. */
    private final Scanner scanner;

    /**
     * Creates Nori's console user interface.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads and trims one command from standard input.
     *
     * @return the command, or {@code null} if standard input has ended.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    /**
     * Closes the console input stream.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Displays Nori's banner and greeting.
     */
    public void showWelcome() {
        System.out.print(BANNER);
        printResponse(false, "Noot noot! I'm Nori, your tiny task penguin.",
                "Waddle in a command and I'll get flapping.");
    }

    /**
     * Displays a response reporting something Nori could not do.
     *
     * A console reader has the whole session in front of them and reads it in
     * order, so an error needs no distinct format here and is printed like any
     * other response. A graphical interface, where responses scroll away and
     * are taken in at a glance, overrides this to mark an error out.
     *
     * @param lines the error lines to display.
     */
    public void showError(String... lines) {
        showResponse(lines);
    }

    /**
     * Displays an error that occurred while loading saved tasks.
     *
     * @param message the loading error to display.
     */
    public void showLoadingError(String message) {
        showError(message);
    }

    /**
     * Displays a notice produced while loading saved tasks.
     *
     * @param message the loading notice to display.
     */
    public void showLoadingNotice(String message) {
        showResponse(message);
    }

    /**
     * Displays Nori's help text.
     */
    public void showHelp() {
        showResponse(HELP_LINES);
    }

    /**
     * Displays one or more response lines between indented dividers.
     *
     * @param lines the response lines to display.
     */
    public void showResponse(String... lines) {
        printResponse(true, lines);
    }

    /**
     * Prints one or more lines between dividers, followed by a blank line.
     *
     * @param shouldIndent whether the response should align under the banner.
     * @param lines the lines to print.
     */
    private void printResponse(boolean shouldIndent, String... lines) {
        assert lines.length > 0 : "A response says something; empty dividers would tell a user nothing.";

        String divider = shouldIndent ? DIVIDER : DIVIDER_LINE;
        String prefix = shouldIndent ? INDENT + " " : "";
        System.out.println(divider);
        for (String line : lines) {
            System.out.println(prefix + line);
        }
        System.out.println(divider);
        System.out.println();
    }
}
