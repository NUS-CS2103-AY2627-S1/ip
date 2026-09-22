package oz;

/**
 * Represents the category of a response produced by Oz.
 */
public enum ResponseType {
    /** Response confirming that a task was added. */
    ADD,

    /** Response confirming that a task completion state changed. */
    CHANGE_MARK,

    /** Response confirming that a task was deleted. */
    DELETE,

    /** Response containing a task listing. */
    LIST,

    /** Response containing task search results. */
    FIND,

    /** Response confirming that the application should exit. */
    BYE,

    /** Response to empty or blank input. */
    BLANK,

    /** Response reporting an execution or parsing error. */
    ERROR,

    /** Response without specialized presentation. */
    DEFAULT
}
