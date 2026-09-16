package zucc.ui;

import java.util.Scanner;

/**
 * Handles terminal input and output for the Zucc command-line interface.
 */
public final class TextUi extends Ui implements AutoCloseable {
    /** A visual separator used to frame the chatbot's messages. */
    private static final String SEPARATOR =
            "____________________________________________________________";

    /** The number of spaces before each separator. */
    private static final int OUTPUT_SEPARATOR_INDENT = 4;

    /** Messages are indented one space farther than their separators. */
    private static final int OUTPUT_MESSAGE_INDENT = OUTPUT_SEPARATOR_INDENT + 1;

    /** Text logo shown before the shared greeting in a terminal session. */
    private static final String LOGO = " ______                \n"
            + "|___  /                \n"
            + "   / / _   _  ___ ___  \n"
            + "  / / | | | |/ __/ __| \n"
            + " / /__| |_| | (_| (__  \n"
            + "/_____|\\__,_|\\___\\___|";

    /** Source from which user commands are read. */
    private final Scanner scanner;

    /**
     * Creates a terminal UI connected to standard input.
     */
    public TextUi() {
        scanner = new Scanner(System.in);
    }

    /**
     * Reports whether another command is available from the user.
     *
     * @return {@code true} if another input line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line entered by the user.
     *
     * @return raw command text.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows the text logo followed by the shared welcome message.
     */
    @Override
    public void showGreeting() {
        showMessage(LOGO + "\n" + GREETING);
    }

    /**
     * Prints a response between separators using consistent indentation.
     *
     * @param message response to print.
     */
    @Override
    public void showMessage(String message) {
        System.out.print(SEPARATOR.indent(OUTPUT_SEPARATOR_INDENT));
        System.out.print(message.indent(OUTPUT_MESSAGE_INDENT));
        System.out.print(SEPARATOR.indent(OUTPUT_SEPARATOR_INDENT));
    }

    @Override
    public void showError(String message) {
        showMessage(message);
    }

    /**
     * Releases the input scanner when the application finishes.
     */
    @Override
    public void close() {
        scanner.close();
    }
}
