package oz.task;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import oz.exception.OzException;

/**
 * Represents a fixed weekly recurrence beginning on a specific date.
 */
final class WeeklyRecurrence {
    /** Number of days in one week. */
    private static final int DAYS_PER_WEEK = 7;

    /** Error shown when a recurrence interval is not positive. */
    private static final String INVALID_INTERVAL_MESSAGE =
            "The recurrence interval must be a positive whole number.";

    /** Error shown when the recurrence ends before its first occurrence. */
    private static final String UNTIL_BEFORE_FIRST_MESSAGE =
            "The recurrence end date (/until) cannot be before the first occurrence.";

    /** Date of the first occurrence. */
    private final LocalDate firstOccurrenceDate;

    /** Number of weeks between occurrence dates. */
    private final int weekInterval;

    /** Inclusive final occurrence date, or null for an indefinite recurrence. */
    private final LocalDate untilDate;

    /**
     * Constructs a weekly recurrence rule.
     *
     * @param firstOccurrenceDate Date of the first occurrence.
     * @param weekInterval Positive number of weeks between occurrences.
     * @param untilDate Inclusive recurrence end date, or null for no end date.
     * @throws OzException If the interval or end date is invalid.
     */
    WeeklyRecurrence(LocalDate firstOccurrenceDate, int weekInterval,
            LocalDate untilDate) throws OzException {
        assert firstOccurrenceDate != null
                : "A weekly recurrence must have a first occurrence date";
        if (weekInterval <= 0) {
            throw new OzException(INVALID_INTERVAL_MESSAGE);
        }
        if (untilDate != null && untilDate.isBefore(firstOccurrenceDate)) {
            throw new OzException(UNTIL_BEFORE_FIRST_MESSAGE);
        }

        this.firstOccurrenceDate = firstOccurrenceDate;
        this.weekInterval = weekInterval;
        this.untilDate = untilDate;
    }

    /**
     * Checks whether the recurrence produces an occurrence on a date.
     *
     * @param date Date to test.
     * @return True if an occurrence starts on the date; false otherwise.
     */
    boolean occursOn(LocalDate date) {
        if (date == null || date.isBefore(this.firstOccurrenceDate)) {
            return false;
        }
        if (this.untilDate != null && date.isAfter(this.untilDate)) {
            return false;
        }

        long daysFromFirstOccurrence = ChronoUnit.DAYS.between(
                this.firstOccurrenceDate, date);
        long recurrenceIntervalDays = (long) this.weekInterval * DAYS_PER_WEEK;
        return daysFromFirstOccurrence % recurrenceIntervalDays == 0;
    }

    /**
     * Returns the number of weeks between occurrences.
     *
     * @return Positive recurrence interval in weeks.
     */
    int getWeekInterval() {
        return this.weekInterval;
    }

    /**
     * Returns the inclusive recurrence end date.
     *
     * @return End date, or null for an indefinite recurrence.
     */
    LocalDate getUntilDate() {
        return this.untilDate;
    }

    /**
     * Formats the recurrence rule for user display.
     *
     * @return Human-readable recurrence description.
     */
    String toDisplayString() {
        String intervalUnit = this.weekInterval == 1 ? "week" : "weeks";
        String recurrenceDescription = "every " + this.weekInterval + " " + intervalUnit;
        if (this.untilDate == null) {
            return recurrenceDescription;
        }
        return recurrenceDescription + " until " + TaskDateTime.formatDate(this.untilDate);
    }
}
