package murphy.parser;

/** Converts a raw user input line into a command and its argument. */
public class Parser {
    /** The command types understood by Murphy. */
    public enum Command {
        BYE, ON, LIST, DELETE, MARK, UNMARK, TODO, DEADLINE, EVENT, FIND, UNKNOWN
    }

    /** The result of parsing one input line. */
    /** Holds the command recognized from one input line and its text arguments. */
    public record ParsedCommand(Command command, String argument, String originalText) { }

    /** Parses a raw command without validating command-specific arguments. */
    public ParsedCommand parse(String input) {
        String trimmed = input.trim();
        String lowerCase = trimmed.toLowerCase();
        if (trimmed.equalsIgnoreCase("bye")) {
            return new ParsedCommand(Command.BYE, "", trimmed);
        }
        if (startsWithCommand(lowerCase, "on")) {
            return withArgument(Command.ON, trimmed, "on ".length());
        }
        if (trimmed.equalsIgnoreCase("list")) {
            return new ParsedCommand(Command.LIST, "", trimmed);
        }
        if (startsWithCommand(lowerCase, "delete")) {
            return withArgument(Command.DELETE, trimmed, "delete ".length());
        }
        if (startsWithCommand(lowerCase, "mark")) {
            return withArgument(Command.MARK, trimmed, "mark ".length());
        }
        if (startsWithCommand(lowerCase, "unmark")) {
            return withArgument(Command.UNMARK, trimmed, "unmark ".length());
        }
        if (lowerCase.equals("todo") || startsWithCommand(lowerCase, "todo")) {
            return withArgument(Command.TODO, trimmed, "todo".length());
        }
        if (startsWithCommand(lowerCase, "deadline")) {
            return withArgument(Command.DEADLINE, trimmed, "deadline ".length());
        }
        if (startsWithCommand(lowerCase, "event")) {
            return withArgument(Command.EVENT, trimmed, "event ".length());
        }
        if (startsWithCommand(lowerCase, "find")) {
            return withArgument(Command.FIND, trimmed, "find ".length());
        }
        return new ParsedCommand(Command.UNKNOWN, "", trimmed);
    }

    /** Creates a parsed command whose argument starts after the command word. */
    private ParsedCommand withArgument(Command command, String input, int commandLength) {
        return new ParsedCommand(command, input.substring(commandLength).trim(), input);
    }

    /** Returns whether the input is the command itself or starts with its separator. */
    private boolean startsWithCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }
}
