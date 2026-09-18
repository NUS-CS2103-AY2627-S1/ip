package hermes.ui;

import java.util.Scanner;

/**
 * Handles everything Hermes shows to the user and reads back from them.
 *
 * <p>Keeping the console in one class means the rest of Hermes works in plain
 * strings and never calls {@code System.out} directly, so how a reply is framed
 * can change here without touching the code that decides what to say.
 */
public class Ui {

    /**
     * The greeting that opens every conversation.
     *
     * <p>The console and the window both show it, so it is kept here once rather
     * than written out in each, where the two copies could drift apart.
     */
    public static final String GREETING = """
            Hail, traveller! I am Hermes, herald of Olympus.
            What errand wouldst thou have me run?""";

    private static final String DIVIDER =
            "____________________________________________________________";

    /** The Hermes wordmark printed above the greeting when the console starts. */
    private static final String LOGO = """
             _   _
            | | | | ___ _ __ _ __ ___   ___  ___
            | |_| |/ _ \\ '__| '_ ` _ \\ / _ \\/ __|
            |  _  |  __/ |  | | | | | |  __/\\__ \\
            |_| |_|\\___|_|  |_| |_| |_|\\___||___/""";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the banner and greeting shown once when Hermes starts. */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(LOGO);
        System.out.println(GREETING);
        System.out.println(DIVIDER);
    }

    /**
     * Reports whether the user has typed anything more.
     *
     * @return false once input has run out, so the caller can stop reading.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one line of input.
     *
     * @return the line with surrounding whitespace removed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints a single message wrapped in divider lines, so every reply from
     * Hermes has the same shape.
     *
     * @param message the text to show to the user.
     */
    public void show(String message) {
        System.out.println(DIVIDER);
        System.out.println(message.strip());
        System.out.println(DIVIDER);
        System.out.println();
    }

    /**
     * Builds the error shown when some stored records could not be read.
     *
     * <p>Skipped lines are not held in memory, so the next save rewrites the
     * file without them. Saying so up front gives the user the chance to
     * repair the file before that happens.
     *
     * <p>This only composes the warning and leaves showing it to the caller,
     * because the window and the console show it in different places. It is
     * static because the wording describes the situation rather than any one
     * way of displaying it.
     *
     * @param skippedLines how many lines were unreadable, always at least one.
     * @param path where those records are stored, so the message can name it.
     * @return the error to show the user.
     */
    public static String formatLoadingError(int skippedLines, String path) {
        return String.format("""
                Error: Hermes could not read %d line%s in your task file, %s.
                Unreadable lines will be lost the next time Hermes saves.
                To keep them, close Hermes and fix the file before starting Hermes again.
                """, skippedLines, skippedLines == 1 ? "" : "s", path);
    }

    /**
     * Builds the error shown when the data file exists but could not be opened.
     *
     * <p>It is written plainly rather than in Hermes's usual voice. The user
     * must understand it at once and know how to fix it.
     *
     * @param path where the tasks are stored, so the message can name it.
     * @return the error to show the user.
     */
    public static String formatUnreadableError(String path) {
        return String.format("""
                Error: Hermes could not open your task file, %s.
                Your saved tasks have not been loaded and no changes will be saved.
                To fix this:
                1. Close Hermes.
                2. Make sure %s is a file you have permission to read and write.
                3. Start Hermes again.
                """, path, path);
    }

    /**
     * Reports something that went wrong.
     *
     * <p>Errors are framed exactly like any other reply; this exists so callers
     * can say which of the two they mean.
     *
     * @param message the explanation to show to the user.
     */
    public void showError(String message) {
        show(message);
    }
}
