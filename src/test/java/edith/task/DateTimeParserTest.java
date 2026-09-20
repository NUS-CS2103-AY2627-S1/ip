package edith.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the supported date and time parsing and display formats. */
class DateTimeParserTest {
    @Test
    void parse_isoDateTime_returnsSameDateTime() {
        LocalDateTime value = DateTimeParser.parse("2026-08-28T14:30");

        assertEquals(LocalDateTime.of(2026, 8, 28, 14, 30), value);
    }

    @Test
    void parse_dayMonthYearAndTime_returnsSameDateTime() {
        LocalDateTime value = DateTimeParser.parse("28/8/2026 1430");

        assertEquals(LocalDateTime.of(2026, 8, 28, 14, 30), value);
    }

    @Test
    void parse_dateOnly_returnsStartOfDay() {
        LocalDateTime value = DateTimeParser.parse("2026-08-28");

        assertEquals(LocalDate.of(2026, 8, 28).atStartOfDay(), value);
    }

    @Test
    void parse_invalidOrImpossibleDate_returnsNull() {
        assertNull(DateTimeParser.parse("not a date"));
        assertNull(DateTimeParser.parse("2026-02-30"));
        assertNull(DateTimeParser.parse("30/2/2026 1200"));
    }

    @Test
    void parseForSorting_monthDayWithOrdinal_usesCurrentYear() {
        LocalDateTime value = DateTimeParser.parseForSorting("June 6th");

        assertEquals(LocalDate.of(LocalDate.now().getYear(), 6, 6).atStartOfDay(), value);
    }

    @Test
    void parseForSorting_monthDayAndTimeWithOrdinal_usesCurrentYearAndTime() {
        LocalDateTime value = DateTimeParser.parseForSorting("June 6th 3pm");

        assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 6, 6, 15, 0), value);
    }

    @Test
    void parseForSorting_fullNaturalDateTime_preservesSpecifiedYear() {
        LocalDateTime value = DateTimeParser.parseForSorting("June 6th 2027 3pm");

        assertEquals(LocalDateTime.of(2027, 6, 6, 15, 0), value);
    }

    @Test
    void format_midnightOmitsTime() {
        assertEquals("Aug 28 2026", DateTimeParser.format(LocalDateTime.of(2026, 8, 28, 0, 0)));
    }

    @Test
    void format_nonMidnightIncludes12HourTime() {
        assertEquals("Aug 28 2026 2:05pm", DateTimeParser.format(LocalDateTime.of(2026, 8, 28, 14, 5)));
    }
}
