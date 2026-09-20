package harold.ui;

import java.util.Scanner;

/**
 * Handles console input and output for Harold.
 */
public class Ui {
    private static final String SEPARATOR =
            "____________________________________________________________";
    private static final String TAG = " _   _                 _     _ \n"
            + "| | | | __ _ _ __ ___ | | __| |\n"
            + "| |_| |/ _` | '__/ _ \\| |/ _` |\n"
            + "|  _  | (_| | | | (_) | | (_| |\n"
            + "|_| |_|\\__,_|_|  \\___/|_|\\__,_|\n";
    private static final String BANNER = "           / \\__ /\\\n"
            + "          /  _  _  \\\n"
            + "         |  / \\/ \\  |\n"
            + "         |  \\_/\\_/  |\n"
            + "         |    __    |\n"
            + "          \\  (__)  /\n"
            + "           \\______/\n"
            + "           /|    |\\\n"
            + "          / |____| \\\n"
            + "            / || \\\n"
            + "           (_/  \\_)\\\n"
            + TAG;

    private final Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays the greeting and an optional message about loading task data.
     *
     * @param loadMessage Loading warning to display, or {@code null} when loading succeeded.
     */
    public void showWelcome(String loadMessage) {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println("Arf! I'm Harold, your mischievous Frenchie task-fetcher.");
        System.out.println("Tasks are my treats. nomnomnom ball!");
        if (loadMessage != null) {
            System.out.println("i pooped on myself... " + loadMessage);
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return {@code true} if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and returns the next command from the user.
     *
     * @return Next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the separator used between command responses.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays one potentially multi-line chatbot response.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
}
