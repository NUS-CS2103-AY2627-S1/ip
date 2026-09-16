package panda.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class DateTimeUtilTest {

    @Test
    void parse_dateTimeWithSlashAndTime_returnsLocalDateTime() {
        LocalDateTime actual = DateTimeUtil.parse("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), actual);
    }

    @Test
    void parse_isoDateTimeWithSpaces_returnsLocalDateTime() {
        LocalDateTime actual = DateTimeUtil.parse("2019-10-16 1400");

        assertEquals(LocalDateTime.of(2019, 10, 16, 14, 0), actual);
    }

    @Test
    void parse_dateOnly_defaultsToMidnight() {
        LocalDateTime actual = DateTimeUtil.parse("2019-10-16");

        assertEquals(LocalDateTime.of(2019, 10, 16, 0, 0), actual);
    }

    @Test
    void parse_amPmTimeWithOptionalMinutes_returnsLocalDateTime() {
        LocalDateTime actual = DateTimeUtil.parse("2/12/2019 6:00 PM");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), actual);
    }

    @Test
    void parse_blankString_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("   "));
    }

    @Test
    void parse_unsupportedFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.parse("Monday"));
    }

    @Test
    void formatForDisplay_midnightValue_omitsTimeComponent() {
        String actual = DateTimeUtil.formatForDisplay(LocalDateTime.of(2019, 12, 2, 0, 0));

        assertEquals("Dec 02 2019", actual);
    }

    @Test
    void formatForDisplay_nonMidnightValue_includesTimeComponent() {
        String actual = DateTimeUtil.formatForDisplay(LocalDateTime.of(2019, 10, 16, 14, 0));

        assertEquals("Oct 16 2019 2:00 PM", actual);
    }

    @Test
    void formatForStorage_roundTripsToIsoLocalDateTime() {
        LocalDateTime value = LocalDateTime.of(2019, 10, 16, 14, 0);

        assertEquals("2019-10-16T14:00:00", DateTimeUtil.formatForStorage(value));
    }
}
