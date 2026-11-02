package megia.exception;

/**
 * Identifies a user-facing error independently of its localized message.
 */
public enum ErrorCode {
    /** A todo commandName omitted its description. */
    TODO_DESCRIPTION_MISSING,
    /** A deadline commandName omitted its {@code /by} marker. */
    DEADLINE_BY_MARKER_MISSING,
    /** A deadline commandName omitted its description. */
    DEADLINE_DESCRIPTION_MISSING,
    /** A deadline commandName omitted the value after its {@code /by} marker. */
    DEADLINE_BY_VALUE_MISSING,
    /** A deadline has an invalid date or time value. */
    DEADLINE_DATE_INVALID,
    /** An event commandName omitted its {@code /from} marker. */
    EVENT_FROM_MARKER_MISSING,
    /** An event commandName omitted its {@code /to} marker. */
    EVENT_TO_MARKER_MISSING,
    /** An event commandName placed its markers in the wrong order. */
    EVENT_MARKERS_OUT_OF_ORDER,
    /** An event commandName omitted its description. */
    EVENT_DESCRIPTION_MISSING,
    /** An event commandName omitted the value after its {@code /from} marker. */
    EVENT_FROM_VALUE_MISSING,
    /** An event commandName omitted the value after its {@code /to} marker. */
    EVENT_TO_VALUE_MISSING,
    /** An event has an invalid date value. */
    EVENT_DATE_INVALID,
    /** An event has an invalid start time value. */
    EVENT_START_TIME_INVALID,
    /** An event has an invalid end time value. */
    EVENT_END_TIME_INVALID,
    /** An event ends at or before its start. */
    EVENT_END_NOT_AFTER_START,
    /** A commandName repeated a marker that must occur once. */
    DUPLICATE_MARKER,
    /** A task operation omitted its task ID. */
    TASK_ID_MISSING,
    /** A task operation supplied a non-integer task ID. */
    TASK_ID_NOT_INTEGER,
    /** A task operation supplied a non-positive task ID. */
    TASK_ID_NOT_POSITIVE,
    /** A task operation supplied an integer larger than Java can represent. */
    TASK_ID_TOO_LARGE,
    /** A task operation requires a task, but the task list is empty. */
    TASK_LIST_EMPTY,
    /** A task operation refers to an unavailable task ID. */
    TASK_NOT_FOUND,
    /** The supplied commandName name is not recognized. */
    UNKNOWN_COMMAND,
    /** Stored task data is malformed. */
    STORAGE_MALFORMED,
    /** The stored task data cannot be read. */
    STORAGE_UNREADABLE,
    /** Task data could not be written to storage. */
    STORAGE_WRITE_FAILED,
    /** A task list request has an invalid date. */
    LIST_DATE_INVALID,
    /** A find command omitted its description query. */
    FIND_QUERY_MISSING,
    /** A task description contains a line break that cannot be stored. */
    DESCRIPTION_LINE_BREAK
}
