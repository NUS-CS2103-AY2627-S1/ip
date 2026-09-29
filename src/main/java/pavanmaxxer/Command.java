package pavanmaxxer;

/**
 * Represents the finite set of commands understood by Pavanmaxxer.
 */
public enum Command {
    /** Exits the application. */
    BYE("bye", false),
    /** Lists all stored tasks. */
    LIST("list", false),
    /** Marks a task as completed. */
    MARK("mark", true),
    /** Marks a task as incomplete. */
    UNMARK("unmark", true),
    /** Deletes a task. */
    DELETE("delete", true),
    /** Creates a todo task. */
    TODO("todo", true),
    /** Creates a deadline task. */
    DEADLINE("deadline", true),
    /** Creates an event task. */
    EVENT("event", true),
    /** Finds tasks whose descriptions match a keyword. */
    FIND("find", true),
    /** Represents unsupported input. */
    UNKNOWN("", false);

    private final String keyword;
    private final boolean hasArguments;

    Command(String keyword, boolean hasArguments) {
        this.keyword = keyword;
        this.hasArguments = hasArguments;
    }

    /**
     * Classifies raw user input without accepting a partial command name.
     *
     * @param input Raw line entered by the user.
     * @return The matching command, or {@link #UNKNOWN} when none matches.
     */
    public static Command from(String input) {
        for (Command command : values()) {
            if (command == UNKNOWN) {
                continue;
            }
            if (input.equals(command.keyword)
                    || command.hasArguments && input.startsWith(command.keyword + " ")) {
                return command;
            }
        }
        return UNKNOWN;
    }
}
