package bags.ui;

import java.util.Scanner;

/**
 * Handles the chatbot's general messages and prompts shown to the user.
 */
public class Ui {

    private static final String DIVIDER =
            "____________________________________________________________";

    private static final String BANNER =
            " ____                  \n"
                    + "| __ )  __ _  __ _ ___ \n"
                    + "|  _ \\ / _` |/ _` / __|\n"
                    + "| |_) | (_| | (_| \\__ \\\n"
                    + "|____/ \\__,_|\\__, |___/\n"
                    + "               |___/";

    private final Scanner scanner;

    /**
     * Creates a user interface with a scanner for reading user input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reads a command from the user.
     *
     * @return the user input
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Closes the scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Shows the welcome message.
     */
    public void showWelcome() {
        showDivider();
        System.out.println(BANNER);
        showDivider();
        System.out.println("🎒 Hi! I'm your trusty bag, here to store all your tasks.");
        showPrompt();
    }

    /**
     * Shows the prompt message.
     */
    public void showPrompt() {
        showDivider();
        System.out.println("What can I do for you?");
        showDivider();
    }

    /**
     * Shows the goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        showDivider();
    }

    /**
     * Shows a divider line.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }
}
