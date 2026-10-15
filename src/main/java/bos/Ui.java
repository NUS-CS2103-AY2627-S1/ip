package bos;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles text input from and output to the user.
 */
public class Ui {
    private static final String INDENT = "     ";
    private static final String DIVIDER = INDENT
            + "____________________________________________________________";
    private static final String BANNER = " ____            \n"
            + "| __ )  ___  ___ \n"
            + "|  _ \\ / _ \\/ __|\n"
            + "| |_) | (_) \\__ \\\n"
            + "|____/ \\___/|___/";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI connected to standard input and output.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied streams.
     *
     * @param input source of user commands.
     * @param output destination for user-facing messages.
     */
    Ui(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Returns whether another command is available to read.
     *
     * @return true when another input line is available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next line of input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows Bos's banner and welcome message.
     *
     * @param greeting greeting and any startup warning to show.
     */
    public void showGreeting(String greeting) {
        output.println(DIVIDER);
        output.println(BANNER);
        showResponse(greeting);
        output.println(DIVIDER);
    }

    /**
     * Shows one response, indenting each line for the terminal layout.
     *
     * @param response text to show.
     */
    public void showResponse(String response) {
        output.println(INDENT + response.replace("\n", "\n" + INDENT));
    }

    /**
     * Separates consecutive command responses.
     */
    public void showDivider() {
        output.println(DIVIDER);
    }
}
