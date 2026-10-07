package drax;

import java.util.Scanner;

/**
 * Handles terminal input and presents response strings produced by Drax.
 */
public class Ui {
    private static final String BANNER = """
            ██████╗ ██████╗  █████╗ ██╗  ██╗
            ██╔══██╗██╔══██╗██╔══██╗╚██╗██╔╝
            ██║  ██║██████╔╝███████║ ╚███╔╝
            ██║  ██║██╔══██╗██╔══██║ ██╔██╗
            ██████╔╝██║  ██║██║  ██║██╔╝ ██╗
            ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝""";

    private final Scanner reader;

    /** Creates a console UI that reads commands from standard input. */
    public Ui() {
        this.reader = new Scanner(System.in);
    }

    /**
     * Adds console-only branding to the interface-neutral greeting.
     *
     * @param greeting greeting supplied by Drax.
     * @return the banner and greeting formatted for the terminal
     */
    public String getConsoleGreeting(String greeting) {
        return BANNER + "\n\n" + greeting;
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return true if another command can be read
     */
    public boolean hasNextCommand() {
        return reader.hasNextLine();
    }

    /**
     * Reads the next command from standard input.
     *
     * @return the next input line
     */
    public String readCommand() {
        return reader.nextLine();
    }

    /**
     * Displays a message on standard output.
     *
     * @param message message to display.
     */
    public void show(String message) {
        System.out.println(message);
    }

    /** Closes the standard-input reader. */
    public void close() {
        reader.close();
    }
}
