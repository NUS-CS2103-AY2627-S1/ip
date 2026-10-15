package bos;

/**
 * Represents an error caused by an invalid command entered by the user.
 */
public class BosException extends Exception {
    /**
     * Creates an exception with the message that Bos should show to the user.
     *
     * @param message explanation of the input error.
     */
    public BosException(String message) {
        super(message);
    }

    /**
     * Creates an error for a task command with no description.
     *
     * @param commandType task-creation command with no description.
     * @return exception describing the missing description.
     */
    public static BosException createEmptyDescriptionException(CommandType commandType) {
        String taskType = commandType.getKeyword();
        String article = commandType == CommandType.EVENT ? "an" : "a";
        return new BosException("The description of " + article + " " + taskType + " cannot be empty.");
    }

    /**
     * Creates an error for a command that does not follow its required format.
     *
     * @param usage example of the correct command format.
     * @return exception describing the correct format.
     */
    public static BosException createInvalidFormatException(String usage) {
        return new BosException("Please use this command format: " + usage);
    }

    /**
     * Creates an error for a command that contains the same parameter more than once.
     *
     * @param parameter duplicated parameter marker.
     * @return exception describing the repeated parameter.
     */
    public static BosException createRepeatedParameterException(String parameter) {
        return new BosException("The " + parameter + " parameter may only be specified once.");
    }

    /**
     * Creates an error for a date-time value that is malformed or does not exist.
     *
     * @param fieldName name of the invalid date-time field.
     * @return exception describing the accepted date-time format.
     */
    public static BosException createInvalidDateTimeException(String fieldName) {
        return new BosException(
                "The " + fieldName + " must be a real date and time in yyyy-MM-dd HHmm format.");
    }

    /**
     * Creates an error for an event whose end is not later than its start.
     *
     * @return exception describing the invalid time range.
     */
    public static BosException createInvalidEventRangeException() {
        return new BosException("The event end must be later than its start.");
    }

    /**
     * Creates an error for task data containing an unsupported character.
     *
     * @return exception describing the unsupported character.
     */
    public static BosException createInvalidTaskCharacterException() {
        return new BosException("Task details cannot contain | or control characters.");
    }

    /**
     * Creates an error for a command containing no text.
     *
     * @return exception describing the empty command.
     */
    public static BosException createEmptyCommandException() {
        return new BosException("Please enter a command.");
    }

    /**
     * Creates an error for a task number that is missing or is not an integer.
     *
     * @return exception describing the invalid task number.
     */
    public static BosException createInvalidTaskNumberException() {
        return new BosException("The task number must be a whole number.");
    }

    /**
     * Creates an error for a task number outside the current task list.
     *
     * @return exception describing the missing task.
     */
    public static BosException createTaskNotFoundException() {
        return new BosException("There is no task with that number.");
    }

    /**
     * Creates an error for a task whose description matches an existing task.
     *
     * @return exception describing the duplicate task.
     */
    public static BosException createDuplicateTaskException() {
        return new BosException("This task already exists in the task list.");
    }

    /**
     * Creates an error for an unrecognized command.
     *
     * @return exception describing the unknown command.
     */
    public static BosException createUnknownCommandException() {
        return new BosException("I'm sorry, but I don't know what that means :-(");
    }
}
