package rambo.ui;

/**
 * Constants used throughout the Rambo application.
 * Provides ANSI color codes and divider strings for UI formatting.
 */
public final class Constants {
    /** Display name shared by the chat window and command-line interface. */
    public static final String BOT_NAME = "Rambo";
    /** Greeting that introduces the chatbot's encouraging mission-commander personality. */
    public static final String WELCOME_MESSAGE = BOT_NAME + " reporting for duty! Ready when you are.\n"
            + "Let's tackle today's missions, one task at a time.";
    /** Farewell shared by both interfaces. */
    public static final String GOODBYE_MESSAGE = "Stand down and recharge. Rambo out!";

    /** ANSI green color code for terminal text highlighting. */
    public static final String ANSI_GREEN = "\u001B[32m";
    /** ANSI red color code for terminal text highlighting. */
    public static final String ANSI_RED = "\033[91m";
    /** ANSI reset color code to restore default terminal text color. */
    public static final String ANSI_RESET = "\u001B[0m";

    private static final String DIVIDER = "===========================================================";

    private Constants() {
    }

    /**
     * Prints a divider that visually separates sections.
     */
    public static void divider() {
        System.out.println("\n");
        System.out.println(DIVIDER);
        System.out.println("\n");
    }

    /**
     * Prints a divider labelled with the supplied application name.
     *
     * @param appName name displayed in the divider
     */
    public static void divider(String appName) {
        System.out.println("\n");
        System.out.println(String.format("============================ %s ==============================", appName));
        System.out.println("\n");
    }
}
