package kafka.parser;

import java.util.Optional;

/**
 * Stores parsed changes to an event's schedule.
 *
 * @param taskNumber One-based number of the event to snooze.
 * @param newFrom Replacement start, if supplied.
 * @param newTo Replacement end, if supplied.
 */
public record EventSnoozeRequest(
        int taskNumber, Optional<String> newFrom, Optional<String> newTo)
        implements SnoozeRequest {
}
