package nori;

import java.util.Locale;

/**
 * Identifies the commands understood by Nori.
 */
public enum Command {
    /** Exits Nori. */
    BYE,
    /** Displays all tasks. */
    LIST,
    /** Finds tasks by description. */
    FIND,
    /** Displays instructions for every command. */
    HELP,
    /** Marks a task as complete. */
    MARK,
    /** Marks a task as incomplete. */
    UNMARK,
    /** Removes a task. */
    DELETE,
    /** Adds a todo task. */
    TODO,
    /** Adds a deadline task. */
    DEADLINE,
    /** Adds an event task. */
    EVENT;

    /**
     * Converts user input to a command, reporting unknown command words clearly.
     *
     * @param word Command word entered by the user.
     * @return Matching command.
     * @throws NoriException If the word is not a supported command.
     */
    public static Command from(String word) throws NoriException {
        try {
            return valueOf(word.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new NoriException("I don't recognise that command. "
                    + "Try help to see the available commands.");
        }
    }
}
