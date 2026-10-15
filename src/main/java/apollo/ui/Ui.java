package apollo.ui;

import java.util.Scanner;

/** Handles console input and output. */
public class Ui {
    private static final String GREETING =
            "Greetings, young mortal! Apollo, lord of light and prophecy, "
                    + "stands ready to order your tasks beneath the sun.";

    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Reads and returns the next command entered by the user. */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /** Returns whether another console command is available to read. */
    public boolean hasNextCommand() {
        return this.scanner.hasNextLine();
    }

    /** Closes the console input scanner. */
    public void close() {
        this.scanner.close();
    }

    /** Prints the greeting message to the console. */
    public void showGreeting() {
        showBarrier();
        System.out.println(GREETING);
    }

    /** Prints a barrier that marks the beginning of the console session. */
    public void showBarrier() {
        System.out.println("=======================================================================");
    }

    /** Prints a separator between a command and its response. */
    public void showSeparator() {
        System.out.println("-----------------------------------------------------------------------");
    }

    /** Prints a response produced by Apollo's command-processing core. */
    public void showResponse(String response) {
        System.out.println(response);
    }
}
