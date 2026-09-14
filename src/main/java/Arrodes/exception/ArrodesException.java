package arrodes.exception;

/** Unchecked exception used for expected application-level errors. */
public class ArrodesException extends RuntimeException {
    /** Message used when no input is provided. */
    public static final String NO_INPUT = "The mirror hears no request. Speak a command.";
    /** Message used for an unsupported command. */
    public static final String UNKNOWN_COMMAND = "Arrodes cannot interpret that incantation.";
    /** Message used when a task description is absent. */
    public static final String EMPTY_DESCRIPTION = "Every request must bear a description.";
    /** Message used when required parameters are absent. */
    public static final String INCORRECT_PARAMS_COUNT = "The incantation requires more magic words (parameters).";
    /** Message used when parameters have an invalid shape. */
    public static final String INCORRECT_PARAMS = "The incantation's form is flawed. Consult the command guide.";
    /** Message used when the same parameter is supplied more than once. */
    public static final String DUPLICATE_PARAMETER = "Each parameter may be specified only once.";
    /** Message used when a task with the same details already exists. */
    public static final String DUPLICATE_TASK = "That request is already recorded in Arrodes' annals.";
    /** Message used when a tag does not follow the supported format. */
    public static final String INVALID_TAG = "Tags may contain only letters, numbers, hyphens, and underscores.";
    /** Message used when an event ends before it starts. */
    public static final String INVALID_EVENT_TIME = "The appointed hour cannot end before it begins.";

    /** Message used when an event mixes date-only and date-time values. */
    public static final String INCONSISTENT_TIME_FORMAT = "The appointed hours must share the same format.";
    /** Message used when a command includes an unnecessary description. */
    public static final String UNNECESSARY_DESCRIPTION = "This incantation requires no description.";
    /** Message used when a date or time does not exist. */
    public static final String INVALID_DATE = "That appointed date or time does not exist.";
    /** Message used when a date or time has an unsupported format. */
    public static final String INVALID_DATE_FORMAT = "Use the date format yyyy-MM-dd or yyyy-MM-ddTHH:mm.";

    /** Message used when the task list has reached capacity. */
    public static final String TASK_LIST_FULL = "Records of Arrodes reached its limit and"
            + "can no longer accept more request!!!";
    /** Message used when a requested task number is invalid. */
    public static final String ITEM_NOT_IN_LIST = "That task does not appear in Arrodes' annals.";

    /** Message used when a mark, unmark, or delete number is not numeric. */
    public static final String NOT_A_NUMBER = "Arrodes requires a numbered record.";
    /** Message used when Arrodes cannot preserve task records. */
    public static final String SAVE_FAILURE = "Arrodes could not preserve the annals. Your records remain unchanged.";
    /** Message used when Arrodes cannot restore task records. */
    public static final String LOAD_FAILURE = "Arrodes could not restore the annals.";
    /** Message used when an unexpected failure prevents command processing. */
    public static final String UNEXPECTED_FAILURE = "Arrodes encountered an unexpected error and kept your records "
            + "safe.";

    /**
     * Constructs an exception without a detail message.
     */
    public ArrodesException() {
        super();
    }

    /**
     * Constructs an exception with a detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ArrodesException(String message) {
        super(message);
    }
}
