package sevensix;

import java.util.Scanner;

/** Reads console commands and prints response blocks without owning command behavior. */
public final class ConsoleUi {
    /** Separates the chatbot's greeting, responses, and prompts. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** Prevents instantiation of the console helpers. */
    private ConsoleUi() {
    }

    /** Prints the greeting shown when the console application starts. */
    public static void printGreeting() {
        printResponse(Ui.getGreeting());
    }

    /** Prints the greeting and, when needed, a separate storage warning.
     *
     * @param warning the storage warning, or an empty string for healthy storage.
     */
    public static void printGreeting(String warning) {
        printGreeting();
        if (!warning.isEmpty()) {
            printResponse(Ui.formatError(warning));
        }
    }

    /**
     * Processes console input until the input ends or the user says goodbye.
     *
     * @param chatbot the command processor used for each input line.
     */
    public static void runConsoleLoop(SevenSix chatbot) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            printCommandResponse(chatbot, command);
            if (Parser.isExitCommand(command)) {
                return;
            }
        }
    }

    /**
     * Prints one command response with the console separators.
     *
     * @param chatbot the command processor used to produce the response.
     * @param command the command entered by the user.
     */
    private static void printCommandResponse(SevenSix chatbot, String command) {
        printResponse(chatbot.getResponse(command));
    }

    /** Prints one complete console block using the shared separators.
     *
     * @param response the message to display.
     */
    private static void printResponse(String response) {
        System.out.println(SEPARATOR);
        System.out.println(response);
        System.out.println(SEPARATOR);
    }

}
