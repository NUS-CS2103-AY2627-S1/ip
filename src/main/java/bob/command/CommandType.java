package bob.command;

import java.util.Optional;

/**
 * Represents a command that can be entered by the user.
 */
public enum CommandType {
    /** Ends the chatbot session. */
    BYE("bye"),
    /** Displays all tasks. */
    LIST("list"),
    /** Finds tasks by description keyword. */
    FIND("find"),
    /** Searches task descriptions using word-prefix terms. */
    SEARCH("search"),
    /** Marks a task as done. */
    MARK("mark"),
    /** Marks a task as not done. */
    UNMARK("unmark"),
    /** Removes a task. */
    DELETE("delete"),
    /** Adds a to-do task. */
    TODO("todo"),
    /** Adds a deadline task. */
    DEADLINE("deadline"),
    /** Adds an event task. */
    EVENT("event");

    private final String keyword;

    /**
     * Creates a command type with its user-facing keyword.
     *
     * @param keyword word used to invoke the command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word that identifies this command in user input.
     *
     * @return command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Finds the command type represented by a keyword, ignoring letter case.
     *
     * @param keyword keyword to look up.
     * @return optional matching command type, empty if the keyword is unknown.
     */
    public static Optional<CommandType> fromKeyword(String keyword) {
        for (CommandType commandType : values()) {
            if (commandType.keyword.equalsIgnoreCase(keyword)) {
                return Optional.of(commandType);
            }
        }
        return Optional.empty();
    }
}
