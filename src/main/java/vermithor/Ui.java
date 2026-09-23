package vermithor;

import java.util.Scanner;

/** Handles console input and output for Vermithor. */
public class Ui implements AutoCloseable {
    private final Scanner scanner;

    /** Creates a console user interface. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Returns whether another command line is available to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command line. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints one or more messages in order. */
    public void show(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }

    /** Closes the input stream wrapper. */
    @Override
    public void close() {
        scanner.close();
    }
}
