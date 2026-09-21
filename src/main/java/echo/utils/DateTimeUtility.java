package echo.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Shared date-time formats so that input parsing and display stay
 * consistent across commands. Dates are entered as
 * {@code yyyy-MM-dd HHmm} (e.g., {@code 2019-12-02 1800}) and shown in
 * a friendlier style (e.g., {@code Dec 02 2019, 6:00 PM}).
 */
public final class DateTimeUtility {
    /** Accepts user-supplied dates, e.g. {@code 2019-12-02 1800}. */
    public static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Renders dates for display, e.g. {@code Dec 02 2019, 6:00 PM}. */
    public static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter
            .ofPattern("MMM dd yyyy, h:mm a")
            .withLocale(Locale.ENGLISH);

    private DateTimeUtility() {
    }

    /**
     * Evaluates if the targetTime is within this past week
     * and not in the future.
     * @param targetTime Time to be evaluated
     * @param weekStart Start of the week
     * @param currentTime The current time
     * @return Whether the target time is completed within the past week
     */
    public static boolean isWithinThisWeek(LocalDateTime targetTime, LocalDateTime weekStart,
                                        LocalDateTime currentTime) {
        return !targetTime.isBefore(weekStart) && !targetTime.isAfter(currentTime);
    }
}
