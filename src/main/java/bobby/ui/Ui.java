package bobby.ui;

import java.util.Scanner;

/**
 * Handles all console input and output for Bobby.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "BBBB   OOO   BBBB  BBBB  Y   Y\n"
            + "B   B O   O  B   B B   B  Y Y\n"
            + "BBBB  O   O  BBBB  BBBB    Y\n"
            + "B   B O   O  B   B B   B   Y\n"
            + "BBBB   OOO   BBBB  BBBB    Y";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome banner.
     */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println(LINE);
        System.out.println("     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.");
        System.out.println("     What charge wouldst thou have me enter, amend, or proclaim?");
        System.out.println(LINE);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the command line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a separator before or after a command response.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Displays Bobby's response to a command.
     *
     * @param response the text to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Displays an error when saved tasks cannot be loaded.
     */
    public void showLoadingError() {
        showLine();
        System.out.println("     Regrettably, the saved register could not be read.");
        System.out.println("     I shall therefore commence with an empty register.");
        showLine();
    }

    /**
     * Displays Bobby's farewell message.
     */
    public void showGoodbye() {
        showLine();
        System.out.println("     I humbly take my leave. May good fortune attend thee until next we meet.");
        showLine();
    }
}
