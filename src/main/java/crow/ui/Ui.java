package crow.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Handles text input from and output to the user.
 */
public class Ui {
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = "  ___  ____   __   _  _ \n"
            + " / __)(  _ \\ /  \\ / )( \\\n"
            + "( (__  )   /(  O )\\ /\\ /\n"
            + " \\___)(__\\_) \\__/ (_/\\_)";

    private final Scanner inputScanner;
    private final PrintStream outputStream;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied input and output streams.
     * This constructor allows the UI to be tested without replacing the global console streams.
     *
     * @param inputStream Stream from which commands are read.
     * @param outputStream Stream to which messages are written.
     */
    public Ui(InputStream inputStream, PrintStream outputStream) {
        inputScanner = new Scanner(inputStream);
        this.outputStream = outputStream;
    }

    /**
     * Displays the chatbot banner and greeting.
     */
    public void showWelcome() {
        outputStream.println(SEPARATOR);
        outputStream.println(BANNER);
        outputStream.println("Hello! I'm Crow.");
        outputStream.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Returns whether another line of user input is available.
     *
     * @return {@code true} if another command can be read.
     */
    public boolean hasNextCommand() {
        return inputScanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return Trimmed user command.
     */
    public String readCommand() {
        return inputScanner.nextLine().trim();
    }

    /**
     * Displays an error message.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        outputStream.println(" " + message);
    }

    /**
     * Displays a potentially multiline response from the command engine.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        outputStream.println(" " + response.replace("\n", "\n "));
    }

    /**
     * Displays a separator between user interactions.
     */
    public void showSeparator() {
        outputStream.println(SEPARATOR);
    }

}
