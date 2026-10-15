package ben;

import java.util.Scanner;

/**
 * Handles all interaction with the user: reading commands from standard
 * input and printing replies in the boxed format used by the course spec.
 * <p>
 * A-MoreOOP: extracted from the main class so that formatting decisions
 * (the divider lines, the leading space on each line) live in one place.
 */
class Ui {
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the logo and greeting shown when the chatbot starts. */
    void showWelcome() {
        String logo = " ____              \n"
                + "|  _ \\ ___ _ __    \n"
                + "| |_) / _ \\ '_ \\   \n"
                + "|  _ <  __/ | | |  \n"
                + "|_| \\_\\___|_| |_|  \n";
        System.out.println(logo);
        show("Hello! I'm Ben\nWhat can I do for you?");
    }

    /** Reads the next line of input from the user. */
    String readCommand() {
        return scanner.nextLine();
    }

    /** Prints a message surrounded by horizontal divider lines. */
    void show(String message) {
        System.out.println(LINE);
        for (String line : message.split("\n")) {
            System.out.println(" " + line);
        }
        System.out.println(LINE);
    }

    /** Prints an error message (already prefixed with "OOPS!!!"). */
    void showError(String message) {
        show(message);
    }

    /** Prints the parting message when the user types "bye". */
    void showBye() {
        show("Bye. Hope to see you again soon!");
    }

    /** Releases the input stream. */
    void close() {
        scanner.close();
    }
}
