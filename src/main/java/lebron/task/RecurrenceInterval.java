package lebron.task;

import java.time.LocalDateTime;

import lebron.exception.LeBronException;

/**
 * Represents how often a {@link RecurringTask} repeats.
 */
public enum RecurrenceInterval {
    DAILY, WEEKLY, MONTHLY;

    /**
     * Parses a user-facing recurrence word into the matching RecurrenceInterval.
     *
     * @param word The word as typed by the user, e.g. "day", "week", or "month".
     * @return The matching RecurrenceInterval.
     * @throws LeBronException if the word does not match a known interval.
     */
    public static RecurrenceInterval fromKeyword(String word) throws LeBronException {
        switch (word) {
            case "day":
                return DAILY;
            case "week":
                return WEEKLY;
            case "month":
                return MONTHLY;
            default:
                throw new LeBronException("Every day, week, or month, that's how we do it round here!");
        }
    }

    /**
     * Returns the given date/time advanced forward by one of this interval.
     *
     * @param dateTime The date/time to advance.
     * @return The next occurrence's date/time.
     */
    public LocalDateTime advance(LocalDateTime dateTime) {
        switch (this) {
            case DAILY:
                return dateTime.plusDays(1);
            case WEEKLY:
                return dateTime.plusWeeks(1);
            case MONTHLY:
                return dateTime.plusMonths(1);
            default:
                throw new IllegalStateException("Unknown RecurrenceInterval: " + this);
        }
    }

    /**
     * Returns the user-facing word for this interval, e.g. "week" for WEEKLY.
     *
     * @return The lowercase, singular word for this interval.
     */
    @Override
    public String toString() {
        switch (this) {
            case DAILY:
                return "day";
            case WEEKLY:
                return "week";
            case MONTHLY:
                return "month";
            default:
                throw new IllegalStateException("Unknown RecurrenceInterval: " + this);
        }
    }
}
