package yachiyo.exception;

/**
 * Describes how prominently an application error should be presented to the user.
 */
public enum ErrorCategory {
    /** User input that can be corrected and submitted again. */
    WARNING,

    /** A valid command that cannot be applied in the current context. */
    INVALID_OPERATION,

    /** A failure to load or save application data. */
    SYSTEM_ERROR
}
