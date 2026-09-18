package herta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests the date/time input parsing used by deadline and event commands.
 */
class DateTimeParserTest {

    @Test
    void parseUserDateTime_supportedFormats_returnsExpectedDateTime() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals(expected, DateTimeParser.parseUserDateTime("2/12/2019 1800"));
        assertEquals(expected, DateTimeParser.parseUserDateTime("2019-12-02 1800"));
        assertEquals(expected, DateTimeParser.parseUserDateTime("2019-12-02 18:00"));
    }

    @Test
    void parseUserDateTime_dateOnlyInput_returnsStartOfDay() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 0, 0);

        assertEquals(expected, DateTimeParser.parseUserDateTime("2019-12-02"));
        assertEquals(expected, DateTimeParser.parseUserDateTime("2/12/2019"));
    }

    @Test
    void parseUserDateTime_impossibleDateOnlyValues_throwDateTimeParseException() {
        String[] invalidInputs = {"31/02/2019", "2019-02-30"};

        for (String input : invalidInputs) {
            assertThrows(DateTimeParseException.class, () ->
                    DateTimeParser.parseUserDateTime(input));
        }
    }

    @Test
    void parseUserDateTime_malformedDateOnlyValues_throwDateTimeParseException() {
        String[] invalidInputs = {"2-12-2019", "2019/12/02", "2/12/19", "2019-12-02 trailing"};

        for (String input : invalidInputs) {
            assertThrows(DateTimeParseException.class, () ->
                    DateTimeParser.parseUserDateTime(input));
        }
    }

    @Test
    void parseUserDateTime_surroundingWhitespace_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseUserDateTime("  2/12/2019 1800  "));
    }

    @Test
    void parseUserDateTime_repeatedHorizontalWhitespace_returnsExpectedDateTime() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals(expected, DateTimeParser.parseUserDateTime("2/12/2019\t  1800"));
    }

    @Test
    void parseUserDateTime_invalidInput_throwsDateTimeParseException() {
        String[] invalidInputs = {
            "31/02/2019 1800",
            "2019-10-15 2460",
            "2019-10-15 18:60",
            "not a date",
            ""
        };

        for (String input : invalidInputs) {
            assertThrows(DateTimeParseException.class, () ->
                    DateTimeParser.parseUserDateTime(input));
        }
    }

    @Test
    void parseUserDate_supportedDateFormats_returnsExpectedDate() {
        LocalDate expected = LocalDate.of(2019, 12, 2);

        assertEquals(expected, DateTimeParser.parseUserDate("2019-12-02"));
        assertEquals(expected, DateTimeParser.parseUserDate("2/12/2019"));
    }

    @Test
    void parseUserDate_surroundingWhitespace_returnsExpectedDate() {
        assertEquals(LocalDate.of(2019, 12, 2),
                DateTimeParser.parseUserDate("  2/12/2019  "));
    }

    @Test
    void parseUserDate_invalidInput_throwsDateTimeParseException() {
        String[] invalidInputs = {"31/02/2019", "2019-02-30", "2019-12-02 1800", "not a date"};

        for (String input : invalidInputs) {
            assertThrows(DateTimeParseException.class, () ->
                    DateTimeParser.parseUserDate(input));
        }
    }

    @Test
    void parseStoredDateTime_datetimeInput_returnsExpectedDateTime() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals(expected, DateTimeParser.parseStoredDateTime("2019-12-02T18:00:00"));
    }

    @Test
    void parseStoredDateTime_dateOnlyInput_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0),
                DateTimeParser.parseStoredDateTime("2019-12-02"));
    }

    @Test
    void parseStoredDateTime_surroundingWhitespace_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseStoredDateTime("  2019-12-02T18:00:00  "));
    }

    @Test
    void parseStoredDateTime_invalidInput_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () ->
                DateTimeParser.parseStoredDateTime("2019-02-30T18:00:00"));
    }

    @Test
    void parseStoredDateTime_slashDateOnlyInput_remainsRejected() {
        assertThrows(DateTimeParseException.class, () ->
                DateTimeParser.parseStoredDateTime("2/12/2019"));
    }

    @Test
    void formatForStorage_dateTime_returnsIsoLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0, 5);

        assertEquals("2019-12-02T18:00:05", DateTimeParser.formatForStorage(dateTime));
    }

    @Test
    void formatForDisplay_midnight_returnsDateOnlyFormat() {
        assertEquals("Dec 02 2019",
                DateTimeParser.formatForDisplay(LocalDateTime.of(2019, 12, 2, 0, 0)));
    }

    @Test
    void formatForDisplay_nonMidnight_returnsDateTimeFormat() {
        assertEquals("Dec 02 2019, 6:05 PM",
                DateTimeParser.formatForDisplay(LocalDateTime.of(2019, 12, 2, 18, 5)));
    }

    @Test
    void formatDateForDisplay_date_returnsReadableDate() {
        assertEquals("Dec 02 2019",
                DateTimeParser.formatDateForDisplay(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void validateSupportedDateTime_boundaryYears_acceptsSupportedValues() {
        DateTimeParser.validateSupportedDateTime(LocalDateTime.of(1, 1, 1, 0, 0));
        DateTimeParser.validateSupportedDateTime(LocalDateTime.of(9999, 12, 31, 23, 59));
    }

    @Test
    void validateSupportedDateTime_unsupportedYears_rejectsWithStableMessage() {
        String expectedMessage = "Dates must be between 0001-01-01 and 9999-12-31.";

        IllegalArgumentException belowMinimum = assertThrows(IllegalArgumentException.class, () ->
                DateTimeParser.validateSupportedDateTime(LocalDateTime.of(0, 1, 1, 0, 0)));
        IllegalArgumentException aboveMaximum = assertThrows(IllegalArgumentException.class, () ->
                DateTimeParser.validateSupportedDateTime(LocalDateTime.of(10000, 1, 1, 0, 0)));

        assertEquals(expectedMessage, belowMinimum.getMessage());
        assertEquals(expectedMessage, aboveMaximum.getMessage());
    }

    @Test
    void validateSupportedDateTime_nullValue_rejectsWithDocumentedException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                DateTimeParser.validateSupportedDateTime(null));

        assertEquals("A date/time cannot be null.", exception.getMessage());
    }

    @Test
    void parseDates_supportedBoundaryYears_areAccepted() {
        assertEquals(LocalDate.of(1, 1, 1), DateTimeParser.parseUserDate("0001-01-01"));
        assertEquals(LocalDate.of(9999, 12, 31), DateTimeParser.parseUserDate("9999-12-31"));
        assertEquals(LocalDateTime.of(1, 1, 1, 0, 0),
                DateTimeParser.parseStoredDateTime("0001-01-01"));
    }

    @Test
    void parseDates_outOfRangeOrNullValues_areRejected() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserDate("0000-01-01"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserDate("10000-01-01"));
        assertThrows(NullPointerException.class, () -> DateTimeParser.parseUserDate(null));
        assertThrows(NullPointerException.class, () -> DateTimeParser.parseUserDateTime(null));
        assertThrows(NullPointerException.class, () -> DateTimeParser.parseStoredDateTime(null));
    }

    @Test
    void parseUserDate_strictLeapYearRules_acceptOnlyRealLeapDays() {
        assertEquals(LocalDate.of(2000, 2, 29), DateTimeParser.parseUserDate("2000-02-29"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserDate("1900-02-29"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parseUserDate("2019-02-29"));
    }

    @Test
    void formatForDisplay_nonEnglishDefaultLocale_remainsEnglish() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINA);

            assertEquals("Dec 02 2019, 6:05 PM",
                    DateTimeParser.formatForDisplay(LocalDateTime.of(2019, 12, 2, 18, 5)));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
