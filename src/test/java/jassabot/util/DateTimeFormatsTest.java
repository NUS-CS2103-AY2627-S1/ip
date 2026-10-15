package jassabot.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests user-facing and persistent date-time conversions.
 */
public class DateTimeFormatsTest {

    @Test
    public void formatForDisplay_midnight_omitsTime() {
        LocalDateTime midnight = LocalDateTime.of(2019, 12, 2, 0, 0);

        assertEquals("Dec 2 2019", DateTimeFormats.formatForDisplay(midnight));
    }

    @Test
    public void formatForDisplay_nonMidnight_usesFriendlyTwelveHourTime() {
        assertAll(() ->
                assertEquals("Dec 2 2019, 12:01 AM",
                        DateTimeFormats.formatForDisplay(
                                LocalDateTime.of(2019, 12, 2, 0, 1))), () ->
                assertEquals("Dec 2 2019, 12:00 PM",
                        DateTimeFormats.formatForDisplay(
                                LocalDateTime.of(2019, 12, 2, 12, 0))), () ->
                assertEquals("Dec 2 2019, 11:59 PM",
                        DateTimeFormats.formatForDisplay(
                                LocalDateTime.of(2019, 12, 2, 23, 59)))
        );
    }

    @Test
    public void formatAndParseStorageDateTime_validValue_roundTripsExactly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 2, 29, 18, 5);

        String storedValue = DateTimeFormats.formatForStorage(dateTime);

        assertAll(() ->
                assertEquals("2024-02-29T18:05", storedValue), () ->
                assertEquals(dateTime,
                        DateTimeFormats.parseStorageDateTime(storedValue))
        );
    }

    @Test
    public void parseStorageDateTime_malformedValue_throwsDateTimeParseException() {
        assertAll(() ->
                assertThrows(DateTimeParseException.class, () ->
                        DateTimeFormats.parseStorageDateTime("2024-02-29 18:05")), () ->
                assertThrows(DateTimeParseException.class, () ->
                        DateTimeFormats.parseStorageDateTime("2023-02-29T18:05")), () ->
                assertThrows(DateTimeParseException.class, () ->
                        DateTimeFormats.parseStorageDateTime("2024-02-29T24:00"))
        );
    }

    @Test
    public void parseUserDateTime_leapCenturyAndMonthEnds_acceptsRealDates() {
        assertEquals(LocalDateTime.of(2000, 2, 29, 0, 0), DateTimeFormats.parseUserDateTime("29/2/2000"));
        assertEquals(LocalDateTime.of(2024, 4, 30, 23, 59),
                DateTimeFormats.parseUserDateTime("2024-04-30 2359"));
        assertEquals(LocalDateTime.of(2024, 1, 1, 0, 0), DateTimeFormats.parseUserDateTime("1/1/2024 0000"));
    }

    @Test
    public void parseUserDateTime_invalidCalendarAndTimeBoundaries_rejectsValues() {
        for (String value : List.of("1900-02-29", "2100-02-29", "2024-04-31", "0/1/2024",
                "2024-00-01", "2024-01-00", "2024-01-01 2360", "2024-01-01 2400")) {
            assertThrows(DateTimeParseException.class, () -> DateTimeFormats.parseUserDateTime(value), value);
        }
    }

    @Test
    public void parseUserDateTime_unsupportedSyntax_rejectsWithoutGuessing() {
        for (String value : List.of(" 2024-02-29", "2024-02-29 ", "2024-02-29T18:00",
                "2024-02-29 18:00", "2024-02-29 180000", "2024/02/29", "tomorrow", "2024-02-29 extra")) {
            assertThrows(DateTimeParseException.class, () -> DateTimeFormats.parseUserDateTime(value), value);
        }
    }

    @Test
    public void parseStorageDateTime_userFormatsAndExtraPrecision_rejectsUnsupportedSyntax() {
        for (String value : List.of("2024-02-29", "29/2/2024", "2024-02-29T18:05:30",
                "2024-02-29T18:05Z", "2024-02-29T18:60", "2024-02-29T18:05 ")) {
            assertThrows(DateTimeParseException.class, () -> DateTimeFormats.parseStorageDateTime(value), value);
        }
    }
}
