package pixel.command;

/**
 * Identifies a task detail that can be changed by an update command.
 */
public enum UpdateField {
    /** Task description. */
    DESCRIPTION,
    /** Deadline due date. */
    BY,
    /** Event start time. */
    FROM,
    /** Event end time. */
    TO
}
