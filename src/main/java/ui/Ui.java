package ui;

import java.util.Scanner;

/**
 * Handles all user interaction concerns for the application.
 * This includes reading commands from the console and formatting output
 * in the chatbot-style speech bubble.
 */
public class Ui {
    private final Scanner scanner;

    /**
     * Creates a UI handler with a scanner bound to the standard input stream.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line entered by the user.
     * @return the user command as a string
     */
    public String readCommand() {
        if (!scanner.hasNextLine()) {
            return "No input detected, closing";
        }
        return scanner.nextLine();
    }

    /**
     * Formats a string as a speech bubble and prints it to the console.
     * @param text message to display
     * @return the formatted string that was printed
     */
    public String echo(String text) {
        String s = styleString(text);
        System.out.println(s);
        return s;
    }

    /**
     * Wraps a message visually in the chatbot's speech bubble.
     * @param text the raw message
     * @return the styled version of the message
     */
    public String styleString(String text) {
        String styledString = "";

        styledString += "\t______________________________________________\n\t";

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                styledString += c + "\t";
            } else {
                styledString += c;
            }
        }

        styledString += "\n\t______________________________________________";
        return styledString;
    }
}
