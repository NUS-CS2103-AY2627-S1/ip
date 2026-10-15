package skynet;

import java.util.Scanner;

/**
 * Handles user interaction and displays messages to the user.
 */
public class Ui {
    private final Scanner scanner;

    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome banner and greeting message.
     */
    public void showWelcome() {
        String banner = """
                 ____  _          _   _      _
                / ___|| | ___   _| \\ | | ___| |_
                \\___ \\| |/ / | |  \\| |/ _ \\ __|
                 ___) |   <| |_| | |\\  |  __/ |_
                |____/|_|\\_\\__, |_| \\_|\\___|\\__|
                             |___/
                """;

        System.out.println(banner);
        System.out.println("Welcome to SkyNET.");
        System.out.println("How may we assist you today?");
    }

    /**
     * Reads and returns the next command entered by the user.
     *
     * @return the command entered by the user
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return "bye";
        }
        return scanner.nextLine();
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        System.out.println("The Future is now. Chat Terminated.");
    }

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null.");
        }
        System.out.println("ERROR: " + message);
    }
}
