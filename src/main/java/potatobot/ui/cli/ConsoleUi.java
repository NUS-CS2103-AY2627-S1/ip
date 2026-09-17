package potatobot.ui.cli;

import java.util.Scanner;

/**
 * Handles console input and output for PotatoBot.
 */
public class ConsoleUi implements AutoCloseable {
    private static final String SEPARATOR = "=".repeat(80);
    private static final String BANNER = """
             ____       _        _        ____        _           _...._
            |  _ \\ ___ | |_ __ _| |_ ___ | __ )  ___ | |_     .-'      '-.
            | |_) / _ \\| __/ _` | __/ _ \\|  _ \\ / _ \\| __|   /  .  .   .  \\
            |  __/ (_) | || (_| | || (_) | |_) | (_) | |_    | .    .     .|
            |_|   \\___/ \\__\\__,_|\\__\\___/|____/ \\___/ \\__|    \\__ .   .    /
                                                                 '-.____.-'
            """;

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates a UI that reads from standard input.
     */
    public ConsoleUi() {
    }

    /**
     * Displays PotatoBot's welcome banner and usage hint.
     */
    public void showGreeting() {
        System.out.println(SEPARATOR);
        System.out.print(BANNER);
        System.out.println("Hello! I'm PotatoBot, your trusty spud assistant.");
        System.out.println(
                "What tasks do you want me to store for you? "
                        + "(Say \"bye\" if you want me to leave you alone)");
        System.out.println(SEPARATOR + "\n");
    }

    /**
     * Prompts for and reads the user's next command.
     *
     * @return the command, or {@code null} when standard input has ended.
     */
    public String readCommand() {
        System.out.print("Me: ");
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Displays a message inside PotatoBot's standard reply box.
     *
     * @param message Message to display.
     */
    public void showMessage(String message) {
        String indentedMessage = message.replace("\n", "\n  ");
        System.out.println("PotatoBot:\n  " + indentedMessage + "\n");
    }

    /**
     * Displays a blank line between the final command and PotatoBot's reply.
     */
    public void showBlankLine() {
        System.out.println();
    }

    /**
     * Displays PotatoBot's farewell message.
     */
    public void showFarewell() {
        System.out.println(SEPARATOR);
        System.out.println("Bye. I'm rolling back to the potato patch. Hope to see you again soon!");
        System.out.println(SEPARATOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        scanner.close();
    }
}
