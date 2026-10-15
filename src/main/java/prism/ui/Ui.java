package prism.ui;

import java.util.Scanner;

/** Handles console input and output for the Prism application. */
public class Ui {
    private static final String LINE = "____________________________________________________________\n";
    private static final String LOGO =
            " ____       _               \n"
                    + "|  _ \\ _ __(_)___ _ __ ___  \n"
                    + "| |_) | '__| / __| '_ ` _ \\ \n"
                    + "|  __/| |  | \\__ \\ | | | | |\n"
                    + "|_|   |_|  |_|___/_| |_| |_|\n";

    private final Scanner scanner;
    private final boolean isConsole;
    private final StringBuilder messages;

    /** Creates a UI connected to standard input. */
    public Ui() {
        this(true);
    }

    /** Creates a UI configured for console output or programmatic output. */
    public Ui(boolean isConsole) {
        this.scanner = new Scanner(System.in);
        this.isConsole = isConsole;
        this.messages = new StringBuilder();
    }

    /** Reads the next complete command from the user. */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /** Displays the application logo and welcome message. */
    public void showWelcome() {
        showLine();
        System.out.println(LOGO + "Hello! I'm Prism, your quietly enthusiastic task companion.\n"
                + "Give me a command and we'll bring your day into focus.");
        showLine();
    }

    /** Displays a separator line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Displays an error message. */
    public void showError(String message) {
        showMessage(message);
    }

    /** Displays a general application message. */
    public void showMessage(String message) {
        if (this.isConsole) {
            System.out.println(message);
        } else {
            this.messages.append(message).append(System.lineSeparator());
        }
    }

    /** Displays the exit message. */
    public void showBye() {
        showLine();
        System.out.println("Until next time—keep your day in focus!");
        showLine();
    }

    /** Closes the input scanner. */
    public void close() {
        this.scanner.close();
    }

    /** Returns and clears messages accumulated for a non-console UI. */
    public String collectMessages() {
        String output = this.messages.toString();
        this.messages.setLength(0);
        return output;
    }
}
