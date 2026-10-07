package mary.exception;

/**
 * Categorizes input errors that MARY can report.
 */
public enum ErrorType {
    EMPTY_DESCRIPTION,
    INVALID_COMMAND,
    INVALID_TASK_NUMBER,
    TASK_NOT_FOUND,
    INVALID_DEADLINE_FORMAT,
    INVALID_EVENT_FORMAT
}
