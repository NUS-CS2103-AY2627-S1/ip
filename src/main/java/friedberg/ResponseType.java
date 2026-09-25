package friedberg;

/**
 * Semantic categories used by frontends to style Friedberg responses.
 * These values describe the kind of response, not the exact parser command class.
 */
public enum ResponseType {
    /** Ordinary informational response such as list, find, bye, or greeting. */
    DEFAULT,
    /** A response confirming that a task was created. */
    ADD,
    /** A response confirming that a task status was changed. */
    STATUS,
    /** A response confirming that a task was removed or moved between lists. */
    REMOVE,
    /** A response reporting an error to the user. */
    ERROR
}
