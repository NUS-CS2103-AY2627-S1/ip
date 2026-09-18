package bobby.ui;

import java.util.Scanner;

/**
 * Handles console interactions with the user.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " ____        _     _           \n"
            + "| __ )  ___ | |__ | |__  _   _ \n"
            + "|  _ \\ / _ \\| '_ \\| '_ \\| | | |\n"
            + "| |_) | (_) | |_) | |_) | |_| |\n"
            + "|____/ \\___/|_.__/|_.__/ \\__, |\n"
            + "                         |___/ \n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads user input from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Greets the user when Bobby starts.
     */
    public void showWelcome() {
        showLine();
        System.out.print(BANNER);
        printLines("Greetings, young one.", "I am Bobby.",
                "Share your task, and we shall bring order to the day.");
        showLine();
    }

    /**
     * Returns whether another line of user input is available.
     *
     * @return true if there is another input line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command from the user.
     *
     * @return trimmed command text.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Prints the goodbye message.
     */
    public void showGoodbye() {
        System.out.println("The pond grows still. Until our paths meet again...");
    }

    /**
     * Prints an error from a user command.
     *
     * @param message error details.
     */
    public void showCommandError(String message) {
        System.out.println("The path is misty: " + message);
    }

    /**
     * Prints an error from loading the save file.
     *
     * @param message error details.
     */
    public void showLoadingError(String message) {
        System.out.println("The old scroll is clouded: " + message);
    }

    /**
     * Prints a multi-line response from Bobby.
     *
     * @param response response text to print.
     */
    public void showResponse(String response) {
        printLines(response.split("\n", -1));
    }

    /**
     * Prints the common horizontal divider.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    private void printLines(String... lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }
}
