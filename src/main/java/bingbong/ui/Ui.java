package bingbong.ui;

import java.util.Scanner;

/**
 * Manages user interface. Formats and prints text output blocks to the standard system console.
 */
public class Ui {

    /**
     * The horizontal dividing boundary separator line printed to the display layout.
     */
    private static final String H_LINE = "____________________________________________________________";

    /**
     * The input channel interface reader instance scanning raw keystroke lines.
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints the ASCII welcome banner to the display terminal.
     */
    public void showWelcome() {
        String banner = " ___  _             ___                 \n"
                + "| _ )(_) _ _  ___  | _ ) ___  _ _  ___  \n"
                + "| _ \\| || ' \\/ _ \\ | _ \\/ _ \\| ' \\/ _ \\ \n"
                + "|___/|_||_||_\\_, | |___/\\___/|_||_\\_, | \n"
                + "             |__/                 |__/  \n";
        System.out.println(banner + H_LINE + "\nBingBong! What can I do for you?\n" + H_LINE);
    }

    /**
     * Reads a text string entered into the terminal by the user.
     *
     * @return The unparsed string input content.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints a horizontal line border.
     */
    public void showHLine() {
        System.out.println(H_LINE);
    }

    /**
     * Prints one or more messages sequentially to the output stream.
     *
     * @param messages An arbitrary number of string messages to print.
     */
    public void print(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }


    /**
     * Prints a warning alert indicating historical file log loads failed.
     */
    public void showLoadingError() {
        System.out.println("BingBong cannot load your previous list: ");
    }

    /**
     * Prints the exit goodbye message.
     */
    public void showBye() {
        System.out.println("Bye. BingBong misses you!");
    }

}
