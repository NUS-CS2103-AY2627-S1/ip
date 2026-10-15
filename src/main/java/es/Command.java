package es;

/**
 * User commands recognised by Es.
 */
public enum Command {
    BYE,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    FIND,
    TAG,
    UNTAG,
    FINDTAG;

    /**
     * Identifies the command word at the start of a trimmed input line.
     * Matching is case-sensitive and uses the enum constant name in lowercase,
     * so {@code mark 1} matches {@code MARK} but {@code MARK 1} does not.
     *
     * @param input the trimmed line entered by the user
     * @return the matching command, or {@code null} if the line is not a known command
     */
    public static Command parse(String input) {
        for (Command command : values()) {
            String keyword = command.name().toLowerCase();
            if (input.equals(keyword) || input.startsWith(keyword + " ")) {
                return command;
            }
        }
        return null;
    }
}
