package rice;

import java.util.Scanner;

/**
 * Handles console input and output for the Rice application.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Displays Rice's welcome message and input-format guidance.
     */
    public void greet() {
        String message = "Hello! I'm Rice.\n"
                + "What can I do for you?\n";
        System.out.println(message);
        System.out.println("Dates should be recorded in the format YYYY-MM-DD");
    }

    /**
     * Reads one complete command from standard input.
     *
     * @return command entered by the user
     */
    public String readInput() {
        String userInput = scanner.nextLine();
        return userInput;
    }

    /**
     * Displays a message to the user.
     *
     * @param message message to display
     * @return the same message that was displayed
     */
    public String show(String message) {
        System.out.println(message);
        return message;
    }
}
