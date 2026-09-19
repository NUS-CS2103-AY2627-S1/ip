package gary.ui;

import java.util.Scanner;

/**
 * Handles console interactions with the user.
 */
public class Ui {
    private static final String BANNER = "██████╗   █████╗ ██████╗ ██╗   ██╗\n"
            + "██╔════╝ ██╔══██╗██╔══██╗╚██╗ ██╔╝\n"
            + "██║  ███╗███████║██████╔╝ ╚████╔╝ \n"
            + "██║   ██║██╔══██║██╔══██╗  ╚██╔╝  \n"
            + "╚██████╔╝██║  ██║██║  ██║   ██║   \n"
            + " ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝   \n";
    private static final String DIVIDER = "____________________________________________________________\n";

    private final Scanner scanner;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available.
     *
     * @return True if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command entered by the user.
     *
     * @return Next user command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the chatbot greeting.
     */
    public void showWelcome() {
        System.out.println(DIVIDER + "\n"
                + BANNER + "\n"
                + "Meow! I'm Gary.\n"
                + "What can I do for you?\n"
                + DIVIDER + "\n");
    }

    /**
     * Shows a divider between user interactions.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Shows one response line to the user.
     *
     * @param message Response line to show.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
}
