package chris;

/**
 * Identifies the command requested by the user.
 */
public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    TODO,
    DEADLINE,
    EVENT,
    DELETE,
    FIND,
    HELP,
    UNKNOWN;

    /**
     * Determines the command type from the first word of an input line.
     *
     * @param input complete input entered by the user
     * @return matching command type, or {@link #UNKNOWN} when there is no match
     */
    public static CommandType from(String input) {
        String commandWord = input.strip().split("\\s+", 2)[0];
        return switch (commandWord) {
        case "bye" -> BYE;
        case "list" -> LIST;
        case "mark" -> MARK;
        case "unmark" -> UNMARK;
        case "todo" -> TODO;
        case "deadline" -> DEADLINE;
        case "event" -> EVENT;
        case "delete" -> DELETE;
        case "find" -> FIND;
        case "help" -> HELP;
        default -> UNKNOWN;
        };
    }
}
