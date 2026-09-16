package serangooner.task;

import java.time.LocalDate;

import serangooner.SerangoonerException;

/**
 * Represents a span of dates, from a first day to a last day, both counted
 * as part of the span.
 * A range that ends before it starts cannot be built, so any range handed
 * to the rest of the program is already known to make sense.
 *
 * @param start First date of the span.
 * @param end Last date of the span.
 */
public record DateRange(LocalDate start, LocalDate end) {
    /**
     * Constructs a span of dates.
     *
     * @throws SerangoonerException If the span ends before it starts.
     */
    public DateRange {
        if (end.isBefore(start)) {
            throw new SerangoonerException("that range ends before it starts o.O");
        }
    }
}
