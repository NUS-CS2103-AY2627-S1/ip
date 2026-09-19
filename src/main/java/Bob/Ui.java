package bob;

import java.util.Scanner;

/** Handles input from and output to the user. */
public class Ui {
    private final Scanner scanner;
    private StringBuilder capturedOutput;

    /**
     * Creates a UI backed by standard input and output.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Shows the application greeting.
     */
    public void showWelcome() {
        show("Hello! I'm Bob.");
        show("What can I do for you?");
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return Next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a line to the user.
     *
     * @param message Message to display.
     */
    public void show(String message) {
        if (capturedOutput != null) {
            capturedOutput.append(message).append(System.lineSeparator());
        } else {
            System.out.println(message);
        }
    }

    /**
     * Displays an application error to the user.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        show("OOPS!!! " + message);
    }

    /**
     * Closes the input resource.
     */
    public void close() {
        scanner.close();
    }

    /** Starts capturing output for a non-CLI caller. */
    public void startCapture() {
        capturedOutput = new StringBuilder();
    }

    /** Returns and stops capturing output. */
    public String stopCapture() {
        String output = capturedOutput.toString().trim();
        capturedOutput = null;
        return output;
    }

}
