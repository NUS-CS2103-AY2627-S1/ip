package baron.ui;

/**
 * Handles user interface interactions for the Baron application.
 */
public class Ui {
    /** Name displayed in the welcome banner and greeting messages. */
    public static final String NAME = "Baron";

    /** ASCII banner shown when the application starts. */
    private static final String BANNER = "__________\n"
            + "\\______   \\_____ _______  ____   ____\n"
            + " |    |  _/\\__  \\\\_  __ \\/  _ \\ /    \\\n"
            + " |    |   \\ / __ \\|  | \\(  <_> )   |  \\\n"
            + " |______  /(____  /__|   \\____/|___|  /\n"
            + "        \\/      \\/                  \\/";
    /**
     * Prints a divider line used to separate UI sections.
     */
    private static void printLine() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Prints the ASCII banner used at startup.
     */
    private static void printBanner() {
        System.out.println(BANNER);
    }

    /**
     * Displays the welcome message and startup banner.
     */
    private static void greet() {
        printLine();
        printBanner();
        System.out.println("Greetings, traveler. I am " + NAME + ", keeper of a Barathos ledger.\n"
                + "Speak your next challenge, and I shall see it recorded.");
        printLine();
    }

    /**
     * Displays the farewell message when the application exits.
     */
    private static void bye() {
        printLine();
        System.out.println("Until our paths cross again, traveler. May your strength outlast the sands.");
        printLine();
    }

    /**
     * Initializes the user interface and displays the welcome message.
     */
    public static void start() {
        greet();
    }

    /**
     * Displays the result of a command execution.
     *
     * @param result The result message to display.
     */
    public static void displayResult(String result) {
        if (result == null || result.isEmpty()) {
            return;
        }
        printLine();
        System.out.println(result);
        printLine();
    }

    /**
     * Displays an error message.
     *
     * @param e The exception to display.
     */
    public static void displayError(Exception e) {
        printLine();
        System.out.println(e.getMessage());
        printLine();
    }

    /**
     * Closes the user interface and displays the farewell message.
     */
    public static void close() {
        bye();
    }
}
