package duchess.tasks.exceptions;

import duchess.tasks.DateFormat;

/** Represents errors encountered while creating a task. */
public class TaskCreationException extends TaskException {
    private TaskCreationException(String message) {
        super(message);
    }

    /** Creates an exception for an empty task description. */
    public static TaskCreationException declareEmptyDescription(String type) {
        return new TaskCreationException(String.format(
                "I will not entertain a %s without a description.", type));
    }

    /** Creates an exception for invalid characters in a task command. */
    public static TaskCreationException declareInvalidCharacters(
            String command, String invalidChars) {
        return new TaskCreationException(String.format(
                "I find these characters unsuitable for the agenda.\n"
                + "Remove the following impediments: %s\n"
                + "Your command was: %s", invalidChars, command));
    }

    /** Creates an exception for a missing field in a task command. */
    public static TaskCreationException declareMissingField(
            String command,
            String fieldName,
            String correctFormat,
            String exampleCommand) {
        return new TaskCreationException(String.format(
                "I require a %s field for the %s command.\n"
                + "Kindly provide one in the prescribed format: %s\n"
                + "Example: %s",
                fieldName, command, correctFormat, exampleCommand));
    }

    /** Creates an exception for an invalid date format. */
    public static TaskCreationException declareInvalidDateFormat(String dateStr) {
        return new TaskCreationException(String.format(
                "The date \"%s\" is simply unacceptable.\n"
                + "Dates must be presented in the format: %s.\n"
                + "Example: %s",
                dateStr,
                DateFormat.PARSE_FORMAT_STRING,
                DateFormat.PARSE_FORMAT_EXAMPLE));
    }

    /** Creates an exception for an invalid event date range. */
    public static TaskCreationException declareInvalidDateRange(String from, String to) {
        return new TaskCreationException(String.format(
                "Your event begins on %s, after its end on %s.\n"
                + "Even I cannot schedule time backwards.\n"
                + "Please provide a valid date range.", from, to));
    }
    /** Creates an exception for an invalid recurrence format. */
    public static TaskCreationException declareInvalidRecurrenceFormat(
            String recurrence, String correctFormat, String exampleCommand) {
        return new TaskCreationException(String.format(
                "I find the recurrence \"%s\" unfit for the agenda.\n"
                + "Specify it as N days, then enter the command in the format: %s\n"
                + "Example: %s",
                recurrence, correctFormat, exampleCommand));
    }
}
