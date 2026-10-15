package pebby.parser;

/**
 * Converts a raw line of user input into a command type and argument.
 */
public class Parser {
    /**
     * Identifies the command word and separates it from the user-supplied argument.
     * Unknown commands have an empty argument because Pebby only displays an error for them.
     */
    public static ParsedCommand parse(String fullCommand) {
        if (fullCommand == null || fullCommand.isBlank()) {
            return invalid("Please enter a command.");
        }
        String trimmedCommand = fullCommand.trim();
        if (trimmedCommand.contains("  ") || trimmedCommand.chars()
                .anyMatch(character -> Character.isWhitespace(character) && character != ' ')) {
            return invalid("Use single spaces between command parts.");
        }

        CommandType type = CommandType.from(trimmedCommand);
        String argument = type == CommandType.UNKNOWN ? "" : type.argumentFrom(trimmedCommand);
        if (type == CommandType.UNKNOWN) {
            return invalid("Unknown command. Type help to see the available commands.");
        }
        if (doesNotTakeArguments(type) && !argument.isEmpty()) {
            return invalid("The " + trimmedCommand.substring(0, trimmedCommand.indexOf(' '))
                    + " command does not take an argument.");
        }
        return new ParsedCommand(type, argument);
    }

    /** Returns an invalid parsed command with its user-facing explanation. */
    private static ParsedCommand invalid(String errorMessage) {
        return new ParsedCommand(CommandType.UNKNOWN, "", errorMessage);
    }

    /** Returns whether the given command must consist only of its command word. */
    private static boolean doesNotTakeArguments(CommandType type) {
        return type == CommandType.LIST || type == CommandType.HELP || type == CommandType.BYE;
    }
}
