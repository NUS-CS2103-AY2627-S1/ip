package yqr.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

import yqr.exception.YqrException;

/**
 * Validates chronological event values that use recognized date or time formats.
 */
public final class EventTimeValidator {
    private static final Pattern DATE_TIME_SHAPE =
            Pattern.compile("\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}");
    private static final Pattern DATE_SHAPE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern TIME_SHAPE = Pattern.compile("\\d{2}:\\d{2}");
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    /** Prevents creation of this utility class. */
    private EventTimeValidator() {
    }

    /**
     * Rejects equal ranges and validates the order of recognized temporal values.
     * Free-form values remain supported when neither value resembles a recognized format.
     *
     * @param from event starting details.
     * @param to event ending details.
     * @throws YqrException if the range is invalid or uses inconsistent formats.
     */
    public static void validate(String from, String to) throws YqrException {
        if (from.equalsIgnoreCase(to)) {
            throw new YqrException("Event start must be earlier than its end");
        }
        if (matchesEither(from, to, DATE_TIME_SHAPE)) {
            requireBothFormats(from, to, DATE_TIME_SHAPE, "yyyy-MM-dd HH:mm");
            try {
                LocalDateTime start = LocalDateTime.parse(from.replace('T', ' '), DATE_TIME_FORMAT);
                LocalDateTime end = LocalDateTime.parse(to.replace('T', ' '), DATE_TIME_FORMAT);
                requireIncreasing(start.compareTo(end));
            } catch (DateTimeParseException e) {
                throw new YqrException("Please input valid event times in yyyy-MM-dd HH:mm format");
            }
            return;
        }
        if (matchesEither(from, to, DATE_SHAPE)) {
            requireBothFormats(from, to, DATE_SHAPE, "yyyy-MM-dd");
            try {
                requireIncreasing(LocalDate.parse(from).compareTo(LocalDate.parse(to)));
            } catch (DateTimeParseException e) {
                throw new YqrException("Please input valid event dates in yyyy-MM-dd format");
            }
            return;
        }
        if (matchesEither(from, to, TIME_SHAPE)) {
            requireBothFormats(from, to, TIME_SHAPE, "HH:mm");
            try {
                requireIncreasing(LocalTime.parse(from).compareTo(LocalTime.parse(to)));
            } catch (DateTimeParseException e) {
                throw new YqrException("Please input valid event times in HH:mm format");
            }
        }
    }

    /** Returns whether either value has the shape described by a pattern. */
    private static boolean matchesEither(String first, String second, Pattern pattern) {
        return pattern.matcher(first).matches() || pattern.matcher(second).matches();
    }

    /** Requires both event values to use the same recognized format. */
    private static void requireBothFormats(String from, String to, Pattern pattern, String format)
            throws YqrException {
        if (!pattern.matcher(from).matches() || !pattern.matcher(to).matches()) {
            throw new YqrException("Please use " + format + " for both event times");
        }
    }

    /** Requires the start value to compare before the end value. */
    private static void requireIncreasing(int comparison) throws YqrException {
        if (comparison >= 0) {
            throw new YqrException("Event start must be earlier than its end");
        }
    }
}
