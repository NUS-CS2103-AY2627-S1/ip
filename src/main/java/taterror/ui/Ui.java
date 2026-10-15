package taterror.ui;

import java.util.Scanner;

/**
 * Console interaction for the text-based CLI entry point (see
 * {@link taterror.TaTerror#main(String[])}). The JavaFX GUI has its own
 * presentation layer ({@link taterror.gui.MainWindow},
 * {@link taterror.gui.DialogBox}) and doesn't use this class.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Creates a Ui reading commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the ASCII-art banner and greeting shown once at startup.
     */
    public void showGreeting() {
        String banner = " _____ _         _____                          \n"
                + "|_   _/ \\       |_   _|__ _ __ _ __ ___  _ __   \n"
                + "  | |/ _ \\        | |/ _ \\ '__| '__/ _ \\| '__|  \n"
                + "  | / ___ \\       | |  __/ |  | | | (_) | |     \n"
                + "  |_/_/   \\_\\     |_|\\___|_|  |_|  \\___/|_|     \n";
        System.out.println(banner);
        showLine();
        System.out.println("Oh. It's you.");
        System.out.println("I'm TA Terror. Try not to waste my time.");
        showLine();
    }

    /**
     * Prints the horizontal divider used before/after each exchange.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Blocks until the user types a line and presses Enter, then returns it.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints one chatbot reply, framed by divider lines.
     */
    public void showResponse(String response) {
        showLine();
        System.out.println(response);
        showLine();
    }

    /**
     * Releases the underlying input stream. Call once, when the CLI session ends.
     */
    public void close() {
        scanner.close();
    }
}
