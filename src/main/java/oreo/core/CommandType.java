package oreo.core;


/** Represents a command that Oreo can recognize from user input. */
public enum CommandType {
    BYE,
    HELP,
    LIST,
    FIND,
    MARK,
    UNMARK,
    DELETE,
    DEADLINE,
    EVENT,
    TODO,
    ON_DATE,
    MERCHANDISE,
    EDIT_MERCHANDISE,
    DELETE_MERCHANDISE,
    FIND_MERCHANDISE,
    LIST_ALL,
    EMPTY,
    UNKNOWN;

    /** Identifies the command represented by the complete user input. */
    public static CommandType fromInput(String input) {
        if (matches(input, "bye")) {
            return BYE;
        } else if (matches(input, "help")) {
            return HELP;
        } else if (matches(input, "list")) {
            return LIST;
        } else if (matches(input, "find")) {
            return FIND;
        } else if (matches(input, "mark")) {
            return MARK;
        } else if (matches(input, "unmark")) {
            return UNMARK;
        } else if (matches(input, "delete")) {
            return DELETE;
        } else if (matches(input, "deadline")) {
            return DEADLINE;
        } else if (matches(input, "event")) {
            return EVENT;
        } else if (matches(input, "todo")) {
            return TODO;
        } else if (matches(input, "on")) {
            return ON_DATE;
        } else if (matches(input, "merchandise")) {
            return MERCHANDISE;
        } else if (matches(input, "edit-merchandise")) {
            return EDIT_MERCHANDISE;
        } else if (matches(input, "delete-merchandise")) {
            return DELETE_MERCHANDISE;
        } else if (matches(input, "find-merchandise")) {
            return FIND_MERCHANDISE;
        } else if (matches(input, "listall")) {
            return LIST_ALL;
        } else if (input.isEmpty()) {
            return EMPTY;
        }
        return UNKNOWN;
    }

    /** Returns whether input is exactly a command or starts with the command followed by a space. */
    private static boolean matches(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }
}
