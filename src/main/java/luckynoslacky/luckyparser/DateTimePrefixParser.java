package luckynoslacky.luckyparser;

import java.time.LocalDateTime;
import java.util.Arrays;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Parses date/time prefixes embedded in command arguments.
 *
 * <p>Timed commands allow plain commentary after a valid date/time expression.
 * This class progressively tries shorter prefixes and leaves the actual
 * date/time grammar to {@link DateTimeParser}.</p>
 */
final class DateTimePrefixParser {
    private final DateTimeParser dateTimeParser;

    /**
     * Creates a prefix parser backed by the supplied date/time parser.
     *
     * @param dateTimeParser parser used for date/time grammar
     */
    DateTimePrefixParser(DateTimeParser dateTimeParser) {
        if (dateTimeParser == null) {
            throw new IllegalArgumentException("Date-time parser cannot be null.");
        }
        this.dateTimeParser = dateTimeParser;
    }

    /**
     * Parses an end datetime while ignoring trailing commentary.
     *
     * @param dateTimeText datetime text
     * @return parsed datetime
     * @throws LuckyNoInputException if no prefix is a valid datetime
     */
    LocalDateTime parseEndDateTimeIgnoringTrailingText(String dateTimeText)
            throws LuckyNoInputException {
        return parseDateTimePrefix(dateTimeText, null, false);
    }

    /**
     * Parses an event end datetime relative to its new start time.
     *
     * @param dateTimeText datetime text
     * @param referenceDateTime reference for the event end
     * @return parsed datetime
     * @throws LuckyNoInputException if no prefix is a valid datetime
     */
    LocalDateTime parseEndDateTimeIgnoringTrailingText(
            String dateTimeText, LocalDateTime referenceDateTime)
            throws LuckyNoInputException {
        return parseDateTimePrefix(dateTimeText, referenceDateTime, false);
    }

    /**
     * Parses an event start datetime while ignoring trailing commentary.
     *
     * @param dateTimeText datetime text
     * @return parsed datetime
     * @throws LuckyNoInputException if no prefix is a valid datetime
     */
    LocalDateTime parseStartDateTimeIgnoringTrailingText(String dateTimeText)
            throws LuckyNoInputException {
        return parseDateTimePrefix(dateTimeText, null, true);
    }

    /**
     * Tries progressively shorter prefixes until one parses as a datetime.
     *
     * @param dateTimeText date/time text
     * @param referenceDateTime reference for an event end, or null
     * @param isStart whether the input is an event start
     * @return parsed datetime
     * @throws LuckyNoInputException if no prefix parses successfully
     */
    private LocalDateTime parseDateTimePrefix(
            String dateTimeText,
            LocalDateTime referenceDateTime,
            boolean isStart)
            throws LuckyNoInputException {
        String[] words = dateTimeText.trim().split("\\s+");
        for (int wordCount = words.length; wordCount > 0; wordCount--) {
            String candidate = String.join(
                    " ", Arrays.copyOf(words, wordCount));
            try {
                DateTimeParser.ParsedDateTime parsed = isStart
                        ? dateTimeParser.parseStartDateTime(candidate)
                        : referenceDateTime == null
                        ? dateTimeParser.parseEndDateTime(candidate)
                        : dateTimeParser.parseEndDateTime(candidate, referenceDateTime);
                if (isStructuredTrailingText(words, wordCount)) {
                    continue;
                }
                return parsed.dateTime();
            } catch (LuckyNoInputException exception) {
                // Try a shorter prefix so valid trailing commentary is ignored.
            }
        }
        throw new LuckyNoInputException(LuckyNoQuips.invalidDateTimeMessage());
    }

    /**
     * Checks whether ignored suffix text looks like another date or time
     * component rather than plain commentary.
     *
     * @param words whitespace-separated date/time input words
     * @param parsedWordCount number of words in the parsed prefix
     * @return true if the suffix begins with a numeric component
     */
    private boolean isStructuredTrailingText(String[] words, int parsedWordCount) {
        if (parsedWordCount == words.length) {
            return false;
        }

        String firstTrailingWord = words[parsedWordCount];
        return !firstTrailingWord.isEmpty()
                && Character.isDigit(firstTrailingWord.charAt(0));
    }
}
