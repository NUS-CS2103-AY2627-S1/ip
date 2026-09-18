package korvus.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * UI class for reading user inputs and writing bot replies.
 */
public class CommandLineUI implements UI {
    private static int DEFAULT_MAX_LINE_LENGTH = 70;
    private static char DIVIDER_CHAR = '_';

    private Scanner inputScanner;
    private PrintStream outputStream;
    private int maxLength;

    /**
     * Returns an instance of CommandLineUI.
     *
     * @param inStream Input stream that users will input from.
     * @param outStream Output stream that the UI should print to.
     * @param maxLength Max length of characters in the outputStream.
     */
    public CommandLineUI(InputStream inStream, PrintStream outStream, int maxLength) {
        this.inputScanner = new Scanner(inStream);
        this.outputStream = outStream;
        this.maxLength = maxLength;
    }

    /**
     * Returns an instance of CommandLineUI.
     *
     * @param inStream Input stream that users will input from.
     * @param outStream Output stream that the CommandLineUI should print to.
     */
    public CommandLineUI(InputStream inStream, PrintStream outStream) {
        this(inStream, outStream, DEFAULT_MAX_LINE_LENGTH);
    }

    /**
     * Returns the user's input. The program pauses until a new input is detected.
     *
     * @return User's input into the input stream.
     */
    public String listen() {
        return inputScanner.nextLine().trim();
    }

    /**
     * Sets the max length of the output.
     *
     * @param length New length to be updated
     * @return The previous max length used.
     */
    public int setMaxLength(int length) {
        int oldLength = this.maxLength;
        this.maxLength = length;
        return oldLength;
    }

    /**
     * Returns the max length of the output.
     *
     * @return The max length of the output.
     */
    public int getMaxLength() {
        return this.maxLength;
    }

    /**
     * Sends the provided message into the output stream.
     * Custom formatting is used to make the output more readable.
     *
     * @param msg Message to be outputted.
     */
    public void say(String msg) {
        if (msg.isEmpty()) {
            outputStream.println("> Caw");
            return;
        }

        int lastSpace = -1;
        int lastLine = -1;
        StringBuilder newText = new StringBuilder("> ");

        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == ' ') {
                lastSpace = i;
                continue;
            } else if (msg.charAt(i) == '\n') {
                newText.append(msg + "\n  ", lastLine + 1, i);
                lastLine = i;
                continue;
            } else if (i - lastLine <= maxLength - 2) {
                continue;
            }

            if (lastSpace > lastLine) {
                newText.append(msg + "\n  ", lastLine + 1, lastSpace);
                lastLine = lastSpace;
            } else {
                newText.append(msg + "\n ", lastLine + 1, lastLine + maxLength - 1);
                lastLine += maxLength - 2;
            }
        }
        newText.append(msg.substring(lastLine + 1));
        outputStream.println(newText);
    }

    /**
     * Prints the provided message into the output stream with no formatting
     *
     * @param s Message to be outputted.
     */
    public void rawPrint(String s) {
        outputStream.println(s);
    }

    /**
     * Prints a divider line into the output stream.
     */
    public void divider() {
        StringBuilder divider = new StringBuilder();
        divider.repeat(DIVIDER_CHAR, maxLength);

        outputStream.println(divider);
    }
}
