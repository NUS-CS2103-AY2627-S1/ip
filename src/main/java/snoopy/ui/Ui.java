package snoopy.ui;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles user input and output for Snoopy's text interface.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = "  ____\n"
            + " / ___| _ __   ___   ___  _ __  _   _\n"
            + " \\___ \\| '_ \\ / _ \\ / _ \\| '_ \\| | | |\n"
            + "  ___) | | | | (_) | (_) | |_) | |_| |\n"
            + " |____/|_| |_|\\___/ \\___/| .__/ \\__, |\n"
            + "                            |_|    |___/";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a text interface connected to standard input and output.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a text interface with specified streams for automated testing.
     *
     * @param scanner Source of user commands.
     * @param output Destination for chatbot messages.
     */
    Ui(Scanner scanner, PrintStream output) {
        assert scanner != null : "Command scanner must be provided";
        assert output != null : "Output stream must be provided";
        this.scanner = scanner;
        this.output = output;
    }

    /**
     * Displays the application banner and startup message.
     *
     * @param welcomeMessage Greeting and any loading error.
     */
    public void showWelcome(String welcomeMessage) {
        assert welcomeMessage != null : "Welcome message must be provided";
        String[] sections = welcomeMessage.split("\\n\\n", 2);
        output.println(DIVIDER);
        output.println(BANNER);
        output.println(sections[0]);
        showDivider();
        if (sections.length == 2) {
            output.println(" " + sections[1]);
            showDivider();
        }
    }

    /**
     * Checks whether another command is available.
     *
     * @return {@code true} when another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command exactly as entered.
     *
     * @return Next user command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays one chatbot response followed by a divider.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        assert response != null : "Response must be provided";
        output.println(response);
        showDivider();
    }

    private void showDivider() {
        output.println(DIVIDER);
    }
}
