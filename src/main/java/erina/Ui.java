package erina;

import java.util.Scanner;

/**
 * Owns all interaction with the user: reading what they type and printing
 * Erina's replies in a consistent format.
 *
 * <p>Keeping every print in one class means the rest of the program deals in
 * <em>what</em> to say, while this class alone decides <em>how</em> it looks.
 * Swapping the console for something else later (such as a GUI) then touches
 * only this class.
 */
public class Ui {
    /** Erina's opening message, shared by the console and the GUI. */
    public static final String GREETING =
            "Good day. I'm Erina, and I keep impeccable lists.\n"
            + "What shall we put in order today? Type help to see what I can do.";

    /** Erina's farewell message, shared by the console and the GUI. */
    public static final String FAREWELL = "Everything is in order. Until next time!";

    /** Horizontal rule used to visually separate each of Erina's replies. */
    private static final String DIVIDER =
            "    ____________________________________________________________";

    /** Where user input is read from. */
    private final Scanner scanner = new Scanner(System.in);

    /** Creates a Ui that reads from standard input and prints to standard output. */
    public Ui() {
    }

    /** Prints the banner and Erina's opening message. */
    public void showWelcome() {
        String banner = " _____      _             \n"
                + "| ____|_ __(_)_ __   __ _ \n"
                + "|  _| | '__| | '_ \\ / _` |\n"
                + "| |___| |  | | | | | (_| |\n"
                + "|_____|_|  |_|_| |_|\\__,_|\n";
        System.out.println(banner);

        reply(GREETING.split("\n"));
    }

    /** Prints Erina's farewell message, shown just before the program ends. */
    public void showGoodbye() {
        reply(FAREWELL);
    }

    /**
     * Reads the next line the user types.
     *
     * @return the line, or {@code null} once there is no more input
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    /**
     * Prints one reply, wrapped in horizontal rules so it stands out from
     * the text the user typed.
     *
     * @param lines the lines of the reply, printed one per output line
     */
    public void reply(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println("     " + line);
        }
        System.out.println(DIVIDER);
    }
}
