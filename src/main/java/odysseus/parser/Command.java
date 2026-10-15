package odysseus.parser;

/** Represents the commands that the user can use. */
public enum Command {
    TODO, DEADLINE, EVENT, LIST, MARK, UNMARK, DELETE, BYE, ON, FIND, UPDATE, UNKNOWN;

    /**
     * Maps a raw command word to its Command, case-insensitively.
     *
     * @param word the first token of user input
     * @return the matching Command, or UNKNOWN if none match
     */
    public static Command fromInput(String word) {
        return switch (word.toLowerCase()) {
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            case "list" -> LIST;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "delete" -> DELETE;
            case "bye" -> BYE;
            case "on" -> ON;
            case "find" -> FIND;
            case "update" -> UPDATE;
            default -> UNKNOWN;
        };
    }
}
