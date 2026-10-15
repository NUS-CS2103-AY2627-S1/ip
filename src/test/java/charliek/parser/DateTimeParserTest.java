package charliek.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests the supported user-input cases handled by {@link DateTimeParser}.
 */
class DateTimeParserTest {
    /**
     * Verifies that a date-only input produces a date without a time.
     */
    @Test
    void parseUserInput_dateOnly_returnsDateWithoutTime() {
        DateTimeParser.ParsedDateTime result =
                DateTimeParser.parseUserInput("2/12/2019");

        assertEquals(LocalDate.of(2019, 12, 2), result.date());
        assertNull(result.dateTime());
        assertFalse(result.hasTime());
    }

    /**
     * Verifies that a date-time input produces the expected date and time.
     */
    @Test
    void parseUserInput_dateAndTime_returnsDateTime() {
        DateTimeParser.ParsedDateTime result =
                DateTimeParser.parseUserInput("2/12/2019 6pm");

        assertNull(result.date());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result.dateTime());
        assertTrue(result.hasTime());
    }

    /**
     * Verifies that surrounding and repeated whitespace is normalized.
     */
    @Test
    void parseUserInput_extraWhitespace_returnsNormalizedValue() {
        DateTimeParser.ParsedDateTime result =
                DateTimeParser.parseUserInput("  2/12/2019   6 PM  ");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result.dateTime());
    }

    /**
     * Verifies textual dates, case-insensitive month names, and seconds.
     */
    @Test
    void parseUserInput_textualAndSecondPrecisionForms_returnsDateTime() {
        DateTimeParser.ParsedDateTime textual =
                DateTimeParser.parseUserInput("december 2 2019 6:05 PM");
        DateTimeParser.ParsedDateTime numeric =
                DateTimeParser.parseUserInput("2019.12.2 23:05:06");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 5), textual.dateTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 23, 5, 6), numeric.dateTime());
    }

    /**
     * Verifies that invalid calendar dates and clock values are rejected strictly.
     */
    @Test
    void parseUserInput_invalidDateOrTime_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserInput("2019-02-30"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserInput("2019-12-02 25:00"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserInput("2019-12-02 13:60:00"));
    }

    /**
     * Verifies that null, blank, and unsupported inputs are rejected.
     */
    @Test
    void parseUserInput_invalidInput_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserInput(null));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserInput("   "));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserInput("31/02/2019"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserInput("not a date"));
    }

    /**
     * Verifies that stored ISO date and date-time values are restored correctly.
     */
    @Test
    void parseStored_isoValues_returnsCorrespondingDateOrDateTime() {
        DateTimeParser.ParsedDateTime date = DateTimeParser.parseStored(" 2019-12-02 ");
        DateTimeParser.ParsedDateTime dateTime =
                DateTimeParser.parseStored("2019-12-02T18:00:00");

        assertEquals(LocalDate.of(2019, 12, 2), date.date());
        assertNull(date.dateTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), dateTime.dateTime());
        assertNull(dateTime.date());
    }

    /**
     * Verifies that malformed stored values are rejected.
     */
    @Test
    void parseStored_invalidValue_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseStored("2019-02-30"));
    }

    /**
     * Verifies the user-facing formatting for both supported value shapes.
     */
    @Test
    void formatForDisplay_dateAndDateTime_returnsReadableValues() {
        assertEquals("2 Dec 2019",
                DateTimeParser.formatForDisplay(
                        DateTimeParser.ParsedDateTime.ofDate(LocalDate.of(2019, 12, 2))));
        assertEquals("2 Dec 2019, 18:05",
                DateTimeParser.formatForDisplay(
                        DateTimeParser.ParsedDateTime.ofDateTime(
                                LocalDateTime.of(2019, 12, 2, 18, 5))));
    }

    /**
     * Verifies the stable ISO formatting used for CSV persistence.
     */
    @Test
    void formatForStorage_dateAndDateTime_returnsIsoValues() {
        assertEquals("2019-12-02",
                DateTimeParser.formatForStorage(
                        DateTimeParser.ParsedDateTime.ofDate(LocalDate.of(2019, 12, 2))));
        assertEquals("2019-12-02T18:05:00",
                DateTimeParser.formatForStorage(
                        DateTimeParser.ParsedDateTime.ofDateTime(
                                LocalDateTime.of(2019, 12, 2, 18, 5))));
    }

    /**
     * Verifies that formatting methods reject absent values.
     */
    @Test
    void formatForDisplayOrStorage_nullValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.formatForDisplay(null));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.formatForStorage(null));
    }

    /**
     * Verifies the invariant that a parsed value contains exactly one date shape.
     */
    @Test
    void parsedDateTime_bothOrNeitherValues_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new DateTimeParser.ParsedDateTime(null, null));
        assertThrows(IllegalArgumentException.class, () -> new DateTimeParser.ParsedDateTime(
                LocalDate.of(2019, 12, 2), LocalDateTime.of(2019, 12, 2, 18, 0)));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.ParsedDateTime.ofDate(null));
        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.ParsedDateTime.ofDateTime(null));
    }

    /**
     * Verifies all documented date-only input families resolve to the same date.
     */
    @Test
    void parseUserInput_supportedDatePatterns_acceptEquivalentDates() {
        String[] inputs = {
            "2/12/2019", "2-12-2019", "2.12.2019", "2019-12-2", "2019/12/2", "2019.12.2",
            "2/12/19", "2-12-19", "2.12.19", "2 Dec 2019", "2 December 2019", "2-Dec-2019",
            "2-December-2019", "Dec 2 2019", "December 2 2019", "2019 Dec 2"
        };

        for (String input : inputs) {
            DateTimeParser.ParsedDateTime result = DateTimeParser.parseUserInput(input);
            assertEquals(LocalDate.of(2019, 12, 2), result.date(), input);
            assertFalse(result.hasTime(), input);
        }
    }

    /**
     * Verifies all documented clock input families resolve to the expected time.
     */
    @Test
    void parseUserInput_supportedTimePatterns_acceptEquivalentTimes() {
        String[] inputs = {
            "1830", "18:30", "8:30", "18:30:05", "8:30:05", "6pm", "6 pm", "6:30pm",
            "6:30 pm", "6:30:05pm", "6:30:05 pm", "6.30pm", "6.30 pm"
        };

        for (String input : inputs) {
            DateTimeParser.ParsedDateTime result =
                    DateTimeParser.parseUserInput("2019-12-2 " + input);
            int expectedHour = input.startsWith("8") ? 8 : 18;
            int expectedMinute = input.equals("1830") || input.contains(":") || input.contains(".") ? 30 : 0;
            assertEquals(LocalDateTime.of(2019, 12, 2, expectedHour, expectedMinute,
                    input.contains(":05") ? 5 : 0),
                    result.dateTime(), input);
            assertTrue(result.hasTime(), input);
        }
    }

    /**
     * Verifies that each supported date/time separator is accepted.
     */
    @Test
    void parseUserInput_supportedSeparators_acceptDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseUserInput("2019-12-2, 6pm").dateTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseUserInput("2019-12-2T6pm").dateTime());
    }

    /**
     * Verifies that stored values require canonical ISO date or date-time syntax.
     */
    @Test
    void parseStored_emptyOrNonIsoValue_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseStored(""));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseStored("2/12/2019"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseStored("2019-12-02 18:00"));
    }
}
