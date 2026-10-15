package atlas.ui;

import java.util.Scanner;

import atlas.AtlasSession;

/**
 * Deals with interactions with the user: reading commands and printing output.
 */
public class Ui implements AtlasSession.Output {

    private static final String BANNER = "     _  _____ _        _    ____\n"
            + "    / \\|_   _| |      / \\  / ___|\n"
            + "   / _ \\ | | | |     / _ \\ \\___ \\\n"
            + "  / ___ \\| | | |___ / ___ \\ ___) |\n"
            + " /_/   \\_\\_| |_____/_/   \\_\\____/\n";

    private final Scanner in;

    /**
     * Creates a user interface that reads commands from the supplied scanner.
     *
     * @param in scanner providing user input.
     */
    public Ui(Scanner in) {
        this.in = in;
    }

    /** Prints the startup banner. */
    public void printBanner() {
        System.out.println(BANNER);
    }

    /**
     * Prints a message inside a speech bubble.
     * The bubble width adapts to the message length.
     *
     * @param message message to display.
     */
    @Override
    public void speak(String message) {
        printBubble(message, '─');
    }

    /**
     * Prints an error inside a bubble bordered with exclamation marks, so a
     * problem stands out from Atlas's ordinary replies.
     *
     * @param message error message to display.
     */
    @Override
    public void speakError(String message) {
        printBubble(message, '!');
    }

    /**
     * Prints one speech bubble whose border is drawn with the given character.
     *
     * @param message message to display.
     * @param border character used to draw the top and bottom border.
     */
    private void printBubble(String message, char border) {
        String rule = String.valueOf(border).repeat(message.length() + 2);
        System.out.println("╭" + rule + "╮");
        System.out.println("│ " + message + " │");
        System.out.println("╰" + rule + "╯");
    }

    /**
     * Prints a line without decoration (used for task listings).
     *
     * @param text text to print.
     */
    @Override
    public void print(String text) {
        System.out.println(text);
    }

    /**
     * Reads the next command line from the user.
     *
     * @return the next line supplied by the user, or {@code null} when the input
     *     has ended, e.g. Ctrl+D on a terminal or a script that ran out of lines.
     */
    public String readLine() {
        return in.hasNextLine() ? in.nextLine() : null;
    }
}
