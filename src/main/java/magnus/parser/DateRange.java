package magnus.parser;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Contains the start and end dates parsed from a date-range argument.
 *
 * @param startDate The first date in the range.
 * @param endDate The last date in the range.
 */
public record DateRange(LocalDate startDate, LocalDate endDate) {
    /**
     * Creates a range whose date endpoints are both present.
     */
    public DateRange {
        Objects.requireNonNull(startDate, "Start date cannot be null");
        Objects.requireNonNull(endDate, "End date cannot be null");
    }
}
