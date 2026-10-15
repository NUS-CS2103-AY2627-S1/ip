package bot.ui;

import java.util.Scanner;

/**
 * Handles console input and output for the chatbot.
 */
public class Ui {
    private static final String LINE = "_".repeat(60);

    private final Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the chatbot banner and greeting.
     */
    public void showWelcome() {
        String banner = """
                         ____        _
                        | __ )  ___ | |_
                        |  _ \\ / _ \\| __|
                        | |_) | (_) | |_
                        |____/ \\___/ \\__|
                        """;
        System.out.print(banner);
        System.out.println("Yo! I'm Bot.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return {@code true} if another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command entered by the user.
     *
     * @return next command
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays the indented separator surrounding a command response.
     */
    public void showCommandSeparator() {
        System.out.println("    " + LINE);
    }

    /**
     * Displays the farewell message.
     */
    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println(ResponseFormatter.formatGoodbye());
        System.out.println(LINE);
    }

    /**
     * Displays a response with the indentation used by the console interface.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        response.lines().forEach(line -> System.out.println("    " + line));
    }
}
