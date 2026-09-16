package mira.command;

import java.util.Locale;

/**
 * Commands understood by Mira.
 */
public enum CommandType {
    /** Ends the chatbot session. */
    BYE,
    /** Displays all tasks. */
    LIST,
    /** Adds a task without scheduling information. */
    TODO,
    /** Adds a task with a due date or time. */
    DEADLINE,
    /** Adds a task with start and end times. */
    EVENT,
    /** Marks a task as completed. */
    MARK,
    /** Marks a task as not completed. */
    UNMARK,
    /** Removes a task. */
    DELETE,
    /** Finds tasks whose descriptions contain a keyword. */
    FIND,
    /** Represents an unrecognized command word. */
    UNKNOWN;

    private static final String TODO_ALIAS = "T";

    /**
     * Maps the first word of user input to a command type.
     *
     * @param word first word of a user command.
     * @return the corresponding command type, or {@link #UNKNOWN}.
     */
    public static CommandType fromWord(String word) {
        String normalizedWord = word.toUpperCase(Locale.ROOT);
        if (TODO_ALIAS.equals(normalizedWord)) {
            return TODO;
        }

        try {
            return valueOf(normalizedWord);
        } catch (IllegalArgumentException exception) {
            return UNKNOWN;
        }
    }
}
