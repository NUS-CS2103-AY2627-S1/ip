package kafka.parser;

/**
 * Stores a parsed change to a deadline's schedule.
 *
 * @param taskNumber One-based number of the deadline to snooze.
 * @param newBy Replacement deadline.
 */
public record DeadlineSnoozeRequest(int taskNumber, String newBy)
        implements SnoozeRequest {
}
