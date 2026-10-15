package luckynoslacky.luckyparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckytask.DurationPeriod;

/**
 * Tests the duration formats accepted by the snooze command.
 */
class DurationParserTest {
    private static final String INVALID_DURATION_PREFIX =
            "Eh can you be more specific anot, what do you mean by \"";
    private static final String INVALID_DURATION_SUFFIX = "\" sia?";
    private static final String NEGATIVE_DURATION_MESSAGE =
            "Siao ah time where got negative one";
    private static final String DECIMAL_CALENDAR_DURATION_MESSAGE =
            "Paiseh bro... i cannot settle decimal values for years and months yet...";

    /** Verifies decimal fixed-length units become clock amounts. */
    @Test
    void parse_decimalFixedLengthUnits_returnsClockAmounts()
            throws LuckyNoInputException {
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofMinutes(90)),
                DurationParser.parse("1.5 hours"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofMinutes(1)),
                DurationParser.parse("1.0 minutes"));
        assertEquals(new DurationPeriod(Period.ofDays(1), Duration.ofHours(12)),
                DurationParser.parse("1.5 days"));
    }

    /** Verifies decimal calendar units use the dedicated user message. */
    @Test
    void parse_decimalCalendarUnits_throwsDedicatedInputException() {
        LuckyNoInputException monthException = assertThrows(
                LuckyNoInputException.class, () -> DurationParser.parse(
                        "1.5 months"));
        LuckyNoInputException yearException = assertThrows(
                LuckyNoInputException.class, () -> DurationParser.parse(
                        "1.5 years"));

        assertEquals(
                DECIMAL_CALENDAR_DURATION_MESSAGE,
                monthException.getMessage());
        assertEquals(monthException.getMessage(), yearException.getMessage());
    }

    /** Verifies integer month and year units become calendar amounts. */
    @Test
    void parse_integerCalendarUnits_returnsCalendarAmounts()
            throws LuckyNoInputException {
        assertEquals(new DurationPeriod(Period.ofMonths(2), Duration.ZERO),
                DurationParser.parse("2 months"));
        assertEquals(new DurationPeriod(Period.ofYears(3), Duration.ZERO),
                DurationParser.parse("3 years"));
    }

    /** Verifies abbreviated units map to their canonical duration units. */
    @Test
    void parse_abbreviatedUnits_returnsExpectedAmounts()
            throws LuckyNoInputException {
        assertEquals(new DurationPeriod(Period.ofYears(1), Duration.ZERO),
                DurationParser.parse("1yr"));
        assertEquals(new DurationPeriod(Period.ofMonths(1), Duration.ZERO),
                DurationParser.parse("1 mos"));
        assertEquals(new DurationPeriod(Period.ofMonths(1), Duration.ZERO),
                DurationParser.parse("1mo"));
        assertEquals(new DurationPeriod(Period.ofDays(1), Duration.ZERO),
                DurationParser.parse("1ds"));
        assertEquals(new DurationPeriod(Period.ofDays(1), Duration.ZERO),
                DurationParser.parse("1d"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofHours(1)),
                DurationParser.parse("1hrs"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofHours(1)),
                DurationParser.parse("1hr"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofHours(1)),
                DurationParser.parse("1h"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofMinutes(1)),
                DurationParser.parse("1 mins"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofMinutes(1)),
                DurationParser.parse("1min"));
    }

    /** Verifies abbreviated units accept optional whitespace and case changes. */
    @Test
    void parse_abbreviatedUnitsWithWhitespaceAndCase_returnsExpectedAmounts()
            throws LuckyNoInputException {
        assertEquals(new DurationPeriod(Period.ofMonths(1), Duration.ZERO),
                DurationParser.parse("1   MO"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofHours(1)),
                DurationParser.parse("1 H"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofHours(1)),
                DurationParser.parse("1 hr"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofMinutes(90)),
                DurationParser.parse("1.5 h"));
        assertEquals(new DurationPeriod(Period.ofDays(1), Duration.ofHours(12)),
                DurationParser.parse("1.5 d"));
    }

    /** Verifies abbreviated components can be combined in canonical order. */
    @Test
    void parse_combinedAbbreviatedUnits_returnsExpectedAmounts()
            throws LuckyNoInputException {
        assertEquals(
                new DurationPeriod(Period.of(1, 2, 3), Duration.ofHours(4)
                        .plusMinutes(30)),
                DurationParser.parse("1yr 2mos 3ds 4hrs 30mins"));
    }

    /** Verifies natural-language components become duration amounts. */
    @Test
    void parse_naturalLanguageComponents_returnsExpectedAmounts()
            throws LuckyNoInputException {
        assertEquals(new DurationPeriod(Period.ofDays(7), Duration.ZERO),
                DurationParser.parse("a week"));
        assertEquals(new DurationPeriod(Period.ofDays(7), Duration.ZERO),
                DurationParser.parse("one more week"));
        assertEquals(new DurationPeriod(Period.ofDays(7), Duration.ZERO),
                DurationParser.parse("1 more week"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofMinutes(30)),
                DurationParser.parse("half an hour"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofHours(12)),
                DurationParser.parse("half a day"));
        assertEquals(new DurationPeriod(Period.ofDays(3), Duration.ofHours(12)),
                DurationParser.parse("half a week"));
    }

    /** Verifies natural-language half-unit phrases accept duration aliases. */
    @Test
    void parse_naturalLanguageAliases_returnsExpectedAmounts()
            throws LuckyNoInputException {
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofMinutes(30)),
                DurationParser.parse("half an hr"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofHours(12)),
                DurationParser.parse("half a DS"));
        assertEquals(new DurationPeriod(Period.ofDays(3), Duration.ofHours(12)),
                DurationParser.parse("half a week"));
    }

    /** Verifies half-unit calendar aliases retain decimal-calendar validation. */
    @Test
    void parse_naturalLanguageCalendarAliases_throwsDedicatedInputException() {
        LuckyNoInputException monthException = assertThrows(
                LuckyNoInputException.class, () -> DurationParser.parse(
                        "half a mo"));
        LuckyNoInputException yearException = assertThrows(
                LuckyNoInputException.class, () -> DurationParser.parse(
                        "half an yr"));

        assertEquals(
                DECIMAL_CALENDAR_DURATION_MESSAGE,
                monthException.getMessage());
        assertEquals(monthException.getMessage(), yearException.getMessage());
    }

    /** Verifies natural-language components combine in canonical order. */
    @Test
    void parse_combinedNaturalLanguageComponents_returnsExpectedAmounts()
            throws LuckyNoInputException {
        assertEquals(
                new DurationPeriod(Period.ofMonths(1).plusDays(14),
                        Duration.ofHours(2)),
                DurationParser.parse("one month two weeks 2 hours"));
    }

    /** Verifies components are parsed in canonical order and combined. */
    @Test
    void parse_combinedUnits_returnsCalendarAndClockAmounts()
            throws LuckyNoInputException {
        assertEquals(
                new DurationPeriod(Period.of(1, 2, 3), Duration.ofHours(4)
                        .plusMinutes(30)),
                DurationParser.parse("1 year 2 months 3 days 4.5 hours"));
    }

    /** Verifies calendar amounts are applied before clock amounts. */
    @Test
    void addTo_monthEndAndLeapYearValues_usesCalendarFirst()
            throws LuckyNoInputException {
        DurationPeriod monthAndDay = DurationParser.parse("1 month 1 day");
        DurationPeriod monthAndHalfDay = DurationParser.parse("1 month 1.5 days");

        assertEquals(LocalDateTime.of(2026, 3, 1, 12, 0),
                monthAndDay.addTo(LocalDateTime.of(2026, 1, 31, 12, 0)));
        assertEquals(LocalDateTime.of(2028, 3, 1, 12, 0),
                monthAndDay.addTo(LocalDateTime.of(2028, 1, 31, 12, 0)));
        assertEquals(LocalDateTime.of(2026, 3, 2, 0, 0),
                monthAndHalfDay.addTo(LocalDateTime.of(2026, 1, 31, 12, 0)));
    }

    /** Verifies zero and trailing commentary are accepted. */
    @Test
    void parse_zeroAndTrailingText_returnsExpectedAmount()
            throws LuckyNoInputException {
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ZERO),
                DurationParser.parse("0 hours 0 minutes"));
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofMinutes(90)),
                DurationParser.parse("  1   HoUr 30 minutes please"));
    }

    /** Verifies ordinary slash text is accepted in trailing commentary. */
    @Test
    void parse_trailingTextWithOrdinarySlash_returnsExpectedAmount()
            throws LuckyNoInputException {
        assertEquals(new DurationPeriod(Period.ZERO, Duration.ofHours(2)),
                DurationParser.parse("2 hours see a/b results"));
    }

    /** Verifies marker-like slash text is rejected in trailing commentary. */
    @Test
    void parse_trailingTextWithMarkerLikeSlash_throwsInputException() {
        assertThrows(LuckyNoInputException.class, () ->
                DurationParser.parse("2 hours /later"));
    }

    /** Verifies negative values receive the dedicated error. */
    @Test
    void parse_negativeComponent_throwsDedicatedInputException() {
        LuckyNoInputException exception = assertThrows(
                LuckyNoInputException.class, () -> DurationParser.parse(
                        "1 hour -2 minutes"));

        assertEquals(NEGATIVE_DURATION_MESSAGE, exception.getMessage());
    }

    /** Verifies malformed duration forms display their exact generic message. */
    @Test
    void parse_malformedDurationForms_throwsExactInputException() {
        assertInvalidDuration("1.5 weeks");
        assertInvalidDuration("half");
        assertInvalidDuration("one and a half hours");
        assertInvalidDuration("1 hour 2 hours");
        assertInvalidDuration("2 days 1 month");
        assertInvalidDuration("1m");
        assertInvalidDuration("1h30min");
        assertInvalidDuration("2 fortnights");
        assertInvalidDuration("1 hour /to tomorrow");
    }

    /**
     * Verifies blank, malformed, and oversized values use their exact messages.
     *
     * @param description identifies the invalid input in the test output
     * @param durationText represents the duration text to parse
     * @param expectedMessage represents the complete expected error message
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidDurationInputs")
    void parse_invalidDurationInput_throwsExactInputException(
            String description,
            String durationText,
            String expectedMessage) {
        LuckyNoInputException exception = assertThrows(
                LuckyNoInputException.class, () -> DurationParser.parse(durationText));

        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Supplies malformed inputs and the full error text visible to the user.
     *
     * @return named malformed inputs and their complete error messages
     */
    private static Stream<Arguments> invalidDurationInputs() {
        return Stream.of(
                Arguments.of("null input", null, invalidDurationMessage("null")),
                Arguments.of("empty input", "", invalidDurationMessage("")),
                Arguments.of("whitespace input", "   ", invalidDurationMessage("   ")),
                Arguments.of("numeric remainder", "1 hour 2", invalidDurationMessage("1 hour 2")),
                Arguments.of("year integer overflow", "2147483648 years",
                        invalidDurationMessage("2147483648 years")),
                Arguments.of("clock conversion overflow", "999999999999 hours",
                        invalidDurationMessage("999999999999 hours")));
    }

    /** Verifies invalid duration errors include the original duration text. */
    @Test
    void parse_invalidDuration_includesOriginalInputInErrorMessage() {
        LuckyNoInputException exception = assertThrows(
                LuckyNoInputException.class, () -> DurationParser.parse("1h30min"));

        assertEquals(
                invalidDurationMessage("1h30min"),
                exception.getMessage());
    }

    /**
     * Asserts malformed duration text produces its full user-facing message.
     *
     * @param durationText represents the malformed duration text
     */
    private void assertInvalidDuration(String durationText) {
        LuckyNoInputException exception = assertThrows(
                LuckyNoInputException.class, () -> DurationParser.parse(durationText));

        assertEquals(invalidDurationMessage(durationText), exception.getMessage());
    }

    /**
     * Returns the full generic duration error independently from production code.
     *
     * @param durationText represents the invalid duration text quoted in the message
     * @return the full user-facing generic error message
     */
    private static String invalidDurationMessage(String durationText) {
        return INVALID_DURATION_PREFIX + durationText + INVALID_DURATION_SUFFIX;
    }
}
