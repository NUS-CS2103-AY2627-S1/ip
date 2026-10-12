package bogos;

import java.util.Scanner;

/**
 * Handles all console input and output for the Bogos application.
 */
public class Ui {
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final String INDENT = "         ";
    private static final String BANNER = """
      ___             __ _                  
     | _ )    ___    / _` |   ___     ___   
     | _ \\   / _ \\   \\__, |  / _ \\   (_-<   
     |___/   \\___/   |___/   \\___/   /__/_  
   _|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"|_|\"\"\"\"\"| 
   \"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'\"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]""";
    private static final String GOODBYE = "Bye bye! :]";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return True if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return The next command.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the application banner and greeting.
     */
    public void showWelcome() {
        System.out.println(BANNER);
    }

    /**
     * Displays the application farewell.
     */
    public void showGoodbye() {
        showMessage(GOODBYE);
    }

    /**
     * Displays a horizontal divider.
     */
    public void showDivider() {
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Displays an indented application message.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        System.out.println(message.isEmpty() ? "" : INDENT + message);
    }
}
