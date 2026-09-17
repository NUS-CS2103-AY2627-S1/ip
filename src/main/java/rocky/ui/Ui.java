package rocky.ui;

import java.util.Scanner;

/**
 * The text UI: reads commands from standard input and prints Rocky's
 * replies between divider lines.
 */
public class Ui {
    private static final String LINE =
            "    ____________________________________________________________";
    private static final String BANNER =
            " ____   ___   ____ _  ______   __\n"
                    + "|  _ \\ / _ \\ / ___| |/ /\\ \\ / /\n"
                    + "| |_) | | | | |   | ' /  \\ V / \n"
                    + "|  _ <| |_| | |___| . \\   | |  \n"
                    + "|_| \\_\\\\___/ \\____|_|\\_\\  |_|  \n";
    private static final String INDENT = "     ";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints Rocky's banner and the given greeting.
     *
     * @param greeting Rocky's opening message.
     */
    public void showWelcome(String greeting) {
        System.out.println(LINE);
        System.out.println(BANNER);
        printIndented(greeting);
        System.out.println(LINE);
    }

    /** Returns true if there is another command to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Returns the next command line the user typed. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints a reply between divider lines.
     *
     * @param reply the reply to show, which may span several lines.
     */
    public void showReply(String reply) {
        System.out.println(LINE);
        printIndented(reply);
        System.out.println(LINE);
    }

    /** Stops reading input. */
    public void close() {
        scanner.close();
    }

    /** Prints a possibly multi-line text, indented to match the layout. */
    private static void printIndented(String text) {
        for (String line : text.split("\n")) {
            System.out.println(INDENT + line);
        }
    }
}
