package kenbot;

import java.util.Arrays;
import java.util.Locale;

/**
 * The commands Kenbot understands.
 *
 * <p>Each value's name, in lower case, is the word the user types, so this list
 * is the single record of which commands exist. Because {@code handleCommand}
 * chooses what to do with a switch expression over this type, adding a value
 * here without handling it there stops the program compiling, rather than
 * failing quietly when a user tries the new command.</p>
 */
public enum CommandType {
    /** Ends the program. */
    BYE,

    /** Shows every task, numbered. */
    LIST,

    /** Marks one task as done. */
    MARK,

    /** Marks one task as not done. */
    UNMARK,

    /** Adds a task with only a description. */
    TODO,

    /** Adds a task with a description and a due date. */
    DEADLINE,

    /** Adds a task with a description, a start and an end. */
    EVENT,

    /** Removes one task from the list. */
    DELETE,

    /** Shows the tasks whose description contains a keyword. */
    FIND,

    /** Attaches one or more tags to a task. */
    TAG,

    /** Removes one or more tags from a task. */
    UNTAG;

    /**
     * Works out which command the user typed.
     *
     * @param word the first word of the line the user typed
     * @return the command that word names
     * @throws KenbotException if the word does not name a command
     */
    public static CommandType from(String word) throws KenbotException {
        // findFirst stops at the match rather than reading the rest, the same
        // as the early return did. orElseThrow takes a supplier, so the
        // exception is built only when nothing matched, and it is allowed to be
        // a checked one.
        return Arrays.stream(values())
                .filter(command -> command.name().toLowerCase(Locale.ROOT).equals(word))
                .findFirst()
                .orElseThrow(() -> new KenbotException("Don't know that one."));
    }
}
