package dobby.ui.cli;

import java.util.Scanner;

/** Handles console input, output, and console-specific presentation text. */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "       *       .       *       .       *       \n"
            + "   .      ____        _     _              .   \n"
            + "     *   |  _ \\  ___ | |__ | |__  _   _     * \n"
            + "   .     | | | |/ _ \\| '_ \\| '_ \\| | | |   . \n"
            + "     *   | |_| | (_) | |_) | |_) | |_| |     * \n"
            + "   .     |____/ \\___/|_.__/|_.__/ \\__, |   . \n"
            + "                                  |___/        \n"
            + "       *       .       *       .       *       \n";

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Dobby's welcome message. */
    public void showWelcome() {
        showLine();
        showMessage(BANNER);
        showMessage("> Dobby says hi!");
        showMessage("> Dobby is ready to take orders.");
        showLine();
    }

    /** Displays the command prompt. */
    public void showPrompt() {
        System.out.print("Tell Dobby: ");
    }

    /** Reads the next command, or returns {@code null} after input ends. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays the standard divider line. */
    public void showLine() {
        showMessage(LINE);
    }

    /** Displays Dobby's farewell message. */
    public void showGoodbye() {
        showLine();
        showMessage("> Dobby says goodbye to master!");
        showLine();
    }

    /** Displays a message when it contains content. */
    public void showMessage(String message) {
        if (!message.isEmpty()) {
            System.out.println(message);
        }
    }
}
