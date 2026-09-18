package herta.storage;

/**
 * Describes how certain storage is about the result of the latest save attempt.
 */
public enum PersistenceState {
    /** No persistence operation was attempted for the current command. */
    NOT_ATTEMPTED,
    /** A persistence operation has started and its final result is not known yet. */
    IN_PROGRESS,
    /** The new data was fully committed to storage. */
    COMMITTED,
    /** A failure occurred and the original data could not be proven unchanged. */
    UNKNOWN
}
