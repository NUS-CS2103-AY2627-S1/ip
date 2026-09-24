package murphy.ui;

import java.util.Scanner;

/** Handles Murphy's console input and output. */
public class Ui implements AutoCloseable {
    /** The line separator used to frame Murphy's console messages. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** Reads commands from the user. */
    private final Scanner scanner;

    /** Creates a UI connected to standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Murphy's welcome message. */
    public void showWelcome() {
        String banner = "M   M  U   U  RRRR   PPPP   H   H  Y   Y\n"
                + "MM MM  U   U  R   R  P   P  H   H   Y Y\n"
                + "M M M  U   U  RRRR   PPPP   HHHHH    Y\n"
                + "M   M  U   U  R  R   P      H   H    Y\n"
                + "M   M   UUU   R   R  P      H   H    Y\n";
        showSeparator();
        System.out.println(banner + "Hi there! I'm Murphy, your command-line conversationalist.\n"
                + "What can I do for you? (I promise not to judge your typing.)");
        showSeparator();
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays Murphy's message separator. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /** Displays a message to the user. */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /** Releases the input resource. */
    @Override
    public void close() {
        scanner.close();
    }
}
