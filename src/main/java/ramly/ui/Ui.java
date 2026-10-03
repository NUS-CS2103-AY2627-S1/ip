package ramly.ui;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;

/** Handles console input and output for Ramly. */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final InputStream input;
    private final Consumer<String> output;
    private Scanner scanner;

    /** Creates a console UI that reads from standard input and writes to standard output. */
    public Ui() {
        this(System.in, System.out::println);
    }

    /** Creates an output UI that sends each displayed message to the supplied consumer. */
    public Ui(Consumer<String> output) {
        this(System.in, output);
    }

    /** Creates a UI with injectable input and output for controlled verification. */
    Ui(InputStream input, Consumer<String> output) {
        assert input != null : "UI input stream must not be null";
        assert output != null : "UI output consumer must not be null";
        this.input = input;
        this.output = output;
    }

    /** Reads one command from the user, or returns null when the input stream ends. */
    public String readCommand() {
        if (scanner == null) {
            scanner = new Scanner(input);
        }
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays one or more messages to the user in the order supplied. */
    public void show(String... messages) {
        assert messages != null : "Displayed messages must not be null";
        for (String message : messages) {
            assert message != null : "Displayed message must not be null";
            output.accept(message);
        }
    }

    /** Displays an error message to the user. */
    public void showError(String message) {
        show(message);
    }

    /** Displays the initial banner and greeting. */
    public void showWelcome() {
        showLine();
        show(" ____  _       \n"
                + "|  _ \\(_)_ __  \n"
                + "| |_) | | '_ \\ \n"
                + "|  __/| | |_) |\n"
                + "|_|   |_| .__/ \n"
                + "        |_|    \n");
        show("Hey, I'm Pip—your pocket pathfinder.");
        show("What shall we tackle next?");
        showLine();
    }

    /** Displays a divider between interactions. */
    public void showLine() {
        show(LINE);
    }

    /** Releases the console input resource. */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
