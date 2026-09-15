package bob.ui;

import java.util.Scanner;

/**
 * Handles command-line interactions between Bob and the user.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        this(new Scanner(System.in));
    }

    /**
     * Creates a user interface that reads from the specified scanner.
     *
     * @param scanner Scanner supplying command-line input.
     */
    Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return Trimmed command entered by the user, or {@code bye} at end-of-input.
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return "bye";
        }
        return scanner.nextLine().trim();
    }

    /**
     * Shows Bob's startup message.
     *
     * @param message Startup message to display.
     */
    public void showWelcome(String message) {
        printLine();
        printBanner();
        printMessage(message);
        printLine();
    }

    /**
     * Shows Bob's response to a command.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        printLine();
        printMessage(response);
        printLine();
    }

    /**
     * Closes the input scanner used by this user interface.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Prints each line of a message with the command-line interface indentation.
     *
     * @param message Message to print.
     */
    private void printMessage(String message) {
        for (String line : message.split("\\R")) {
            System.out.println("        " + line);
        }
    }

    /**
     * Prints the horizontal separator used between command-line messages.
     */
    private void printLine() {
        System.out.println("        ___________________________________________________________________");
    }

    /**
     * Prints Bob's command-line banner.
     */
    private void printBanner() {
        System.out.println("        ____        _     ");
        System.out.println("        |  _ \\      | |    ");
        System.out.println("        | |_) | ___ | |__  ");
        System.out.println("        |  _ < / _ \\| '_ \\ ");
        System.out.println("        | |_) | (_) | |_) |");
        System.out.println("        |____/ \\___/|_.__/ ");
    }
}
