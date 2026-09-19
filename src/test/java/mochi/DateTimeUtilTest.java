package mochi;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing and formatting of date-time values used by Mochi.
 */
public class DateTimeUtilTest {
    @Test
    public void parse_validDateTime_returnsLocalDateTime() {
        LocalDateTime result = DateTimeUtil.parse("2019-12-02 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    public void parse_leapDayAtMidnight_returnsLocalDateTime() {
        LocalDateTime result = DateTimeUtil.parse("2024-02-29 0000");

        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), result);
    }

    @Test
    public void parse_invalidCalendarDates_exceptionThrown() {
        assertAll(
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeUtil.parse("2023-02-29 1200")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeUtil.parse("2024-04-31 1200")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeUtil.parse("2024-13-01 1200"))
        );
    }

    @Test
    public void parse_invalidTimes_exceptionThrown() {
        assertAll(
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeUtil.parse("2024-01-01 2400")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeUtil.parse("2024-01-01 1260"))
        );
    }

    @Test
    public void parse_incorrectFormats_exceptionThrown() {
        assertAll(
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeUtil.parse("02/12/2019 1800")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeUtil.parse("2019-12-02")),
                () -> assertThrows(DateTimeParseException.class,
                        () -> DateTimeUtil.parse("2019-12-02 18:00"))
        );
    }

    @Test
    public void formatForDisplay_differentTimes_returnsFriendlyTwelveHourFormat() {
        assertAll(
                () -> assertEquals("Jan 01 2024, 12:00 AM",
                        DateTimeUtil.formatForDisplay(LocalDateTime.of(2024, 1, 1, 0, 0))),
                () -> assertEquals("Jan 01 2024, 12:00 PM",
                        DateTimeUtil.formatForDisplay(LocalDateTime.of(2024, 1, 1, 12, 0))),
                () -> assertEquals("Dec 02 2019, 6:05 PM",
                        DateTimeUtil.formatForDisplay(LocalDateTime.of(2019, 12, 2, 18, 5)))
        );
    }

    @Test
    public void formatForStorage_dateTime_returnsStrictStorageFormat() {
        assertAll(
                () -> assertEquals("2019-12-02 1800",
                        DateTimeUtil.formatForStorage(LocalDateTime.of(2019, 12, 2, 18, 0))),
                () -> assertEquals("2024-02-09 0005",
                        DateTimeUtil.formatForStorage(LocalDateTime.of(2024, 2, 9, 0, 5)))
        );
    }

    @Test
    public void parse_storageFormattedValues_returnsOriginalDateTimes() {
        LocalDateTime firstDateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        LocalDateTime secondDateTime = LocalDateTime.of(2024, 2, 29, 0, 5);

        assertAll(
                () -> assertEquals(firstDateTime,
                        DateTimeUtil.parse(DateTimeUtil.formatForStorage(firstDateTime))),
                () -> assertEquals(secondDateTime,
                        DateTimeUtil.parse(DateTimeUtil.formatForStorage(secondDateTime)))
        );
    }
}
