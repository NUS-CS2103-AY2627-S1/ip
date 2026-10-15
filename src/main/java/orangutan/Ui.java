package orangutan;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import orangutan.command.Parser;

/**
 * Handles user interface and user interactions for CLI.
 */
class Ui {
    private static final String LINE = "___________________________________________________________";
    // ascii banner adapted from https://ascii.co.uk/art/orangutan
    private static final String BANNER = "  ___                             | |             \n"
            + " / _ \\ ____ ____ ____   ____ _   _| |_ ____ ____  \n"
            + "| . . |  __/ _  |  _ \\ / _  | | | | __/ _  |  _ \\ \n"
            + "| (_) | |  |(_| | | | | (_| | |_| | | |(_| | | | |\n"
            + " \\___/|_|  \\____|_| |_|\\__  |\\____|\\__\\____|_| |_|\n"
            + "                        __/ |                     \n"
            + "                       |___/                      \n";

    private final PrintStream out;
    private final Scanner in;

    /**
     * Initializes a new UI.
     *
     * @param out Output stream.
     * @param in Input stream.
     */
    Ui(PrintStream out, InputStream in) {
        this.out = out;
        this.in = new Scanner(in);
    }

    /**
     * Prints the welcome message and initializes list from file.
     */
    void welcome(Parser parser) {
        out.println(String.join("\n", LINE, BANNER));
        out.println(parser.runCommand("init")); // sets context.isRunLoop to true if successful
    }

    /**
     * Receives input from user and prints the reply.
     *
     * @param parser Parser object, for parsing the command.
     */
    void getInput(Parser parser) {
        out.print(" > ");
        String input = in.nextLine();
        String output = parser.runCommand(input);

        out.println(LINE);
        out.println(output);
        out.println(LINE);
    }
}
