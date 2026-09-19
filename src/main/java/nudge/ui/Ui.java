package nudge.ui;

import java.util.Scanner;

import nudge.NudgeResponse;

/**
 * Handles console input and output for Nudge.
 */
public class Ui {
    private static final String OUTPUT_DETAIL_INDENTATION = "      ";
    private static final String OUTPUT_INDENTATION = "    > ";
    private static final String OUTPUT_SEPARATOR = "_".repeat(60);

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from the standard input stream.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return true if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the application greeting.
     *
     * @param welcomeMessage greeting that introduces Nudge's personality.
     */
    public void showWelcome(String welcomeMessage) {
        String banner = " _   _           _            \n"
                + "| \\ | |_   _  __| | __ _  ___ \n"
                + "|  \\| | | | |/ _` |/ _` |/ _ \\\n"
                + "| |\\  | |_| | (_| | (_| |  __/\n"
                + "|_| \\_|\\__,_|\\__,_|\\__, |\\___|\n"
                + "                   |___/\n";

        System.out.println(OUTPUT_SEPARATOR);
        System.out.print(banner);
        System.out.println(OUTPUT_INDENTATION + welcomeMessage);
        System.out.println(OUTPUT_SEPARATOR);
    }

    /**
     * Prints a response and its supporting details between separator lines.
     *
     * @param response response to display.
     */
    public void showResponse(NudgeResponse response) {
        System.out.println(OUTPUT_SEPARATOR);
        System.out.println(OUTPUT_INDENTATION + response.getHeader());
        for (String detail : response.getDetails()) {
            System.out.println(OUTPUT_DETAIL_INDENTATION + detail);
        }
        if (response.hasFooter()) {
            System.out.println(OUTPUT_INDENTATION + response.getFooter());
        }
        System.out.println(OUTPUT_SEPARATOR);
    }
}
