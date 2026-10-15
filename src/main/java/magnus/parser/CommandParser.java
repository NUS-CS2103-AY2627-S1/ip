package magnus.parser;

/**
 * Parses raw user input into a command and its arguments.
 * The first whitespace-separated token is treated as the command keyword. All
 * remaining text is preserved as one argument for command-specific validation.
 */
public class CommandParser {
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final int COMMAND_AND_ARGUMENTS_SPLIT_LIMIT = 2;
    private static final int COMMAND_ONLY_PART_COUNT = 1;

    /**
     * Creates a parser for command-line input.
     */
    public CommandParser() {
    }

    /**
     * Splits user input into a command followed by its argument fields. The first
     * whitespace-separated word is the command, and {@code /by}, {@code /from},
     * and all remaining text is returned as one argument.
     *
     * @param userInput The user input to split.
     * @return The tokens in the input, or an empty array if the input is blank.
     */
    public String[] parse(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return new String[0];
        }

        String[] commandAndArguments = userInput.strip()
                .split(WHITESPACE_REGEX, COMMAND_AND_ARGUMENTS_SPLIT_LIMIT);
        if (commandAndArguments.length == COMMAND_ONLY_PART_COUNT) {
            return commandAndArguments;
        }
        return commandAndArguments;
    }
}
