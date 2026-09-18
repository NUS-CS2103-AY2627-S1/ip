package nimbus.parser;

/** Identifies the operation requested by a user command. */
public enum CommandType {
    LIST, MARK, UNMARK, DELETE, UPDATE, TODO, DEADLINE, EVENT, FIND, BYE, UNKNOWN;

    /** Returns the command type identified by the first word of the input. */
    public static CommandType from(String command) {
        String keyword = command.split("\\s+", 2)[0];
        try {
            return CommandType.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
