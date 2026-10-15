package lenzabot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import lenzabot.LenZaBotException;

class DateTimeParserTest {
    @Test
    void parse_dayMonthYearWithTime_returnsDateTime() throws LenZaBotException {
        LocalDateTime result = DateTimeParser.parse("2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    void parse_isoDateWithTime_returnsDateTime() throws LenZaBotException {
        LocalDateTime result = DateTimeParser.parse("2019-12-02 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    void parse_dayMonthYearWithoutTime_returnsStartOfDay() throws LenZaBotException {
        LocalDateTime result = DateTimeParser.parse("2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), result);
    }

    @Test
    void parse_isoDateWithoutTime_returnsStartOfDay() throws LenZaBotException {
        LocalDateTime result = DateTimeParser.parse("2019-12-02");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), result);
    }

    @Test
    void parse_unsupportedFormat_throwsException() {
        assertThrows(LenZaBotException.class, () -> DateTimeParser.parse("Sunday evening"));
    }

    @Test
    void parse_impossibleDate_throwsException() {
        assertThrows(LenZaBotException.class, () -> DateTimeParser.parse("31/2/2020 1800"));
    }

    @Test
    void format_dateTime_returnsReadableText() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals("Dec 2 2019, 6:00 PM", DateTimeParser.format(dateTime));
    }
}
