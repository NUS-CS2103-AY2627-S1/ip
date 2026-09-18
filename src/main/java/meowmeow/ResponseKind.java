package meowmeow;

/**
 * What kind of reply a {@link Response} is, so the GUI can style each one
 * differently instead of showing every reply as an identical bubble.
 */
public enum ResponseKind {
    /** An ordinary confirmation or listing. */
    NORMAL,
    /** A user-facing error - an unrecognised command, a bad argument. */
    ERROR,
    /** A non-fatal problem reported in passing, e.g. a failed save. */
    WARNING,
    /** The final reply before the session ends - the "bye" response. */
    EXIT
}
