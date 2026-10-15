package kafka.parser;

/**
 * Identifies the task selected by a parsed snooze command.
 */
public interface SnoozeRequest {
    /**
     * Returns the one-based number of the task to snooze.
     *
     * @return Selected task number.
     */
    int taskNumber();
}
