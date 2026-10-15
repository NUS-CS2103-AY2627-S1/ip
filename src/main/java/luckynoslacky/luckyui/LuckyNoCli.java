package luckynoslacky.luckyui;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Handles direct command-line interaction with the user.
 */
public class LuckyNoCli {
    private static final String DIVIDER =
            "  ____________________________________________________________\n";

    private final Scanner scanner;

    /** Creates a command-line interface connected to standard input. */
    public LuckyNoCli() {
        scanner = new Scanner(System.in);
    }

    /**
     * Checks whether another command is available from standard input.
     *
     * @return true if another input line is available
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one command from standard input.
     *
     * @return the next user command
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints a user-facing reply with the chatbot's standard formatting.
     *
     * @param messageParts ordered parts of the reply, joined with newlines
     */
    public void showReply(String... messageParts) {
        String output = String.join("\n", messageParts);
        String indentedOutput = output.replace("\n", "\n  ");
        writeUtf8(DIVIDER + "  " + indentedOutput + "\n" + DIVIDER);
    }

    /** Displays the chatbot banner and greeting. */
    public void showGreeting() {
        writeUtf8(DIVIDER + LuckyNoQuips.banner() + "\n");
        showReply(LuckyNoQuips.greeting());
    }

    /** Displays the chatbot goodbye message. */
    public void showGoodbye() {
        showReply(LuckyNoQuips.goodbye());
    }

    /** Displays the message shown when task loading fails. */
    public void showLoadingError() {
        showReply(LuckyNoQuips.loadErrorMessage());
    }

    /** Displays the message shown when task saving fails. */
    public void showSavingError() {
        showReply(LuckyNoQuips.saveErrorMessage());
    }

    /** Displays the message shown when application configuration is invalid. */
    public void showConfigurationError() {
        showReply(LuckyNoQuips.configurationErrorMessage());
    }

    /**
     * Echoes input using the normal chatbot reply formatting.
     *
     * @param userInput input to echo
     */
    public void echo(String userInput) {
        showReply(userInput);
    }

    /**
     * Writes text to standard output with UTF-8 encoding.
     *
     * @param text text to write
     */
    private static void writeUtf8(String text) {
        byte[] encodedText = text.getBytes(StandardCharsets.UTF_8);
        System.out.write(encodedText, 0, encodedText.length);
    }
}
