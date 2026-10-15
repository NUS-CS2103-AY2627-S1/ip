package neil;

import java.util.Objects;

/**
 * Represents the text and outcome of processing a command.
 *
 * @param message user-facing response text.
 * @param type category used to display the response.
 */
public record CommandResult(String message, Type type) {
    /**
     * Lists the visual categories of command responses.
     */
    public enum Type {
        NORMAL,
        ERROR,
        HELP
    }

    /**
     * Ensures every result contains response text and a display category.
     */
    public CommandResult {
        Objects.requireNonNull(message);
        Objects.requireNonNull(type);
    }

    /**
     * Returns a normal command result containing the specified message.
     *
     * @param message user-facing response text.
     * @return normal command result.
     */
    public static CommandResult normal(String message) {
        return new CommandResult(message, Type.NORMAL);
    }

    /**
     * Returns an error command result containing the specified message.
     *
     * @param message user-facing error text.
     * @return error command result.
     */
    public static CommandResult error(String message) {
        return new CommandResult(message, Type.ERROR);
    }

    /**
     * Returns a help result containing the specified table text.
     *
     * @param message user-facing help table text.
     * @return help command result.
     */
    public static CommandResult help(String message) {
        return new CommandResult(message, Type.HELP);
    }

    /**
     * Returns whether this result describes an error.
     *
     * @return true if this result is an error.
     */
    public boolean isError() {
        return type == Type.ERROR;
    }

    /**
     * Returns whether this result contains the help table.
     *
     * @return true if this result contains help.
     */
    public boolean isHelp() {
        return type == Type.HELP;
    }
}
