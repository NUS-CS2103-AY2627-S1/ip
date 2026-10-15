package jeryl.parser;

/**
 * Deals with making sense of raw user input: splitting a line into a
 * command word and the remaining arguments.
 */
public class Parser {
    /**
     * A line of user input split into its command word (the first
     * whitespace-separated token) and everything after it.
     */
    public record ParsedInput(Command command, String arguments) {
    }

    /**
     * Splits one line of raw user input into a Command and the raw
     * argument text that follows it.
     */
    public static ParsedInput parse(String input) {
        String trimmed = input.strip();
        int spaceIndex = trimmed.indexOf(' ');
        String keyword = spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex);
        String arguments = spaceIndex == -1 ? "" : trimmed.substring(spaceIndex + 1);
        Command command = Command.fromKeyword(keyword);
        assert command != null : "Command.fromKeyword should always return a command, falling back to UNKNOWN";
        return new ParsedInput(command, arguments);
    }
}
