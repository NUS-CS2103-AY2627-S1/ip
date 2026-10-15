package peter;

/**
 * Lists the commands understood by Peter.
 */
public enum Command {
    BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, SCHEDULE, FIND, UNKNOWN;

    /**
     * Converts a command keyword into its matching command value.
     *
     * @param keyword the command keyword entered by the user
     * @return the matching command, or {@link #UNKNOWN} if there is no match
     */
    public static Command fromKeyword(String keyword) {
        assert keyword != null : "command keyword should not be null";
        try {
            return Command.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
