package alzara.parser;

/**
 * Represents the commands Alzara recognises.
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
    VIEW,
    UNKNOWN;

    /**
     * Identifies a command using Alzara's existing command-recognition rules.
     * The command word is matched case-insensitively - {@code "Todo"} and
     * {@code "TODO"} are recognised the same as {@code "todo"}.
     *
     * @param command command entered by the user
     * @return type of the command, or {@code UNKNOWN} when it is not recognised
     */
    public static CommandType from(String command) {
        String lowerCaseCommand = command.toLowerCase();
        if (lowerCaseCommand.equals("bye")) {
            return BYE;
        } else if (lowerCaseCommand.equals("mark") || lowerCaseCommand.startsWith("mark ")) {
            return MARK;
        } else if (lowerCaseCommand.equals("unmark") || lowerCaseCommand.startsWith("unmark ")) {
            return UNMARK;
        } else if (lowerCaseCommand.equals("todo") || lowerCaseCommand.startsWith("todo ")) {
            return TODO;
        } else if (lowerCaseCommand.equals("deadline") || lowerCaseCommand.startsWith("deadline ")) {
            return DEADLINE;
        } else if (lowerCaseCommand.equals("event") || lowerCaseCommand.startsWith("event ")) {
            return EVENT;
        } else if (lowerCaseCommand.equals("delete") || lowerCaseCommand.startsWith("delete ")) {
            return DELETE;
        } else if (lowerCaseCommand.equals("find") || lowerCaseCommand.startsWith("find ")) {
            return FIND;
        } else if (lowerCaseCommand.equals("view") || lowerCaseCommand.startsWith("view ")) {
            return VIEW;
        } else if (lowerCaseCommand.equals("list")) {
            return LIST;
        }
        return UNKNOWN;
    }
}
