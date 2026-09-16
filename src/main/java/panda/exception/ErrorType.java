package panda.exception;

/**
 * User-input errors that Panda can report.
 */
public enum ErrorType {
    EMPTY_COMMAND("OOPS!!! Please enter a command."),
    INVALID_COMMAND_FORMAT("OOPS!!! Commands must not have leading/trailing spaces or repeated whitespace."),
    EMPTY_TODO_DESCRIPTION("OOPS!!! The description of a todo cannot be empty."),
    EMPTY_DEADLINE_DESCRIPTION("OOPS!!! The description of a deadline cannot be empty."),
    MISSING_DEADLINE_BY("OOPS!!! A deadline must include a /by date."),
    EMPTY_DEADLINE_BY("OOPS!!! The /by date of a deadline cannot be empty."),
    INVALID_DATE_TIME("OOPS!!! Please enter a valid date/time, such as 2019-10-15 or 2/12/2019 1800."),
    EMPTY_EVENT_DESCRIPTION("OOPS!!! The description of an event cannot be empty."),
    MISSING_EVENT_TIMES("OOPS!!! An event must include /from and /to times."),
    EMPTY_EVENT_TIME("OOPS!!! An event must include both a start and end time."),
    EVENT_END_NOT_AFTER_START("OOPS!!! An event's end date/time must be after its start date/time."),
    DUPLICATE_PARAMETER("OOPS!!! The %s parameter can only be specified once."),
    UNEXPECTED_PARAMETER("OOPS!!! The %s parameter is not valid for this command."),
    INVALID_DESCRIPTION("OOPS!!! Task descriptions cannot contain '|', which is reserved for saved data."),
    DUPLICATE_TASK("OOPS!!! An identical task is already in the list."),
    STORAGE_LOAD_FAILED("OOPS!!! I could not read your saved tasks. Panda started with an empty list."),
    STORAGE_SAVE_FAILED("OOPS!!! Your change was made for this session, but could not be saved to disk."),
    EMPTY_FIND_KEYWORD("OOPS!!! The keyword to find cannot be empty."),
    UNKNOWN_COMMAND("OOPS!!! I'm sorry, but I don't know what that means :-("),
    MISSING_TASK_NUMBER("OOPS!!! Please specify a task number to %s."),
    NON_NUMERIC_TASK_NUMBER("OOPS!!! The task number must be a number."),
    TASK_NOT_FOUND("OOPS!!! That task number does not exist.");

    private final String messageTemplate;

    ErrorType(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    /**
     * Formats the user-facing message for this error.
     *
     * @param arguments values used by any placeholders in the message
     * @return the formatted Panda-style error message
     */
    public String getMessage(Object... arguments) {
        return String.format(messageTemplate, arguments);
    }
}
