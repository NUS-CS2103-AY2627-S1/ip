package laby.command;

/** Lists the commands understood by the application. */
public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    DELETE_TASK,
    TODO,
    DEADLINE,
    EVENT,
    FIND,
    CONTACT,
    DELETE_CONTACT,
    UNKNOWN;

    /**
     * Converts a command word to its type, or {@link #UNKNOWN} if unsupported.
     *
     * @param commandWord User-entered command word.
     * @return Matching command type.
     */
    public static CommandType from(String commandWord) {
        if (commandWord == null) {
            return UNKNOWN;
        }
        return switch (commandWord) {
            case "bye" -> BYE;
            case "list" -> LIST;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "deletetask" -> DELETE_TASK;
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            case "find" -> FIND;
            case "contact" -> CONTACT;
            case "deletecontact" -> DELETE_CONTACT;
            default -> UNKNOWN;
        };
    }
}
