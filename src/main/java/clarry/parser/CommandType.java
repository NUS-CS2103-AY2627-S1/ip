package clarry.parser;

/**
 * Represents the commands understood by Clarry.
 */
public enum CommandType {
    HELP, BYE, LIST, ON, FIND, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, UNKNOWN;

    /**
     * Converts the first word of a user command to its command type.
     *
     * @param word first word entered by the user
     * @return corresponding command type, or {@code UNKNOWN} if it is unsupported
     */
    public static CommandType fromWord(String word) {
        switch (word) {
            case "help":
                return HELP;
            case "bye":
                return BYE;
            case "list":
                return LIST;
            case "on":
                return ON;
            case "find":
                return FIND;
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
            default:
                return UNKNOWN;
        }
    }
}
