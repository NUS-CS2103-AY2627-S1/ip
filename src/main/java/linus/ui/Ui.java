package linus.ui;

import java.util.Scanner;

/**
 * Represents the command line user interface of the Linus chatbot.
 * Responsible for printing output to the user interface.
 */
public class Ui {
    /** Horizontal line divider. */
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    /** Custom banner for the Linus chatbot. */
    private static final String BANNER = " _     _                 \n"
            + "| |   (_)_ __  _   _ ___ \n"
            + "| |   | | '_ \\| | | / __|\n"
            + "| |___| | | | | |_| \\__ \\\n"
            + "|_____|_|_| |_|\\__,_|___/\n";
    /** Standard welcome message. */
    private static final String HELLO_MESSAGE = "Hello! My name is Linus.\nWhat tech tips would you like today?";
    /** Standard goodbye message. */
    private static final String BYE_MESSAGE = "Bye! Hope to see you again soon for more tech tips!";
    /** Scanner to read user input from the command line. */
    private final Scanner scanner;

    /**
     * Initialises both a new Ui for the chatbot and the scanner to receive the user input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next line of user input using the scanner.
     *
     * @return Input text from command line entered by user.
     */
    public String read() {
        return this.scanner.nextLine();
    }

    /**
     * Prints the default welcome message when the Linus chatbot starts.
     */
    public static void sayHello() {
        Ui.display(Ui.BANNER + "\n" + Ui.HELLO_MESSAGE);
    }

    /**
     * Returns the standard welcome message for the GUI to display when the Linus chatbot starts.
     *
     * @return Standard welcome message of the chatbot.
     */
    public static String getHelloMessage() {
        return Ui.HELLO_MESSAGE;
    }

    /**
     * Prints the default goodbye message when the user exits.
     */
    public static void sayBye() {
        Ui.display(Ui.BYE_MESSAGE);
    }

    /**
     * Prints the input text to the command line.
     * Formats input text with horizontal lines above and below for spacing.
     *
     * @param text Text to be printed to the screen.
     */
    public static void display(String text) {
        System.out.println(Ui.HORIZONTAL_LINE);
        System.out.println(text);
        System.out.println(Ui.HORIZONTAL_LINE);
    }
}
