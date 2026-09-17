package lobby.ui;

import java.util.Scanner;

/**
 * Handles all text-based interactions between Lobby and the user.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " _           _     _\n"
            + "| |    ___  | |__ | |__  _   _\n"
            + "| |   / _ \\ | '_ \\| '_ \\| | | |\n"
            + "| |__| (_) | |_) | |_) | |_| |\n"
            + "|_____\\___/|_.__/|_.__/ \\__, |\n"
            + "                         |___/";

    private final Scanner scanner;

    /**
     * Creates a console UI that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows the application banner and initial greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Lobby.");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Reads the next trimmed command, or returns {@code null} at the end of input.
     *
     * @return the next command, or {@code null} when no more input is available
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    /**
     * Starts a response with the standard divider.
     */
    public void startResponse() {
        System.out.println(DIVIDER);
    }

    /**
     * Ends a response with the standard divider.
     */
    public void endResponse() {
        System.out.println(DIVIDER);
    }

    /**
     * Shows a complete response produced by Lobby.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Shows a warning that malformed saved records were ignored.
     *
     * @param skippedLines number of invalid records.
     * @param filePath location of the save file.
     */
    public void showSkippedLinesWarning(int skippedLines, String filePath) {
        String lineWord = skippedLines == 1 ? "line" : "lines";
        System.out.println(" I skipped " + skippedLines + " invalid " + lineWord
                + " while loading " + filePath + ".");
        endResponse();
    }

    /**
     * Shows a warning that the save file could not be read.
     *
     * @param filePath location of the save file.
     */
    public void showLoadingError(String filePath) {
        System.out.println(" I couldn't read " + filePath + ", so I started with an empty task list.");
        endResponse();
    }

    /**
     * Shows the standard farewell.
     */
    public void showFarewell() {
        System.out.println(" Bye. Hope to see you again soon!");
        endResponse();
    }
}
