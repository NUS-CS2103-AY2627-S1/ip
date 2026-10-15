package rex.command;

/**
 * The set of recognized command words. UNKNOWN represents any input word
 * that doesn't match one of the others (i.e. an unrecognized command).
 *
 * This names what the user typed, and nothing more. What a command does, and
 * whether doing it changes the task list, belongs to the Command classes; the
 * parser uses this only to decide which one of them to build.
 */
public enum CommandType {
    LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, WITHIN, ON, FIND, BYE, UNKNOWN
}
