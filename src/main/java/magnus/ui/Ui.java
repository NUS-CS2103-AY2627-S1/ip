package magnus.ui;

/**
 * Provides Magnus's welcome messages and displays them on the command line.
 */
public class Ui {
    private static final String UI_BANNER = """
            ███╗   ███╗ █████╗  ██████╗ ███╗   ██╗██╗   ██╗███████╗
            ████╗ ████║██╔══██╗██╔════╝ ████╗  ██║██║   ██║██╔════╝
            ██╔████╔██║███████║██║  ███╗██╔██╗ ██║██║   ██║███████╗
            ██║╚██╔╝██║██╔══██║██║   ██║██║╚██╗██║██║   ██║╚════██║
            ██║ ╚═╝ ██║██║  ██║╚██████╔╝██║ ╚████║╚██████╔╝███████║
            ╚═╝     ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═══╝ ╚═════╝ ╚══════╝
            """;
    private static final String UI_GREETING = """
            Greetings! I'm Magnus, your task tactician.
            The board is ready. What shall be our next move?
            """;
    private static final String UI_DIVIDER = "____________________________________________________________";
    private static final String UI_INDENT = "\t";

    /**
     * Creates a command-line user interface.
     */
    public Ui() {
    }

    /**
     * Returns the banner shared by the command-line and graphical interfaces.
     *
     * @return The Magnus banner without a trailing line break.
     */
    public static String getBanner() {
        return UI_BANNER.stripTrailing();
    }

    /**
     * Returns the greeting shared by the command-line and graphical interfaces.
     *
     * @return The Magnus greeting without a trailing line break.
     */
    public static String getGreeting() {
        return UI_GREETING.stripTrailing();
    }

    /**
     * Prints the application banner, greeting, and surrounding dividers.
     */
    public void showWelcome() {
        printDivider("");
        System.out.print(UI_BANNER);
        System.out.print(UI_GREETING);
        printDivider("");
    }

    /**
     * Prints an indented divider followed by a blank line.
     */
    public void printDivider() {
        printDivider(UI_INDENT);
    }

    /**
     * Prints a divider with the supplied prefix, followed by a blank line.
     *
     * @param prefix The text to print immediately before the divider.
     */
    private void printDivider(String prefix) {
        System.out.println(prefix + UI_DIVIDER);
        System.out.println();
    }

    /**
     * Prints a message followed by a line break.
     *
     * @param message The message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
}
