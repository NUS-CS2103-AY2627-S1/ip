package nimbus.ui;

import java.util.Scanner;

/** Handles all console interaction with the user. */
public class Ui implements AutoCloseable {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Returns whether another line of input is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next user command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays one or more messages, each on a new line. */
    public void show(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    /** Displays a horizontal divider. */
    public void showLine() {
        show(DIVIDER);
    }

    @Override
    public void close() {
        scanner.close();
    }
}
