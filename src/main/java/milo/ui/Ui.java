package milo.ui;

import java.util.Scanner;

/** Handles Milo's console input and output. */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER =
            " __  __   _       \n"
            + "|  \\/  (_) | ___  \n"
            + "| |\\/| | | |/ _ \\ \n"
            + "| |  | | | | (_) |\n"
            + "|_|  |_|_|_|\\___/\n";
    private final Scanner scanner;

    /** Creates a console user interface backed by standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Reads the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays Milo's banner. */
    public void showWelcome() {
        System.out.println(LINE);
        System.out.println(BANNER);
    }

    /** Displays the message shown while stored tasks are loaded. */
    public void showLoading() {
        System.out.println(LINE);
        System.out.println("Retrieving old tasks...");
        System.out.println(LINE);
    }

    /** Displays Milo's greeting and prompt introduction. */
    public void showGreeting() {
        System.out.println("Hey there! My name is Milo.");
        System.out.println("How can I help you today?");
        System.out.println(LINE);
    }

    /** Displays a response surrounded by Milo's standard separator. */
    public void showResponse(String string) {
        System.out.println(LINE);
        System.out.println("    " + string);
        System.out.println(LINE);
    }

    /** Closes the console input scanner. */
    public void close() {
        scanner.close();
    }
}
