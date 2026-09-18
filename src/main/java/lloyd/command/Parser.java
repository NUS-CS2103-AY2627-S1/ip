package lloyd.command;

/**
 * Interprets raw user input as a command type and its argument text.
 */
public class Parser {

    /** Creates a parser for Lloyd's supported console commands. */
    public Parser() {
        // This class currently needs no configuration.
    }

    /**
     * Parses one line entered by the user.
     *
     * @param input Raw user input.
     * @return Recognized command and its arguments.
     */
    public ParsedCommand parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        String[] commandParts = input.trim().split("\\s+", 2);
        CommandType commandType = CommandType.from(commandParts[0]);
        String arguments = commandParts.length < 2 ? "" : commandParts[1];
        return new ParsedCommand(commandType, arguments);
    }
}
