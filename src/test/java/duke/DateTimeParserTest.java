package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests the supported deadline date and time formats. */
class DateTimeParserTest {
    @Test
    void parse_isoDate_usesMidnight() {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                DateTimeParser.parse("2019-10-15"));
    }

    @Test
    void parse_dayMonthDateAndCompactTime_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("2/12/2019 1800"));
    }

    @Test
    void parse_colonTime_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 30),
                DateTimeParser.parse("2/12/2019 18:30"));
    }

    @Test
    void parse_impossibleDate_throwsException() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("2019-02-30"));
    }

    @Test
    void format_dateOnly_omitsMidnightTime() {
        assertEquals("Oct 15 2019",
                DateTimeParser.format(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    void format_dateWithTime_usesReadableAmPmTime() {
        assertEquals("Dec 2 2019 6:00 PM",
                DateTimeParser.format(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }
}
