package emma;

import java.util.Scanner;

/**
 * Shows Emma's side of the conversation on the command line, and reads what the user types.
 */
public class Ui {

    private static final String ANSI_ESC = String.valueOf((char) 27);
    private static final String COLOR_BLUE = ANSI_ESC + "[34m";
    private static final String COLOR_ORANGE = ANSI_ESC + "[38;5;208m";
    private static final String COLOR_RESET = ANSI_ESC + "[0m";

    private static final String BANNER = " _____\n"
            + "| ____|  _ __ ___   _ __ ___    __ _ \n"
            + "|  _|   | '_ ` _ \\  | '_ ` _ \\   / _` |\n"
            + "| |___  | | | | | | | | | | | | | (_| |\n"
            + "|_____| |_| |_| |_| |_| |_| |_|  \\__,_|";
    private static final String GREETING = "Hey there! I'm Emma.\n"
            + "What can I do for you?";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns Emma's opening line, so that another front end can greet the user the same way.
     *
     * @return the greeting.
     */
    public static String getGreeting() {
        return GREETING;
    }

    /** Prints the banner and Emma's opening line. */
    public void showWelcome() {
        System.out.println(BANNER);
        showResponse(GREETING);
    }

    /**
     * Prints one response under Emma's name.
     *
     * @param response what Emma has to say.
     */
    public void showResponse(String response) {
        System.out.println();
        System.out.println(COLOR_BLUE + "Emma" + COLOR_RESET);
        System.out.println(response);
    }

    /**
     * Prompts under the user's name and reads the line they type.
     *
     * @return the line typed, or null once there is no more input.
     */
    public String readCommand() {
        System.out.println();
        System.out.println(COLOR_ORANGE + "user" + COLOR_RESET);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }

    /** Stops reading input. */
    public void close() {
        scanner.close();
    }
}
