package baemax;

import java.util.Scanner;

/**
 * Handles console interaction: reading commands from standard input and
 * printing framed responses to standard output. Keeping this in one class
 * means the command logic never touches {@code System.in} or {@code System.out}
 * directly. The GUI front end does not use this class.
 */
public class Ui {
    /** Divider printed above and below each response. */
    private static final String DIVIDER = "__________________________________________";

    /** Source of user commands. */
    private final Scanner scanner;

    /** Creates a Ui that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the startup banner. */
    public void showBanner() {
        String banner = "╔════════════════╗\n"
                + "║     Baemax     ║\n"
                + "╚════════════════╝\n";
        System.out.println(banner);
    }

    /** Prints the divider that frames each response. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Reports whether the user has entered another command.
     *
     * @return {@code true} while standard input has another line to read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the raw command text, without the trailing newline
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints one or more response blocks in order.
     *
     * @param blocks the text blocks to print
     */
    public void show(String... blocks) {
        for (String block : blocks) {
            System.out.println(block);
        }
    }

    /** Releases the input resource. */
    public void close() {
        scanner.close();
    }
}
