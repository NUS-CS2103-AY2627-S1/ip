package nori.task;

import java.time.LocalDate;

/**
 * Represents an inclusive date range for a task query.
 */
public class DateRange {
    /** Inclusive first date of the range. */
    private final LocalDate fromDate;
    /** Inclusive last date of the range. */
    private final LocalDate toDate;

    /**
     * Creates an inclusive range between two dates.
     *
     * @param fromDate the first date in the range.
     * @param toDate the last date in the range.
     */
    public DateRange(LocalDate fromDate, LocalDate toDate) {
        assert fromDate != null && toDate != null : "A range is built from two dates the parser read.";
        assert !toDate.isBefore(fromDate) : "The parser reports a reversed range instead of building one.";

        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Returns the first date in the range.
     *
     * @return the inclusive lower bound.
     */
    public LocalDate getFrom() {
        return fromDate;
    }

    /**
     * Returns the last date in the range.
     *
     * @return the inclusive upper bound.
     */
    public LocalDate getTo() {
        return toDate;
    }
}
