package rambo.ui;

import java.util.Scanner;

/**
 * Handles all interactions between the Rambo application and the user.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a UI that reads input from the given scanner.
     *
     * @param scanner scanner used to read user input
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Displays the application's welcome message.
     */
    public void showWelcome() {
        showDivider(Constants.BOT_NAME);
        showLine(Constants.WELCOME_MESSAGE);
        showLine("\n");
    }

    /**
     * Displays the application's goodbye message.
     */
    public void showGoodbye() {
        showDivider();
        showLine(Constants.GOODBYE_MESSAGE);
    }

    /**
     * Displays a divider between sections.
     */
    public void showDivider() {
        Constants.divider();
    }

    /**
     * Displays a divider containing a section name.
     *
     * @param sectionName name displayed in the divider
     */
    public void showDivider(String sectionName) {
        Constants.divider(sectionName);
    }

    /**
     * Displays text followed by a line separator.
     *
     * @param message text to display
     */
    public void showLine(String message) {
        System.out.println(message);
    }

    /**
     * Displays a prompt without a line separator.
     *
     * @param message prompt to display
     */
    public void showPrompt(String message) {
        System.out.print(message);
    }

    /**
     * Returns whether another line of input is available.
     *
     * @return true if another input line is available
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next line entered by the user.
     *
     * @return the next input line
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Closes the input source used by this UI.
     */
    public void close() {
        scanner.close();
    }
}
