package nori.parser;

/**
 * Identifies the operation requested by the first word of a user command.
 */
public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    FIND,
    HELP,
    TODO,
    DEADLINE,
    EVENT,
    EMPTY,
    UNKNOWN;

    /**
     * Determines a command type from trimmed user input.
     *
     * @param command trimmed command entered by the user.
     * @return matching command type, or {@link #UNKNOWN}
     */
    public static CommandType from(String command) {
        if (command.isEmpty()) {
            return EMPTY;
        }
        String commandWord = command.split("\\s+", 2)[0];
        return switch (commandWord) {
            case "bye" -> command.equals("bye") ? BYE : UNKNOWN;
            case "list" -> command.equals("list") ? LIST : UNKNOWN;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "delete" -> DELETE;
            case "find" -> FIND;
            case "help" -> command.equals("help") ? HELP : UNKNOWN;
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            default -> UNKNOWN;
        };
    }
}
