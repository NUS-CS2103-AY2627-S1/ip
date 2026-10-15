package charlie.command;

import charlie.exception.CharlieException;

/**
 * Identifies a command that Charlie can recognize.
 */
public enum CommandType {
    /** Ends the current Charlie session. */
    BYE("bye"),
    /** Displays all tasks. */
    LIST("list"),
    /** Marks a task as completed. */
    MARK("mark"),
    /** Marks a task as not completed. */
    UNMARK("unmark"),
    /** Deletes a task. */
    DELETE("delete"),
    /** Finds dated tasks occurring on a date. */
    ON("on"),
    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),
    /** Adds a todo. */
    TODO("todo"),
    /** Adds a deadline. */
    DEADLINE("deadline"),
    /** Adds an event. */
    EVENT("event"),
    /** Updates a task. */
    UPDATE("update");

    /** Keyword that identifies this command in user input. */
    private final String keyword;

    /**
     * Associates a command type with its input keyword.
     *
     * @param keyword Keyword entered to invoke the command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Converts a command keyword entered by the user into its corresponding enum value.
     *
     * @param keyword Command keyword entered by the user.
     * @return The matching command.
     * @throws CharlieException If the keyword does not represent a supported command.
     */
    public static CommandType parseKeyword(String keyword) {
        for (CommandType commandType : values()) {
            if (commandType.keyword.equals(keyword)) {
                return commandType;
            }
        }

        throw new CharlieException("Oh! I don't recognize that command yet.");
    }
}
