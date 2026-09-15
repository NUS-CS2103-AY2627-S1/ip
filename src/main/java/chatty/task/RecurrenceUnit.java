package chatty.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Identifies a calendar unit supported by a recurring task schedule.
 */
public enum RecurrenceUnit {
    /** Recurs after a number of days. */
    DAY("day"),

    /** Recurs after a number of weeks. */
    WEEK("week"),

    /** Recurs after a number of months. */
    MONTH("month"),

    /** Recurs after a number of years. */
    YEAR("year");

    /** Singular unit name displayed to users. */
    private final String displayName;

    /**
     * Creates a recurrence unit with the supplied display name.
     *
     * @param displayName singular unit name displayed to users.
     */
    RecurrenceUnit(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Parses a supported singular or plural recurrence unit.
     *
     * @param input recurrence unit entered by the user.
     * @return matching recurrence unit.
     * @throws IllegalArgumentException if the unit is unsupported.
     */
    public static RecurrenceUnit fromInput(String input) {
        String normalizedInput = input.toLowerCase(Locale.ENGLISH);
        switch (normalizedInput) {
            case "day":
            case "days":
                return DAY;
            case "week":
            case "weeks":
                return WEEK;
            case "month":
            case "months":
                return MONTH;
            case "year":
            case "years":
                return YEAR;
            default:
                throw new IllegalArgumentException("Unsupported recurrence unit");
        }
    }

    /**
     * Advances a date by the supplied number of this unit.
     *
     * @param date date from which to advance.
     * @param amount positive number of units to add.
     * @return advanced date.
     */
    public LocalDate addTo(LocalDate date, int amount) {
        switch (this) {
            case DAY:
                return date.plusDays(amount);
            case WEEK:
                return date.plusWeeks(amount);
            case MONTH:
                return date.plusMonths(amount);
            case YEAR:
                return date.plusYears(amount);
            default:
                throw new AssertionError("Unhandled recurrence unit: " + this);
        }
    }

    /**
     * Formats an interval using the correct singular or plural unit name.
     *
     * @param amount positive recurrence interval.
     * @return user-facing recurrence interval.
     */
    public String formatInterval(int amount) {
        String suffix = amount == 1 ? "" : "s";
        return amount + " " + displayName + suffix;
    }
}
