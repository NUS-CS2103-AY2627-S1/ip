package bot67.ui;

import java.io.PrintStream;

/**
 * Handles output for Bot67's command-line interface.
 */
public class Ui {
    public static final String WELCOME = "Hello! I'm Bot67. Six seven! Your tasks, my favourite topic after 67.";
    public static final String GOODBYE = "Bye. Hope to see you again soon. Six Seven!";
    private static final String SEPARATOR = "____________________________________________________________";
    private final PrintStream output;

    /** Creates a UI that writes to the console. */
    public Ui() {
        this(System.out);
    }

    /** Creates a UI that writes to the supplied stream. */
    public Ui(PrintStream output) {
        this.output = output;
    }

    /** Displays the startup greeting. */
    public void showWelcome(String banner) {
        output.println(banner);
        showSeparator();
        output.println(WELCOME);
        output.println("Let's take it one task at a time. Try: todo read a book");
        showSeparator();
    }

    /** Displays the goodbye message and personality art. */
    public void showGoodbye(String personalityArt) {
        output.println("67676767676767676767676767676767676767");
        output.println(personalityArt);
        output.println(GOODBYE);
        showSeparator();
    }

    /** Displays a separator between UI interactions. */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /** Displays an error message using Bot67's standard prefix. */
    public void showError(String message) {
        output.println("SIX SEVEN! " + message);
        output.println("No worries. Give it another go - I've got you. 67!");
    }

    /** Displays one or more lines of normal application output in the supplied order. */
    public void showLine(String... messages) {
        for (String message : messages) {
            output.println(message);
        }
    }
}
