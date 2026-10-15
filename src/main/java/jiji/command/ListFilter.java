package jiji.command;

/**
 * Represents the display filter for the {@link ListCommand}.
 */
public enum ListFilter {
    /** Display all tasks in the list. */
    ALL,
    /** Display only incomplete (pending) tasks. */
    PENDING,
    /** Display only completed tasks. */
    DONE
}
