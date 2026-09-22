package lebron.task;

/**
 * How often a recurring {@link Deadline} or {@link Event} repeats. Advancing
 * a task's date by one period is delegated to {@link DateTime}, which owns
 * the actual date arithmetic.
 */
public enum RecurrencePeriod {
    DAY, WEEK, MONTH;

    /**
     * Parses the command/file text for a period.
     *
     * @param text the period text, exactly as typed after {@code /every} or
     *     stored in the data file (one of {@code day}, {@code week}, {@code month})
     * @return the matching period
     * @throws IllegalArgumentException if it matches none of the above;
     *     callers turn this into a user-facing error message
     */
    public static RecurrencePeriod parse(String text) {
        switch (text) {
            case "day":
                return DAY;
            case "week":
                return WEEK;
            case "month":
                return MONTH;
            default:
                throw new IllegalArgumentException("unrecognised recurrence period: '" + text + "'");
        }
    }

    /**
     * Returns {@code dateTime} advanced by one of this period.
     *
     * @param dateTime the date/time to advance
     * @return the next occurrence's date/time
     */
    public DateTime advance(DateTime dateTime) {
        switch (this) {
            case DAY:
                return dateTime.plusDays(1);
            case WEEK:
                return dateTime.plusWeeks(1);
            case MONTH:
                return dateTime.plusMonths(1);
            default:
                // DAY/WEEK/MONTH are the only constants; unreachable given correct code.
                throw new AssertionError("Unreachable: unhandled period " + this);
        }
    }

    /** Returns the text stored in the data file and typed after {@code /every}. */
    public String toFileFormat() {
        return name().toLowerCase();
    }
}
