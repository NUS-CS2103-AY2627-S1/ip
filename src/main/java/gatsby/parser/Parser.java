package gatsby.parser;

import java.util.Locale;

import gatsby.command.CommandType;

/**
 * Converts a raw console line into a command and its payload.
 *
 * The parser recognizes commands case-insensitively and preserves the payload's
 * original content so descriptions and dates are handled exactly as entered.
 */
public class Parser {
    /** Creates a parser for Gatsby command lines. */
    public Parser() {
    }

    /**
     * Parses one line of user input.
     *
     * @param input the raw line entered by the user
     * @return the recognized command and the text after it
     */
    public ParsedCommand parse(String input) {
        if (input == null) {
            return new ParsedCommand(CommandType.UNKNOWN, "");
        }
        String trimmedInput = input.strip();
        if (trimmedInput.isEmpty()) {
            return new ParsedCommand(CommandType.UNKNOWN, "");
        }
        String[] commandParts = trimmedInput.split("\\s+", 2);
        String payload = commandParts.length > 1 ? commandParts[1].strip() : "";
        return new ParsedCommand(identifyCommand(trimmedInput), payload);
    }

    /**
     * Identifies a command from a complete input line.
     *
     * Goodbye and list are whole-line commands. The remaining commands are
     * recognized from the first word so they can be followed by a payload.
     *
     * @param input the trimmed input line
     * @return the matching command type, or {@link CommandType#UNKNOWN}
     */
    private CommandType identifyCommand(String input) {
        String normalizedInput = input.toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        if (CommandType.BYE.matchesAlias(normalizedInput)) {
            return CommandType.BYE;
        }
        if (CommandType.LIST.matchesAlias(normalizedInput)) {
            return CommandType.LIST;
        }
        if (CommandType.HELP.matchesAlias(normalizedInput)) {
            return CommandType.HELP;
        }

        String action = normalizedInput.split("\\s+", 2)[0];
        for (CommandType command : CommandType.values()) {
            if (command != CommandType.BYE && command != CommandType.LIST
                    && command != CommandType.HELP
                    && command.matchesAlias(action)) {
                return command;
            }
        }

        return CommandType.UNKNOWN;
    }

    /**
     * Holds the result of parsing one input line.
     */
    public static class ParsedCommand {
        /** The command recognized from the input. */
        private final CommandType command;

        /** The text after the command, or an empty string when there is none. */
        private final String payload;

        /**
         * Creates a parsed command result.
         *
         * @param command the recognized command
         * @param payload the text after the command
         */
        public ParsedCommand(CommandType command, String payload) {
            if (command == null || payload == null) {
                throw new IllegalArgumentException("A parsed command must have command and payload values.");
            }
            this.command = command;
            this.payload = payload;
        }

        /**
         * Returns the recognized command.
         *
         * @return the command type
         */
        public CommandType getCommand() {
            return command;
        }

        /**
         * Returns the text after the command.
         *
         * @return the command payload
         */
        public String getPayload() {
            return payload;
        }
    }
}
