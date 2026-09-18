package blud.command;

import blud.exception.TaskTypeException;

/** Represents the commands accepted by Blud. */
public enum Command {
    TODO,
    DEADLINE,
    EVENT,
    MARK,
    UNMARK,
    LIST,
    FIND,
    DELETE,
    SORT,
    BYE;

    /** Converts a user-entered command name to its command enum value. */
    public static Command stringToCommand(String commandInput) {
        if (commandInput == null) {
            return null;
        }
        try {
            return Command.valueOf(commandInput.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new TaskTypeException(String.format(
                    "invalid task type %s, please use one of todo, event or deadline task types", commandInput));
        }
    }
}
