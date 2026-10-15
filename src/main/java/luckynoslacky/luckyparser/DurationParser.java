package luckynoslacky.luckyparser;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckyresponse.LuckyNoQuips;
import luckynoslacky.luckytask.DurationPeriod;

/**
 * Parses the decimal and combined duration formats supported by the snooze
 * command.
 */
public final class DurationParser {
    private DurationParser() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Parses a non-negative duration and ignores trailing commentary unless it
     * contains a marker-like slash.
     *
     * @param durationText duration text to parse
     * @return parsed calendar and clock amounts
     * @throws LuckyNoInputException if the duration is malformed, negative, or
     *                               followed by marker-like slash text
     */
    public static DurationPeriod parse(String durationText)
            throws LuckyNoInputException {
        String inputText = String.valueOf(durationText);
        String normalizedText = normalizeInput(durationText, inputText);

        return DurationComponentParser.parse(normalizedText, inputText);
    }

    /**
     * Validates and normalizes the raw duration input.
     *
     * @param durationText raw duration text supplied by the user
     * @param inputText original duration text used in error messages
     * @return normalized duration text
     * @throws LuckyNoInputException if the input is blank or contains a slash
     */
    private static String normalizeInput(
            String durationText,
            String inputText)
            throws LuckyNoInputException {
        if (durationText == null || durationText.isBlank()) {
            throw invalidDurationFormat(inputText);
        }
        String normalizedText = durationText.trim();
        if (hasMarkerLikeSlash(normalizedText)) {
            throw invalidDurationFormat(inputText);
        }
        return NaturalLanguageDurationNormalizer.normalize(normalizedText);
    }

    /**
     * Checks whether text contains a slash that could be mistaken for a
     * command marker.
     *
     * @param text text to inspect
     * @return true if a slash is followed by a letter at a token boundary
     */
    private static boolean hasMarkerLikeSlash(String text) {
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) != '/') {
                continue;
            }
            boolean atTokenStart = index == 0
                    || Character.isWhitespace(text.charAt(index - 1));
            boolean followedByLetter = index + 1 < text.length()
                    && Character.isLetter(text.charAt(index + 1));
            if (atTokenStart && followedByLetter) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates the standard invalid-duration exception.
     *
     * @param durationText original duration text
     * @return invalid-duration exception
     */
    private static LuckyNoInputException invalidDurationFormat(
            String durationText) {
        return new LuckyNoInputException(
                LuckyNoQuips.invalidDurationMessage(durationText));
    }
}
