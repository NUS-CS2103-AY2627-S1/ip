package quu;

/**
 * The category of a reply, used to decide how that reply is presented.
 *
 * <p>This is not the set of words the user may type: {@code todo}, {@code deadline} and
 * {@code event} are three commands but one category, {@link #ADD}, because a reply to any
 * of them is presented the same way. The JavaFX front end reads the category through
 * {@code Quu.getCommandType()} and styles the dialog box accordingly.
 */
public enum CommandType {
    /** No command has run yet, so the reply is the opening greeting. */
    NONE,
    /** A task was added by a todo, deadline or event command. */
    ADD,
    /** A task was marked as done. */
    MARK,
    /** A task was marked as not done. */
    UNMARK,
    /** A task was removed. */
    DELETE,
    /** A task's details were changed. */
    UPDATE,
    /** The task list was shown. */
    LIST,
    /** A search was run over the task list. */
    FIND,
    /** The command failed and the reply explains why. */
    ERROR
}
