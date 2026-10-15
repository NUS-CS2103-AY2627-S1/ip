package dingleberry.parser;

/**
 * Represents a command understood by Dingleberry.
 */
public enum CommandWord {
    /** Represents the command that ends the application. */
    BYE("bye"),
    /** Represents the command that lists all tasks. */
    LIST("list"),
    /** Represents the command that finds matching tasks. */
    FIND("find"),
    /** Represents the command that deletes one task. */
    DELETE("delete"),
    /** Represents the command that marks one task as done. */
    MARK("mark"),
    /** Represents the command that marks one task as not done. */
    UNMARK("unmark"),
    /** Represents the command that adds a todo. */
    TODO("todo"),
    /** Represents the command that adds a deadline. */
    DEADLINE("deadline"),
    /** Represents the command that adds an event. */
    EVENT("event");

    /** Stores the command text accepted from the user. */
    private final String keyword;

    CommandWord(final String commandKeyword) {
        this.keyword = commandKeyword;
    }

    /**
     * Returns the text that begins this command.
     *
     * @return the command keyword.
     */
    public String keyword() {
        return keyword;
    }

    /**
     * Returns whether the input is exactly this command's keyword.
     *
     * @param input the user input to compare.
     * @return true when the input equals this keyword ignoring case.
     */
    public boolean isExactInput(final String input) {
        return input.equalsIgnoreCase(keyword);
    }

    /**
     * Returns the command identified by the input, or null when none matches.
     *
     * @param input the user input to inspect.
     * @return the matching command, or null when there is no match.
     */
    public static CommandWord fromInput(final String input) {
        for (CommandWord command : values()) {
            if (command.isMatchingInput(input)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Matches an exact keyword or a keyword followed by a space and arguments.
     * The exit command only matches its exact keyword.
     *
     * @param input the command line to compare without normalizing whitespace.
     * @return true when this command recognizes the input, ignoring case.
     */
    private boolean isMatchingInput(final String input) {
        if (isExactInput(input)) {
            return true;
        }
        if (this == BYE) {
            return false;
        }
        String argumentPrefix = keyword + " ";
        return input.regionMatches(true, 0, argumentPrefix, 0,
                argumentPrefix.length());
    }
}
