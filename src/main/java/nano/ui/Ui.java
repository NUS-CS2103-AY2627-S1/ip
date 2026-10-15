package nano.ui;

import java.util.Scanner;

/**
 * Handles interactions between Nano and the user.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates an Ui object for reading user input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the user's command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a message to the user.
     *
     * @param message message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the Nano welcome message.
     */
    public void showWelcome() {
        System.out.println(getWelcomeMessage());
    }

    /**
     * Returns the message shown when Nano starts.
     *
     * @return Nano's welcome banner and greeting.
     */
    public static String getWelcomeMessage() {
        String banner = "NN   NN   AAA   NN   NN   OOO \n"
                + "NNN  NN  AA AA  NNN  NN  OO OO\n"
                + "NN N NN AA   AA NN N NN OO  OO\n"
                + "NN  NNN AAAAAAA NN  NNN OO  OO\n"
                + "NN   NN AA   AA NN   NN  OOO \n";

        return banner + "\nHello! I'm Nano.\nHow may i assist you?";
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        System.out.println(getGoodbyeMessage());
    }

    /**
     * Returns the message shown when Nano exits.
     *
     * @return Nano's goodbye banner and message.
     */
    public static String getGoodbyeMessage() {
        return "BBBB   Y   Y  EEEEE\n"
                        + "B   B   Y Y   E\n"
                        + "BBBB     Y    EEEE\n"
                        + "B   B    Y    E\n"
                        + "BBBB     Y    EEEEE\n\n"
                        + "Bye! Hope to see you again soon!";
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }
}
