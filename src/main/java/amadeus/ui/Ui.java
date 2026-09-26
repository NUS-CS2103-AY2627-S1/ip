package amadeus.ui;

import java.util.Scanner;

/**
 * Everything the chatbot says and everything the user types, in the console.
 * <p>
 * This class is the only place in the app that touches {@code System.in} and
 * {@code System.out}. Deciding <em>what</em> to say belongs to
 * {@link amadeus.Amadeus}, which hands back a reply as plain text; this class only
 * decides <em>how</em> that text reaches a console user. The JavaFX window in
 * {@link MainWindow} is the alternative to this class, and takes its replies from
 * exactly the same method.
 * <p>
 * Every message the bot speaks is printed with a single leading space, which is what
 * visually sets the bot's voice apart from the line the user just typed. That space is
 * added here rather than by each caller, so no caller can forget it.
 */
public class Ui {

    /** Horizontal rule used to separate the chatbot's messages from the rest of the output. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Amadeus";

    /**
     * ASCII-art banner shown on startup.
     * Each backslash is written as "\\" because a lone backslash starts an escape
     * sequence in a Java string literal.
     */
    private static final String BANNER =
              "     _     __  __      _     ____   _____  _   _  ____  \n"
            + "    / \\   |  \\/  |    / \\   |  _ \\ | ____|| | | |/ ___| \n"
            + "   / _ \\  | |\\/| |   / _ \\  | | | ||  _|  | | | |\\___ \\ \n"
            + "  / ___ \\ | |  | |  / ___ \\ | |_| || |___ | |_| | ___) |\n"
            + " /_/   \\_\\|_|  |_| /_/   \\_\\|____/ |_____| \\___/ |____/ ";

    /** Reads the user's commands. Owned by Ui so nothing else has to know about System.in. */
    private final Scanner scanner;

    /** Creates a Ui that reads the user's commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the banner and greeting shown when the app starts. */
    public void showWelcome() {
        showLine();
        System.out.println(BANNER);
        System.out.println("Hello I'm " + NAME + ", sir.");
        System.out.println("Sir what do you need assistance with");
        showLine();
    }

    /**
     * Returns true if there is another command to read.
     * <p>
     * Asked before every {@link #readCommand()} so the loop ends cleanly when the
     * input runs out before "bye", which is what happens when input is piped in
     * from a file.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Returns the next line the user typed, exactly as typed. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the horizontal rule that brackets each reply. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints a reply in the bot's voice.
     * <p>
     * The reply arrives as one string because that is the form the JavaFX window wants;
     * it is split here so that every line gets the leading space, not just the first.
     *
     * @param reply the reply text, whose lines are separated by {@code \n}.
     */
    public void showReply(String reply) {
        for (String line : reply.split("\n")) {
            System.out.println(" " + line);
        }
    }

    /** Releases the input source. Called once, when the app is about to end. */
    public void close() {
        scanner.close();
    }
}
