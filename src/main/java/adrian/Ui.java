package adrian;

import java.util.Scanner;

/**
 * Handles console input and output for the Adrian application.
 */
public class Ui {
    private static final String BANNER = "    _       _      _             \n"
            + "   / \\   __| |_ __(_) __ _ _ __  \n"
            + "  / _ \\ / _` | '__| |/ _` | '_ \\ \n"
            + " / ___ \\ (_| | |  | | (_| | | | |\n"
            + "/_/   \\_\\__,_|_|  |_|\\__,_|_| |_|\n";
    private static final String DIVIDER = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the application banner and welcome message.
     */
    public void showWelcome() {
        showDivider();
        System.out.println(BANNER);
        System.out.println("Adrian online. Hello, Rocky.");
        System.out.println("Mission tasks ready. Amaze. Amaze. Amaze.");
        showDivider();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a response produced by Adrian.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Displays a horizontal separator line.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays an error indicating that saved tasks could not be loaded.
     */
    public void showLoadingError() {
        System.out.println("OOPS!!! Rocky, the mission log could not be loaded.");
    }

    /**
     * Releases the input scanner used by this user interface.
     */
    public void close() {
        scanner.close();
    }
}
