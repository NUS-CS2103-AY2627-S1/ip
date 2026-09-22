package oz.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oz.exception.OzException;

/**
 * Parses raw user input into its command word and argument text.
 */
public final class CommandParser {
    /** Error shown when the command structure cannot be parsed. */
    private static final String UNRECOGNIZED_INPUT_MESSAGE = "I could not understand that input.";

    /** Pattern matching one command word followed by optional arguments. */
    private static final Pattern COMMAND_PATTERN = Pattern
            .compile("^(?<command>\\S+)(?:\\s+(?<arguments>.*))?$");

    /** Prevents instantiation of this stateless parser. */
    private CommandParser() {
    }

    /**
     * Parses a non-blank user input into a command word and arguments.
     *
     * @param input Non-blank user input.
     * @return Parsed command word and arguments.
     * @throws OzException If the input does not match the command structure.
     */
    public static ParsedCommand parse(String input) throws OzException {
        assert input != null && !input.isBlank() : "Command input must be non-blank";

        Matcher commandMatcher = COMMAND_PATTERN.matcher(input.trim());
        if (!commandMatcher.matches()) {
            throw new OzException(UNRECOGNIZED_INPUT_MESSAGE);
        }

        String commandWord = commandMatcher.group("command");
        String rawArguments = commandMatcher.group("arguments");
        String arguments = rawArguments == null ? "" : rawArguments.trim();
        return new ParsedCommand(commandWord, arguments);
    }
}
