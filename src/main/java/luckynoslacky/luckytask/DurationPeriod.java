package luckynoslacky.luckytask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

/**
 * Stores the calendar-based and clock-based parts of a snooze amount.
 *
 * @param calendarAmount years, months, and whole days to add
 * @param timeAmount fractional days, hours, and minutes to add
 */
public record DurationPeriod(Period calendarAmount, Duration timeAmount) {

    /**
     * Creates a duration period with non-null calendar and clock components.
     *
     * @param calendarAmount years, months, and whole days to add
     * @param timeAmount fractional days, hours, and minutes to add
     */
    public DurationPeriod {
        Objects.requireNonNull(calendarAmount, "Calendar amount cannot be null.");
        Objects.requireNonNull(timeAmount, "Time amount cannot be null.");
    }

    /**
     * Adds the calendar component before the clock component.
     *
     * @param dateTime date and time to update
     * @return updated date and time
     */
    public LocalDateTime addTo(LocalDateTime dateTime) {
        return dateTime.plus(calendarAmount).plus(timeAmount);
    }
}
