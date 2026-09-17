package mouse.parser;

/**
 * User commands Mouse understands.
 */
public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    FIND,
    HELP,
    PRIORITY,
    UNKNOWN;

    /**
     * Identifies the command from a raw input line.
     *
     * @param input Raw user input.
     * @return Matching command type, or {@link #UNKNOWN} if none match.
     */
    public static CommandType fromInput(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return UNKNOWN;
        }
        String command = trimmed.split("\\s+", 2)[0].toLowerCase();
        switch (command) {
        case "bye":
            return BYE;
        case "list":
            return LIST;
        case "mark":
            return MARK;
        case "unmark":
            return UNMARK;
        case "delete":
            return DELETE;
        case "todo":
            return TODO;
        case "deadline":
            return DEADLINE;
        case "event":
            return EVENT;
        case "find":
            return FIND;
        case "help":
            return HELP;
        case "priority":
            return PRIORITY;
        default:
            return UNKNOWN;
        }
    }
}
