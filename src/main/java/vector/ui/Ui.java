package vector.ui;

import java.util.Scanner;

/**
 * Handles interactions with the user, including reading input and displaying messages.
 */
public class Ui {
    private static final String LINE = "    ____________________________________________________________";
    private Scanner scanner;

    /**
     * Constructs a Ui object and initializes the scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message and banner.
     */
    public void showWelcome() {
        String banner = " __     _______ ____ _____ ___  ____\n"
                + " \\ \\   / / ____/ ___|_   _/  _ \\|  _ \\\n"
                + "  \\ \\ / /|  _| | |     | || | | | |_) |\n"
                + "   \\ V / | |___| |___  | || |_| |  _ <\n"
                + "    \\_/  |______\\____| |_| \\___/|_| \\_\\\n";
        showLine();
        System.out.print(banner);
        showMessage("Initialization complete. I am Vector, your personal cybernetic assistant.",
                "Awaiting input protocols.");
        showLine();
    }

    /**
     * Displays a horizontal line separator.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Displays messages to the user, properly indented.
     * Supports variable number of arguments (varargs) to print multiple lines easily.
     *
     * @param messages The messages to display.
     */
    public void showMessage(String... messages) {
        java.util.Arrays.stream(messages)
                .flatMap(msg -> java.util.Arrays.stream(msg.split("\\r?\\n")))
                .map(line -> "     " + line)
                .forEach(System.out::println);
    }

    /**
     * Displays an error message to the user, properly indented and prefixed.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println("     SYSTEM FAULT: " + message);
    }

    /**
     * Reads the next command input from the user.
     *
     * @return The user's input string.
     */
    public String readCommand() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }
}
