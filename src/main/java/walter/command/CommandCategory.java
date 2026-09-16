package walter.command;

/**
 * Groups commands by the kind of response styling they should receive in a presentation layer.
 */
public enum CommandCategory {
    /** A command that creates a new task. */
    ADD,

    /** A command that changes the completion status of a task. */
    STATE_CHANGE,

    /** A command that removes a task. */
    DELETE,

    /** A command whose response has no special visual accent. */
    NORMAL,

    /** An input or execution failure. */
    ERROR
}
