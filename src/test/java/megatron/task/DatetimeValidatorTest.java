package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import org.junit.jupiter.api.Test;

/**
 * Tests supported and invalid inputs for {@link DatetimeValidator}.
 */
class DatetimeValidatorTest {

    @Test
    void parseToLocalDateTime_isoDate_defaultsToMidnight() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 0, 0),
                DatetimeValidator.parseToLocalDateTime("2026-08-06"));
    }

    @Test
    void parseToLocalDateTime_compactTime_parsesTime() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                DatetimeValidator.parseToLocalDateTime("2026-08-06 1400"));
    }

    @Test
    void parseToLocalDateTime_colonTime_parsesTime() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                DatetimeValidator.parseToLocalDateTime("2026-08-06 14:05"));
    }

    @Test
    void parseToLocalDateTime_meridiemTime_parsesTime() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                DatetimeValidator.parseToLocalDateTime("2026-08-06 2pm"));
    }

    @Test
    void parseToLocalDateTime_meridiemColonTime_parsesTime() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                DatetimeValidator.parseToLocalDateTime("2026-08-06 2:05pm"));
    }

    @Test
    void parseToLocalDateTime_numericDate_parsesDate() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                DatetimeValidator.parseToLocalDateTime("6/8/2026 1400"));
    }

    @Test
    void parseToLocalDateTime_numericDateWithColonTime_parsesTime() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                DatetimeValidator.parseToLocalDateTime("6/8/2026 14:05"));
    }

    @Test
    void parseToLocalDateTime_numericDateWithMeridiemTime_parsesTime() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                DatetimeValidator.parseToLocalDateTime("6/8/2026 2:05pm"));
    }

    @Test
    void parseToLocalDateTime_numericDateOnly_defaultsToMidnight() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 0, 0),
                DatetimeValidator.parseToLocalDateTime("6/8/2026"));
    }

    @Test
    void parseToLocalDateTime_shortTextDate_parsesDate() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                DatetimeValidator.parseToLocalDateTime("Aug 6 2026 14:00"));
    }

    @Test
    void parseToLocalDateTime_longTextDate_parsesDate() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                DatetimeValidator.parseToLocalDateTime("August 6 2026 2pm"));
    }

    @Test
    void parseToLocalDateTime_dayFirstTextDate_parsesDate() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                DatetimeValidator.parseToLocalDateTime("6 Aug 2026 2:05pm"));
    }

    @Test
    void parseToLocalDateTime_dayFirstLongTextDate_defaultsToMidnight() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 0, 0),
                DatetimeValidator.parseToLocalDateTime("6 August 2026"));
    }

    @Test
    void parseToLocalDateTime_shortTextDateOnly_defaultsToMidnight() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 0, 0),
                DatetimeValidator.parseToLocalDateTime("Aug 6 2026"));
    }

    @Test
    void parseToLocalDateTime_dayFirstShortTextDate_parsesDate() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 0),
                DatetimeValidator.parseToLocalDateTime("6 Aug 2026 1400"));
    }

    @Test
    void parseToLocalDateTime_irregularCaseWhitespaceAndOrdinal_normalizesInput() {
        assertEquals(LocalDateTime.of(2026, 8, 6, 14, 5),
                DatetimeValidator.parseToLocalDateTime("  AUGUST   6th   2026   2:05 pm  "));
    }

    @Test
    void parseToLocalDateTime_shortTextDateWithoutYear_usesCurrentYear() {
        int currentYear = LocalDate.now().getYear();

        assertEquals(LocalDateTime.of(currentYear, 8, 6, 0, 0),
                DatetimeValidator.parseToLocalDateTime("Aug 6"));
    }

    @Test
    void parseToLocalDateTime_longTextDateWithoutYear_usesCurrentYear() {
        int currentYear = LocalDate.now().getYear();

        assertEquals(LocalDateTime.of(currentYear, 8, 6, 14, 0),
                DatetimeValidator.parseToLocalDateTime("August 6 2pm"));
    }

    @Test
    void parseToLocalDateTime_textDateWithoutYearWithWhitespace_usesCurrentYear() {
        int currentYear = LocalDate.now().getYear();

        assertEquals(LocalDateTime.of(currentYear, 8, 6, 14, 0),
                DatetimeValidator.parseToLocalDateTime("Aug 6 2 pm"));
    }

    @Test
    void parseToLocalDateTime_weekdayWithoutTime_usesNextOccurrenceAtMidnight() {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        assertEquals(nextMonday.atStartOfDay(), DatetimeValidator.parseToLocalDateTime("monday"));
    }

    @Test
    void parseToLocalDateTime_weekdayAbbreviationWithTime_usesNextOccurrence() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String weekdayAbbreviation = tomorrow.getDayOfWeek().name().substring(0, 3).toLowerCase();

        assertEquals(tomorrow.atTime(18, 0),
                DatetimeValidator.parseToLocalDateTime(weekdayAbbreviation + " 6pm"));
    }

    @Test
    void parseToLocalDateTime_weekdayWithInvalidTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DatetimeValidator.parseToLocalDateTime("monday 25:00"));
    }

    @Test
    void hasExplicitTime_dateOnly_returnsFalse() {
        assertFalse(DatetimeValidator.hasExplicitTime("2026-08-06"));
    }

    @Test
    void hasExplicitTime_dateTime_returnsTrue() {
        assertTrue(DatetimeValidator.hasExplicitTime("2026-08-06 1400"));
    }

    @Test
    void hasExplicitTime_weekdayWithTime_returnsTrue() {
        assertTrue(DatetimeValidator.hasExplicitTime("monday 6pm"));
    }

    @Test
    void hasExplicitTime_weekdayWithoutTime_returnsFalse() {
        assertFalse(DatetimeValidator.hasExplicitTime("monday"));
    }

    @Test
    void format_customPattern_returnsFormattedValue() {
        assertEquals("2026/08/06 14:05",
                DatetimeValidator.format(LocalDateTime.of(2026, 8, 6, 14, 5), "uuuu/MM/dd HH:mm"));
    }

    @Test
    void formatForUser_value_returnsDefaultUserFormat() {
        assertEquals("06 Aug 26, 1405hrs",
                DatetimeValidator.formatForUser(LocalDateTime.of(2026, 8, 6, 14, 5)));
    }

    @Test
    void parseToLocalDateTime_nullInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DatetimeValidator.parseToLocalDateTime(null));
    }

    @Test
    void parseToLocalDateTime_emptyInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DatetimeValidator.parseToLocalDateTime(""));
    }

    @Test
    void parseToLocalDateTime_whitespaceInput_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DatetimeValidator.parseToLocalDateTime("   "));
    }

    @Test
    void parseToLocalDateTime_impossibleDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DatetimeValidator.parseToLocalDateTime("31/4/2026"));
    }

    @Test
    void parseToLocalDateTime_nonLeapDay_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DatetimeValidator.parseToLocalDateTime("2025-02-29"));
    }

    @Test
    void parseToLocalDateTime_impossibleTime_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DatetimeValidator.parseToLocalDateTime("2026-08-06 2400"));
    }

    @Test
    void parseToLocalDateTime_unknownText_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DatetimeValidator.parseToLocalDateTime("not a date"));
    }
}
