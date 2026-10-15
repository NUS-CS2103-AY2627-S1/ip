package zeus.ui;

import java.util.List;
import java.util.Scanner;

/** Handles command input and console-specific output boundaries. */
public class ConsoleUi extends Ui {
    /** Visual boundary used between commands and responses. */
    private static final String SEPARATOR =
            "____________________________________________________________";

    /** Name banner displayed when Zeus starts. */
    private static final String BANNER = " _____\n"
            + "|__  /___ _   _ ___\n"
            + "  / // _ \\ | | / __|\n"
            + " / /|  __/ |_| \\__ \\\n"
            + "/____\\___|\\__,_|___/";

    /** Source of commands entered by the user. */
    private final Scanner scanner;

    /** Creates a console UI that reads from standard input and writes to standard output. */
    public ConsoleUi() {
        scanner = new Scanner(System.in);
    }

    /** Displays Zeus's banner and greeting. */
    public void showWelcome() {
        showLines(
                SEPARATOR,
                BANNER,
                "Zeus awaits your command. ⚡",
                "Type 'help' to see available commands.",
                SEPARATOR);
    }

    /**
     * Reports whether another command is available.
     *
     * @return True when another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command and displays the response boundary.
     *
     * @return Trimmed command entered by the user.
     */
    public String readCommand() {
        String command = scanner.nextLine().trim();
        showLines(SEPARATOR);
        return command;
    }

    /**
     * Displays recoverable problems found while loading saved tasks.
     *
     * @param warnings Loading warning messages.
     */
    public void showLoadingWarnings(List<String> warnings) {
        for (String warning : warnings) {
            showError(warning);
        }
        if (!warnings.isEmpty()) {
            showLines(SEPARATOR);
        }
    }

    /** Displays Zeus's farewell followed by the final console boundary. */
    @Override
    public void showGoodbye() {
        super.showGoodbye();
        showLines(SEPARATOR);
    }

    /** Closes the response boundary for a completed command. */
    public void showResponseEnd() {
        showLines(SEPARATOR);
    }
}
