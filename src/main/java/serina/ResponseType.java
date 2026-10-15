package serina;

/**
 * Describes how a response should be presented to the user.
 */
public enum ResponseType {
    /** Indicates an ordinary application response. */
    NORMAL,
    /** Indicates a command-processing error requiring the user's attention. */
    ERROR,
    /** Indicates a non-fatal problem encountered outside command processing. */
    WARNING
}
