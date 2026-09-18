package nimbus.parser;

/** Converts raw user input into recognised commands. */
public class Parser {
    /** Parses a line of user input. */
    public ParsedCommand parse(String input) {
        String fullText = input.trim();
        return new ParsedCommand(CommandType.from(fullText), fullText);
    }
}
