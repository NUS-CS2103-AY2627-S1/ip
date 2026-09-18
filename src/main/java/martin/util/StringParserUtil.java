package martin.util;

import martin.exception.IllegalCommandException;

/**
 * Utility class for parsing strings and validating command inputs.
 */
public class StringParserUtil {

    /**
     * Returns a trimmed command value, rejecting an empty value with a clear
     * message.
     *
     * @param value    The string to trim and validate.
     * @param errorMsg The error message to throw if the value is empty.
     * @return The trimmed non-empty string.
     * @throws IllegalCommandException If the trimmed string is empty.
     */
    public static String requireValue(String value, String errorMsg) {
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalCommandException(errorMsg);
        }
        return trimmedValue;
    }

    /**
     * Returns the index of a substring, rejecting a missing substring with a clear
     * message.
     *
     * @param str      The string to search within.
     * @param substr   The delimiter or substring to search for.
     * @param errorMsg The error message to throw if the substring is not found.
     * @return The index of the substring.
     * @throws IllegalCommandException If the substring is not found.
     */
    public static int requireIndex(String str, String substr, String errorMsg) {
        int index = str.indexOf(substr);
        if (index < 0) {
            throw new IllegalCommandException(errorMsg);
        }
        return index;
    }

    /**
     * Checks that the fromIndex is before the toIndex, throwing an exception if
     * not.
     *
     * @param fromIndex
     * @param toIndex
     */
    public static void requireFromIndexBeforeToIndex(int fromIndex, int toIndex) {
        if (toIndex <= fromIndex) {
            throw new IllegalCommandException("An event needs a /from date that comes before the /to date.");
        }
    }
}
