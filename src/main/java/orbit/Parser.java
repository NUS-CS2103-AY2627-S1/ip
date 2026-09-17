package orbit;

/**
 * Interprets user input as an Orbit command.
 */
public class Parser {
    /** Creates a command parser. */
    public Parser() {
    }

    /**
     * Identifies the command in a complete user input.
     *
     * @param input Complete user input.
     * @return Identified command type.
     */
    public CommandType parse(String input) {
        return CommandType.from(input);
    }
}
