package toothless.command;

import java.util.Arrays;

/**
 * Identifies a command keyword understood by the Toothless chatbot.
 */
public enum CommandType {
    /** Adds a task that has no associated date or time. */
    TODO("todo"),
    /** Adds a task that must be completed by a specified date. */
    DEADLINE("deadline"),
    /** Adds a task that takes place between specified times. */
    EVENT("event"),
    /** Displays every task currently kept by Toothless. */
    LIST("list"),
    FIND("find"),
    MARK("mark"),
    /** Changes a selected task back to incomplete. */
    UNMARK("unmark"),
    /** Removes a selected task from Toothless's list. */
    DELETE("delete"),
    /** Replaces selected fields of an existing task. */
    EDIT("edit"),
    /** Ends the current Toothless session. */
    BYE("bye"),
    /** Represents input that does not match a supported command keyword. */
    UNKNOWN("");

    private final String keyword;

    /**
     * Creates a command type associated with the keyword entered by Toothless users.
     *
     * @param keyword lowercase keyword that identifies the command
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Converts a user-entered keyword into its corresponding command type.
     *
     * @param keyword first word entered by the user.
     * @return matching command type, or {@link #UNKNOWN} when there is no match
     */
    public static CommandType fromKeyword(String keyword) {
        if (BYE.keyword.equalsIgnoreCase(keyword)) {
            return BYE;
        }
        return Arrays.stream(values())
                .filter(commandType -> commandType.keyword.equals(keyword))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /**
     * Returns the command keyword used in user-facing instructions.
     *
     * @return lowercase command keyword
     */
    @Override
    public String toString() {
        return keyword;
    }
}
