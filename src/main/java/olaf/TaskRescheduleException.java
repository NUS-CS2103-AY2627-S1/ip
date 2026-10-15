package olaf;

/**
 * Signals that a task cannot accept the requested replacement schedule.
 */
final class TaskRescheduleException extends Exception {
    private static final long serialVersionUID = 1L;

    TaskRescheduleException(String message) {
        super(message);
    }
}
