package chris;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/** Handles all interaction with the user. */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner;
    private final PrintStream output;
    private final boolean usesSeparators;

    /** Creates a UI connected to the console. */
    public Ui() {
        this(System.in, System.out, true);
    }

    /** Creates a UI connected to the given streams. */
    Ui(InputStream input, PrintStream output, boolean usesSeparators) {
        scanner = new Scanner(input);
        this.output = output;
        this.usesSeparators = usesSeparators;
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays a message between separator lines. */
    public void showMessage(String... lines) {
        showSeparator();
        for (String line : lines) {
            output.println(line);
        }
        showSeparator();
    }

    /** Displays the numbered task list. */
    public void showTasks(List<Task> tasks) {
        showSeparator();
        output.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
        showSeparator();
    }

    private void showSeparator() {
        if (usesSeparators) {
            output.println(SEPARATOR);
        }
    }
}
