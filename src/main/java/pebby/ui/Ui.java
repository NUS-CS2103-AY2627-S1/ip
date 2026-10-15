package pebby.ui;

import java.util.Scanner;

import pebby.Pebby;

/**
 * Handles Pebby's console input and output.
 *
 * <p>This class keeps display details, such as the banner and separators, out of
 * the application logic in {@link Pebby}.</p>
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner;
    private final StringBuilder response;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this(new Scanner(System.in));
    }

    /**
     * Creates a UI that reads commands from the given scanner.
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
        this.response = null;
    }

    /**
     * Creates a UI that captures command output for display in the graphical interface.
     *
     * @param response destination for command output
     */
    public Ui(StringBuilder response) {
        this.scanner = null;
        this.response = response;
    }

    /**
     * Returns whether another command is available from the user.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Pebby's welcome banner.
     */
    public void showWelcome() {
        String banner = " ____       _     _          \n"
                + "|  _ \\  ___| |__ | |__  _   _\n"
                + "| |_) |/ _ \\ '_ \\| '_ \\| | | |\n"
                + "|  __/|  __/ |_) | |_) | |_| |\n"
                + "|_|    \\___|_.__/|_.__/ \\__, |\n"
                + "                         |___/\n";
        showSeparator();
        System.out.print(banner);
        System.out.println("Hello! I'm Pebby.");
        System.out.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Displays the separator that divides each interaction.
     */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /**
     * Displays text without adding a newline.
     */
    public void show(String message) {
        if (response == null) {
            System.out.print(message);
        } else {
            response.append(message);
        }
    }

    /**
     * Displays text followed by a newline.
     */
    public void showLine(String message) {
        if (response == null) {
            System.out.println(message);
        } else {
            response.append(message).append(System.lineSeparator());
        }
    }

    /**
     * Displays Pebby's closing message.
     */
    public void showGoodbye() {
        showSeparator();
        System.out.println("Bye Bye!");
        showSeparator();
    }
}
