package noms.exception;

/** Indicates that a task cannot be snoozed using the supplied command. */
public class InvalidSnoozeException extends NomsException {
    /**
     * Creates the exception with a message explaining the snooze problem.
     *
     * @param message the explanation shown to the user
     */
    public InvalidSnoozeException(String message) {
        super(message);
    }
}
