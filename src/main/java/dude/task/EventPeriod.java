package dude.task;

import java.util.Objects;

/**
 * Represents the validated start and end of an event.
 *
 * @param from Event start.
 * @param to   Event end.
 */
public record EventPeriod(TaskDate from, TaskDate to) {
    /**
     * Validates that the event has two ordered endpoints.
     *
     * @param from Event start.
     * @param to   Event end.
     */
    public EventPeriod {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        if (!to.effectiveDateTime().isAfter(from.effectiveDateTime())) {
            throw new IllegalArgumentException("Event end precedes event start");
        }
    }
}
