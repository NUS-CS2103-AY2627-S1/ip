package kelore.parser;

/** Represents a command that Kelore can execute. */
public enum Command {
    /** Exits Kelore. */
    BYE("bye", false),
    /** Displays all tasks. */
    LIST("list", false),
    /** Marks a task as completed. */
    MARK("mark", true),
    /** Marks a task as incomplete. */
    UNMARK("unmark", true),
    /** Deletes a task. */
    DELETE("delete", true),
    /** Adds a to-do. */
    TODO("todo", true),
    /** Adds a deadline. */
    DEADLINE("deadline", true),
    /** Adds an event. */
    EVENT("event", true),
    /** Displays dated tasks occurring on a specified date. */
    ON("on", true),
    /** Finds the earliest free time slot of a requested duration. */
    FREE("free", true),
    /** Finds tasks whose descriptions match a keyword. */
    FIND("find", true);

    private final String commandWord;
    private final boolean acceptsArguments;

    /**
     * Creates a command with its command word and argument behavior.
     *
     * @param commandWord Word that invokes the command.
     * @param acceptsArguments Whether the command accepts trailing arguments.
     */
    Command(String commandWord, boolean acceptsArguments) {
        this.commandWord = commandWord;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Returns whether the complete user input invokes this command.
     *
     * @param input Complete user input to check.
     * @return True if the input invokes this command; false otherwise.
     */
    boolean matches(String input) {
        return input.equals(commandWord)
                || acceptsArguments
                && input.startsWith(commandWord)
                && input.length() > commandWord.length()
                && Character.isWhitespace(input.charAt(commandWord.length()));
    }
}
