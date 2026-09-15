package luke.exceptions;

/** Represents a problem with a command's argument text. */
public class InvalidArgumentException extends UserInputException {
    private InvalidArgumentException(String message) {
        super(message);
    }

    /**
     * Creates an error for commands such as {@code list} or {@code bye} that
     * do not accept text after the command word.
     *
     * @param command command that rejected the argument
     * @param argument unexpected argument text
     * @return formatted argument exception
     */
    public static InvalidArgumentException unexpected(String command, String argument) {
        return new InvalidArgumentException(
                "`%s` command does not take arguments: %s".formatted(command, argument));
    }

    /**
     * Creates an error for task commands with no description before their flags.
     *
     * @param taskType task type that is missing a description
     * @return formatted argument exception
     */
    public static InvalidArgumentException missingDescription(String taskType) {
        return new InvalidArgumentException(
                "Missing description for %s task.".formatted(taskType));
    }

    /**
     * Creates an error for commands that require argument text.
     *
     * @param command command that expected an argument
     * @return formatted argument exception
     */
    public static InvalidArgumentException missing(String command) {
        return new InvalidArgumentException(
                "`%s` command requires an argument.".formatted(command));
    }

    /**
     * Creates an error for index commands where the argument is not a number.
     *
     * @param command command that expected an index
     * @param argument non-numeric argument text
     * @return formatted argument exception
     */
    public static InvalidArgumentException invalidIndex(String command, String argument) {
        return new InvalidArgumentException(
                "`%s` command received invalid index: %s".formatted(command, argument));
    }

    /**
     * Creates an error for numeric indexes that do not refer to a stored task.
     *
     * @param command command that expected an existing task index
     * @param argument numeric argument text
     * @return formatted argument exception
     */
    public static InvalidArgumentException outOfBoundsIndex(String command, String argument) {
        return new InvalidArgumentException(
                "`%s` command received out-of-bounds index: %s".formatted(command, argument));
    }

    /**
     * Creates an error for terminal control characters in user input.
     *
     * @return formatted argument exception
     */
    public static InvalidArgumentException unsupportedControlCharacter() {
        return new InvalidArgumentException(
                "Input contains unsupported control characters.");
    }

    /**
     * Creates an error for an event whose end is not after its start.
     *
     * @return formatted event-range exception
     */
    public static InvalidArgumentException invalidEventRange() {
        return new InvalidArgumentException(
                "Event end must be after its start.");
    }
}
