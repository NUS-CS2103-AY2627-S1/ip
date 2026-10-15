package ayre;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * A helper class with some useful methods for validating String input against a certain pattern or format.
 */
public class ValidationTools {
    // Regex for int validation via Gemini
    private static final Pattern NUMERIC_STRING = Pattern.compile("-?\\d+");
    // Strict version of default
    private static final DateTimeFormatter STRICT_ISO_LOCAL_DATE =
            DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT);

    /**
     * Tests whether the input String is numeric via regex.
     *
     * @param str Input String.
     * @return Whether the input String is numeric.
     */
    public static boolean isInvalidTaskIndex(String str) {
        return !NUMERIC_STRING.matcher(str).matches();
    }

    /**
     * Tests whether the input String is a valid ISO_LOCAL_DATE via strict DateTimeFormatter.
     * This prevents the DateTimeFormatter from trying to crush impossible dates into plausible ones.
     *
     * @param str Input String.
     * @return Whether the input String is a valid ISO_LOCAL_DATE.
     */
    public static boolean isInvalidIsoDate(String str) {
        try {
            LocalDate.parse(str, STRICT_ISO_LOCAL_DATE);
            return false;
        } catch (DateTimeParseException e) {
            return true;
        }
    }

    /**
     * Tests whether the input date Strings are arranged in chronological order.
     * Allows both dates to be on the same day.
     *
     * @param fromDate First date.
     * @param toDate Second date to validate against.
     * @return Whether the input date Strings are arranged in chronological order.
     */
    public static boolean isInvalidDatePair(String fromDate, String toDate) {
        LocalDate firstDate = LocalDate.parse(fromDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate secondDate = LocalDate.parse(toDate, DateTimeFormatter.ISO_LOCAL_DATE);
        return firstDate.isAfter(secondDate);
    }
}
