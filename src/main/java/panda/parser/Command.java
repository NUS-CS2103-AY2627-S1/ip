package panda.parser;

import panda.exception.InvalidCommandException;

/**
 * Represents a command understood by the Panda chatbot.
 *
 * Keeps command keywords and argument rules in one type-safe place.
 */
public enum Command {
    /** Ends the current Panda session. */
    BYE("bye", false),

    /** Displays all tasks or tasks occurring on a supplied date. */
    LIST("list", true),

    /** Finds task that matches a key word */
    FIND("find", true),

    /** Marks a task as completed. */
    MARK("mark", true),

    /** Marks a task as unfinished. */
    UNMARK("unmark", true),

    /** Changes the name of an existing task. */
    UPDATE("update", true),

    /** Removes a task from the task list. */
    DELETE("delete", true),

    /** Creates an event with start and end values. */
    EVENT("event", true),

    /** Creates a task with a deadline. */
    DEADLINE("deadline", true),

    /** Creates a date-free task. */
    TODO("todo", true);

    /** Stores the exact lowercase word entered by the user. */
    private final String keyword;

    /** Distinguishes commands that may contain details after their keyword. */
    private final boolean canAcceptArguments;

    /**
     * Creates a command definition.
     *
     * @param keyword the command word entered by the user.
     * @param canAcceptArguments whether text may follow the command word.
     */
    Command(String keyword, boolean canAcceptArguments) {
        this.keyword = keyword;
        this.canAcceptArguments = canAcceptArguments;
    }

    /**
     * Finds the command represented by a complete user message.
     *
     * The list command may include a date filter, while bye takes no arguments.
     *
     * @param message the complete user message.
     * @return the matching command.
     * @throws InvalidCommandException if the message does not match a supported command.
     */
    public static Command findCommand(String message) throws InvalidCommandException {
        for (Command command : values()) {
            if (command.matches(message)) {
                return command;
            }
        }
        throw new InvalidCommandException();
    }

    /**
     * Returns the command word entered by the user.
     *
     * @return the lowercase command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Checks whether a complete message represents this command.
     *
     * @param message the complete user message.
     * @return true when the message follows this command's argument rule.
     */
    public boolean matches(String message) {
        return message.equals(keyword) || canAcceptArguments && message.startsWith(keyword + " ");
    }
}
