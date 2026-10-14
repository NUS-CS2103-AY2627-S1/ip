package milo.parser;

/**
 * Identifies the finite set of commands understood by Milo.
 */
public enum CommandType {
    /** Exit command. */
    BYE,
    /** Help command. */
    HELP,
    /** List command. */
    LIST,
    /** Statistics command. */
    STATS,
    /** Find command. */
    FIND,
    /** Mark command. */
    MARK,
    /** Unmark command. */
    UNMARK,
    /** Delete command. */
    DELETE,
    /** Todo creation command. */
    TODO,
    /** Deadline creation command. */
    DEADLINE,
    /** Event creation command. */
    EVENT,
    /** Unrecognized command. */
    UNKNOWN
}
