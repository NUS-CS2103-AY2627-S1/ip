package tangent.exception;

/** Stores user-facing error-message templates used throughout Tangent. */
public final class ErrorMessages {
    /** Error shown when no command or task description is entered. */
    public static final String EMPTY_INPUT_MESSAGE = "please enter a command or task description!";
    /** Error shown when a command keyword is not recognized. */
    public static final String INVALID_COMMAND_MESSAGE = "this command doesn't exist :(";
    /** Error shown when a task description is missing. */
    public static final String MISSING_TASK_DESCRIPTION_MESSAGE = "please provide a task description!";
    /** Error shown when a find keyword is missing. */
    public static final String MISSING_SEARCH_KEYWORD_MESSAGE = "please provide a keyword to search for!";
    /** Error shown when task indexes are missing or invalid. */
    public static final String INVALID_TASK_INDEX_MESSAGE = "please provide a valid task number!";
    /** Error shown when a task-index range has invalid syntax. */
    public static final String TASK_INDEX_FORMAT_MESSAGE = "please provide task numbers or ranges separated by "
            + "spaces, (example: delete 1 4-6)";
    /** Error shown when a task type is unsupported. */
    public static final String UNKNOWN_TASK_TYPE_MESSAGE = "unknown task type :(";
    /** Error shown when deadline syntax is invalid. */
    public static final String DEADLINE_FORMAT_MESSAGE = "please use: deadline DESCRIPTION /by TIME";
    /** Error shown when event syntax is invalid. */
    public static final String EVENT_FORMAT_MESSAGE = "please use: event DESCRIPTION /from START /to END";
    /** Error shown when event end time is not after its start time. */
    public static final String END_TIME_ORDER_MESSAGE = "your end time must be later than your start time!";
    /** Error shown when a task description contains the storage separator. */
    public static final String DESCRIPTION_SEPARATOR_MESSAGE = "task descriptions cannot contain %s!";
    /** Error shown when a date does not match the accepted format. */
    public static final String BAD_DATE_MESSAGE = "bad date format :( ensure your dates are in the format "
            + "DD/MM/YYYY HHmm (example: 07/06/2026 2200)";
    /** Error shown when a date matches the accepted format but does not exist. */
    public static final String INVALID_DATE_MESSAGE = "invalid date :( ensure your date is a real calendar date "
            + "(example: 31/02/2024 does not exist)";
    /** Error shown when a saved task record contains an invalid date. */
    public static final String INVALID_STORED_DATE_MESSAGE = "data file contains an invalid date in task record: %s";
    /** Error shown when the configured storage path is a directory. */
    public static final String DATA_PATH_DIRECTORY_MESSAGE = "data path is a directory: %s";
    /** Error shown when storage receives no task list. */
    public static final String MISSING_TASK_LIST_MESSAGE = "unable to save tasks: task list is missing";
    /** Error shown when a saved task object is invalid. */
    public static final String INVALID_TASK_TO_SAVE_MESSAGE = "unable to save an invalid task";
    /** Error shown when a stored record is malformed. */
    public static final String INVALID_TASK_RECORD_MESSAGE = "data file contains an invalid task record: %s";
    /** Error shown when a stored record has an unsupported task type. */
    public static final String UNKNOWN_STORED_TASK_TYPE_MESSAGE = "data file contains an unknown task type: %s";
    /** Error shown when a stored event has an invalid time range. */
    public static final String INVALID_STORED_EVENT_RANGE_MESSAGE =
            "data file contains an invalid event time range: %s";
    /** Error shown when storage cannot complete a file operation. */
    public static final String STORAGE_OPERATION_FAILURE_MESSAGE = "unable to %s data file %s: %s";
}
