package gary.command;

import java.util.Locale;

/**
 * Represents a command recognized by Gary.
 */
public enum CommandType {
    /** Ends the application. */
    BYE("bye"),
    /** Displays the command guide. */
    HELP("help"),
    /** Displays all tasks. */
    LIST("list"),
    /** Marks a task as done. */
    MARK("mark"),
    /** Marks a task as not done. */
    UNMARK("unmark"),
    /** Adds a todo. */
    TODO("todo"),
    /** Adds a deadline. */
    DEADLINE("deadline"),
    /** Adds an event. */
    EVENT("event"),
    /** Deletes a task. */
    DELETE("delete"),
    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),
    /** Manages contacts. */
    CONTACT("contact"),
    /** Represents unrecognized input. */
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the command type identified by the first word of the input.
     *
     * @param input Full command entered by the user.
     * @return Matching command type, or {@link #UNKNOWN} if no command matches.
     */
    public static CommandType from(String input) {
        assert input != null : "Command input should not be null";

        String strippedInput = input.strip();
        if (strippedInput.isEmpty()) {
            return UNKNOWN;
        }

        String keyword = strippedInput.split("\\s+", 2)[0].toLowerCase(Locale.ENGLISH);
        for (CommandType commandType : values()) {
            if (commandType.keyword.equals(keyword)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
