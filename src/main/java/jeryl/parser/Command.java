package jeryl.parser;

/**
 * The set of commands Jeryl understands. The keyword is the first
 * whitespace-separated word of the user's input.
 */
public enum Command {
    LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, BYE, UNKNOWN;

    /**
     * Maps a command keyword (case-sensitive, as typed by the user) to
     * its Command, or UNKNOWN if it doesn't match any known command.
     */
    public static Command fromKeyword(String keyword) {
        try {
            return Command.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
