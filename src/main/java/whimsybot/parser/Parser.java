package whimsybot.parser;

import java.util.Locale;

/** Parses the command word and arguments from a line entered by the user. */
public class Parser {
    private Parser() {
        // Utility class.
    }

    /**
     * Splits a full command into its command word and trimmed arguments.
     *
     * @param command the complete line entered by the user
     * @return an array containing the command word followed by its arguments
     */
    public static String[] parse(String command) {
        if (command == null || command.isBlank()) {
            return new String[] {"", ""};
        }
        String[] splitCommand = command.trim().split("\\s+", 2);
        String commandWord = splitCommand[0].toLowerCase(Locale.ROOT);
        String arguments = splitCommand.length > 1 ? splitCommand[1].trim() : "";
        return new String[] {commandWord, arguments};
    }
}
