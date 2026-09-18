package oreo.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the date and time formats accepted by {@link DateTimeParser}. */
class DateTimeParserTest {

    @Test
    void parseDate_validIsoDate_returnsParsedDate() {
        assertEquals(LocalDate.of(2024, 2, 29), DateTimeParser.parseDate("2024-02-29"));
    }

    @Test
    void parseDate_invalidDate_throwsExpectedException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DateTimeParser.parseDate("2023-02-29"));

        assertEquals("Use a date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    void parse_slashDateTime_returnsParsedDateAndTime() {
        assertEquals(
                LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("2/12/2019 1800"));
    }

    @Test
    void parse_isoDate_returnsStartOfThatDate() {
        assertEquals(
                LocalDateTime.of(2019, 10, 15, 0, 0),
                DateTimeParser.parse("2019-10-15"));
    }

    @Test
    void parse_singleDigitSlashDateAndTime_returnsParsedValue() {
        assertEquals(
                LocalDateTime.of(2025, 1, 3, 9, 5),
                DateTimeParser.parse("3/1/2025 0905"));
    }

    @Test
    void parse_freeFormText_returnsNullForLegacyValues() {
        assertNull(DateTimeParser.parse("next Monday"));
        assertNull(DateTimeParser.parse(""));
    }

    @Test
    void parse_invalidCalendarDate_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DateTimeParser.parse("2019-02-29"));

        assertEquals("Invalid date or time: 2019-02-29", exception.getMessage());
    }

    @Test
    void parse_invalidTime_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DateTimeParser.parse("2/12/2019 2460"));
    }

    @Test
    void parse_wrongFormat_returnsNullInsteadOfParsingPartially() {
        assertNull(DateTimeParser.parse("2019/10/15 1800"));
        assertNull(DateTimeParser.parse("2019-1-15"));
    }

    @Test
    void format_midnightValue_returnsDateOnlyDisplay() {
        assertEquals(
                "Oct 15 2019",
                DateTimeParser.format(LocalDateTime.of(2019, 10, 15, 0, 0), "original"));
    }

    @Test
    void format_nonMidnightValue_returnsDateAndTimeDisplay() {
        assertEquals(
                "Oct 15 2019 6:05PM",
                DateTimeParser.format(LocalDateTime.of(2019, 10, 15, 18, 5), "original"));
    }

    @Test
    void format_nullValue_returnsOriginalFreeFormText() {
        assertEquals("next Monday", DateTimeParser.format(null, "next Monday"));
    }
}
