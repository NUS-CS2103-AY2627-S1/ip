package luckynoslacky.luckyparser;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckynoslacky.luckyexception.LuckyNoInputException;

/**
 * Converts supported natural-language duration phrases into numeric components.
 */
final class NaturalLanguageDurationNormalizer {
    private static final Pattern HALF_UNIT_PATTERN = Pattern.compile(
            "\\bhalf\\s+(?:a|an)\\s+"
                    + "(minutes?|min(?:s)?|hours?|h(?:r)?s?|days?|d(?:s)?|"
                    + "weeks?|months?|mo(?:s)?|years?|yr(?:s)?)\\b",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern WORD_NUMBER_PATTERN = Pattern.compile(
            "\\b(a|an|one|two|three|four|five|six|seven|eight|nine|ten)"
                    + "(?:\\s+more)?\\s+(minutes?|hours?|days?|weeks?|"
                    + "months?|years?)\\b",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern NUMERIC_MORE_PATTERN = Pattern.compile(
            "\\b(-?\\d+(?:\\.\\d+)?)\\s+more\\s+(minutes?|hours?|days?|"
                    + "weeks?|months?|years?)\\b",
            Pattern.CASE_INSENSITIVE);

    private NaturalLanguageDurationNormalizer() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Normalizes the supported natural-language duration forms.
     *
     * @param durationText duration text supplied by the user
     * @return duration text in the numeric format understood by the parser
     * @throws LuckyNoInputException if a duration unit is unsupported
     */
    static String normalize(String durationText) throws LuckyNoInputException {
        String normalizedText = normalizeHalfUnits(durationText);
        normalizedText = normalizeNumberWords(normalizedText);
        return normalizeNumericMore(normalizedText);
    }

    /**
     * Converts supported half-unit phrases into numeric components.
     *
     * @param durationText duration text to normalize
     * @return text with half-unit phrases replaced
     * @throws LuckyNoInputException if a half-unit alias is unsupported
     */
    private static String normalizeHalfUnits(String durationText)
            throws LuckyNoInputException {
        Matcher matcher = HALF_UNIT_PATTERN.matcher(durationText);
        StringBuffer normalizedText = new StringBuffer();

        while (matcher.find()) {
            DurationPeriodUnit unit = DurationPeriodUnit.fromText(
                    matcher.group(1), durationText);
            String replacement = switch (unit) {
                case MINUTE -> "0.5 minutes";
                case HOUR -> "0.5 hours";
                case DAY -> "0.5 days";
                case WEEK -> "3 days 12 hours";
                case MONTH -> "0.5 months";
                case YEAR -> "0.5 years";
            };
            matcher.appendReplacement(
                    normalizedText, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(normalizedText);
        return normalizedText.toString();
    }

    /**
     * Converts supported number-word components into numeric components.
     *
     * @param durationText duration text to normalize
     * @return text with number-word components replaced
     */
    private static String normalizeNumberWords(String durationText) {
        Matcher matcher = WORD_NUMBER_PATTERN.matcher(durationText);
        StringBuffer normalizedText = new StringBuffer();

        while (matcher.find()) {
            String number = numberWordValue(matcher.group(1));
            String replacement = number + " " + matcher.group(2);
            matcher.appendReplacement(
                    normalizedText, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(normalizedText);
        return normalizedText.toString();
    }

    /**
     * Removes the optional filler word from numeric components.
     *
     * @param durationText duration text to normalize
     * @return text with numeric filler words removed
     */
    private static String normalizeNumericMore(String durationText) {
        Matcher matcher = NUMERIC_MORE_PATTERN.matcher(durationText);
        StringBuffer normalizedText = new StringBuffer();

        while (matcher.find()) {
            String replacement = matcher.group(1) + " " + matcher.group(2);
            matcher.appendReplacement(
                    normalizedText, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(normalizedText);
        return normalizedText.toString();
    }

    /**
     * Converts a supported number word or article into its numeric value.
     *
     * @param numberWord number word or article
     * @return numeric value represented by the word
     */
    private static String numberWordValue(String numberWord) {
        return switch (numberWord.toLowerCase(Locale.ROOT)) {
            case "a", "an", "one" -> "1";
            case "two" -> "2";
            case "three" -> "3";
            case "four" -> "4";
            case "five" -> "5";
            case "six" -> "6";
            case "seven" -> "7";
            case "eight" -> "8";
            case "nine" -> "9";
            case "ten" -> "10";
            default -> numberWord;
        };
    }
}
