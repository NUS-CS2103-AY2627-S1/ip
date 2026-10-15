package kdb;

/**
 * Converts raw user input into a command and its arguments.
 */
public class Parser {
    /** Represents one parsed user command. */
    public static class ParsedCommand {
        private final CommandType command;
        private final String arguments;

        private ParsedCommand(CommandType command, String arguments) {
            this.command = command;
            this.arguments = arguments;
        }

        /** Returns the parsed command type. */
        public CommandType getCommand() {
            return command;
        }

        /** Returns the text following the command word. */
        public String getArguments() {
            return arguments;
        }
    }

    /**
     * Splits input into its command word and the remaining arguments.
     *
     * @param input raw text entered by the user
     * @return the recognized command and trimmed arguments
     */
    public ParsedCommand parse(String input) {
        if (input == null || input.isBlank()) {
            return new ParsedCommand(CommandType.UNKNOWN, "");
        }
        String[] split = input.trim().split("\\s+", 2);
        String commandWord = split[0];
        String arguments = split.length > 1 ? split[1].trim() : "";
        return new ParsedCommand(CommandType.fromWord(commandWord), arguments);
    }
}
