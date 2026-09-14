package nova.command;

import nova.NovaException;

/**
 * The keywords the chatbot understands, and the mapping from a line of user
 * input to the kind of command it starts.
 */
public enum CommandType {
    /** Ends the session. */
    BYE("bye"),

    /** Shows every task in the list. */
    LIST("list"),

    /** Marks the given task as done. */
    MARK("mark"),

    /** Marks the given task as not done. */
    UNMARK("unmark"),

    /** Removes the given task from the list. */
    DELETE("delete"),

    /** Adds a task with no date. */
    TODO("todo"),

    /** Adds a task due by a given moment. */
    DEADLINE("deadline"),

    /** Adds a task spanning a given stretch of time. */
    EVENT("event"),

    /** Shows the tasks falling on a given date. */
    ON("on"),

    /** Shows the tasks whose description contains a given keyword. */
    FIND("find"),

    /** Reorders the list chronologically. */
    SORT("sort"),

    /** Lists the commands the chatbot understands. */
    HELP("help");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, e.g. {@code deadline}
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns everything the user typed after the keyword.
     *
     * @param input the full line of input, which must start with this keyword
     * @return the arguments, trimmed, or an empty string if there were none
     */
    public String argumentsIn(String input) {
        assert input != null : "input should never be null";
        assert input.startsWith(keyword)
                : "argumentsIn called with input that does not start with '" + keyword + "': " + input;
        return input.substring(keyword.length()).trim();
    }

    /**
     * Works out which command a line of input starts with.
     *
     * @param input the full line of input
     * @return the matching command type
     * @throws NovaException if the line does not start with a known keyword
     */
    public static CommandType fromInput(String input) throws NovaException {
        for (CommandType type : CommandType.values()) {
            if (input.equals(type.keyword) || input.startsWith(type.keyword + " ")) {
                return type;
            }
        }
        throw new NovaException("I don't know that command. Type help to see what I understand.");
    }
}
