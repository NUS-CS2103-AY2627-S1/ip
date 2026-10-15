package ev.ui;

import java.util.Scanner;

/**
 * The text version of the user interface: it reads commands typed at the terminal and
 * prints each reply between two divider lines as it is produced.
 */
public class ConsoleUi extends Ui {

    private static final String LINE = "____________________________________________________________";

    private static final String BANNER = " _______     __\n"
            + "|   ____|   /  \\\n"
            + "|  |__     |    |\n"
            + "|   __|    |    |\n"
            + "|  |____    \\  /\n"
            + "|_______|    \\/\n";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns whether there is another line of input waiting.
     *
     * @return false once the input has run out, for example at the end of a piped file.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next line the user typed.
     *
     * @return the line, with the spaces around it removed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the EV banner. */
    public void showBanner() {
        System.out.println(BANNER);
    }

    @Override
    protected void show(String message) {
        super.show(message);
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
