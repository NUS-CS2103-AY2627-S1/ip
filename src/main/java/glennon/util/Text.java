package glennon.util;

/**
 * Normalizes surrounding whitespace consistently for user-entered mission descriptions.
 */
public final class Text {
    /** Prevents creation of this stateless utility class. */
    private Text() {
    }

    /**
     * Removes surrounding Unicode whitespace, including non-breaking spaces,
     * while preserving all characters within the text.
     *
     * @param text non-null text to trim.
     * @return text without leading or trailing whitespace or space characters.
     */
    public static String stripWhitespace(String text) {
        int start = 0;
        int end = text.length();
        while (start < end && isWhitespace(text.codePointAt(start))) {
            start += Character.charCount(text.codePointAt(start));
        }
        while (end > start && isWhitespace(text.codePointBefore(end))) {
            end -= Character.charCount(text.codePointBefore(end));
        }
        return text.substring(start, end);
    }

    /**
     * Includes the Unicode space characters omitted by {@link Character#isWhitespace(int)}.
     */
    private static boolean isWhitespace(int character) {
        return Character.isWhitespace(character) || Character.isSpaceChar(character);
    }
}
