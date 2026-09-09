package chin.util;

/**
 * Enumerates the top-level commands the Chin chatbot understands.
 * {@link #UNKNOWN} is returned for anything unrecognised.
 */
public enum Command {
    LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, SORT, BYE, UNKNOWN;

    /** Maps the first whitespace-delimited token of {@code input} to a command. */
    public static Command fromInput(String input) {
        String head = input.split(" ", 2)[0];
        try {
            return Command.valueOf(head.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
