package es;

/** Converts raw user input into a recognised command. */
public class Parser {
    private Parser() {
    }

    /**
     * Parses a trimmed input line.
     *
     * @param input the user input
     * @return the matching command, or null when unknown
     */
    public static Command parse(String input) {
        return Command.parse(input);
    }

    /** Returns a command action for lifecycle commands such as {@code bye}. */
    public static CommandAction parseAction(String input) {
        return parse(input) == Command.BYE ? new ExitCommand() : null;
    }
}
