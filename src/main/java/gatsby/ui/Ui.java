package gatsby.ui;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Handles Gatsby's interaction with the console.
 *
 * Keeping input and output here allows the command logic to focus on deciding
 * what Gatsby should do, while this class focuses on how Gatsby communicates it.
 */
public class Ui {
    /** The separator printed around each console interaction. */
    private static final String LINE = "____________________________________________________________";

    /** The joke included in Gatsby's welcome message. */
    private static final String START_JOKE = "When did the Japanese invent eggs? A long tamago :)";

    /** Reads commands from standard input for the current Gatsby session. */
    private final Scanner scanner;

    /** Receives complete lines printed by this UI. */
    private final Consumer<String> lineOutput;

    /** Receives text printed without an automatic newline. */
    private final Consumer<String> output;

    /** Creates a console UI connected to standard input. */
    public Ui() {
        this(new Scanner(System.in), System.out::println, System.out::print);
    }

    /**
     * Creates a UI that sends its output to the supplied line consumer.
     *
     * This constructor lets non-console front ends reuse Gatsby's command logic
     * without having to parse console output from standard output.
     *
     * @param lineOutput the destination for complete output lines
     */
    public Ui(Consumer<String> lineOutput) {
        this(new Scanner(InputStream.nullInputStream()), lineOutput, lineOutput);
    }

    /** Creates a UI with explicit input and output collaborators. */
    private Ui(Scanner scanner, Consumer<String> lineOutput, Consumer<String> output) {
        this.scanner = scanner;
        this.lineOutput = lineOutput;
        this.output = output;
    }

    /**
     * Returns the joke included in Gatsby's welcome message.
     *
     * @return Gatsby's starting joke
     */
    public static String getStartingJoke() {
        return START_JOKE;
    }

    /** Prints Gatsby's banner and welcome message. */
    public void showWelcome() {
        String banner = "************************************\n"
                + "*              Gatsby              *\n"
                + "************************************\n";
        print(banner);
        String welcome = "Wassup! I'm Gatsby.\n"
                + START_JOKE + "\n"
                + "What can I do for you?\n";
        print(welcome);
    }

    /**
     * Returns whether another command is available on standard input.
     *
     * @return true when another input line can be read
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line.
     *
     * @return the raw line entered by the user
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /** Prints the separator used around each command interaction. */
    public void showSeparator() {
        printLine(LINE);
    }

    /**
     * Prints a message followed by a newline.
     *
     * @param message the message to display
     */
    public void printLine(String message) {
        lineOutput.accept(message);
    }

    /**
     * Prints a message without adding a newline.
     *
     * @param message the message to display
     */
    public void print(String message) {
        output.accept(message);
    }

    /** Closes the input reader at the end of the session. */
    public void close() {
        scanner.close();
    }
}
