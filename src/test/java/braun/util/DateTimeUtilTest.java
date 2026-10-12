package braun.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import braun.exception.BraunException;

/**
 * Unit tests for {@link DateTimeUtil} parsing and formatting capabilities.
 */
public class DateTimeUtilTest {

    @Test
    public void parse_validIsoDateTime_parsedSuccessfully() throws BraunException {
        DateTimeUtil.TaskDateTime result = DateTimeUtil.parse("2026-08-30 1700");
        assertEquals(LocalDate.of(2026, 8, 30), result.getDate());
        assertEquals(LocalTime.of(17, 0), result.getTime());
        assertTrue(result.hasTime());
        assertEquals("Aug 30 2026, 5:00PM", result.formatForDisplay());
        assertEquals("2026-08-30 1700", result.formatForFile());
    }

    @Test
    public void parse_validSlashFormatDateTime_parsedSuccessfully() throws BraunException {
        DateTimeUtil.TaskDateTime result = DateTimeUtil.parse("2/12/2026 1830");
        assertEquals(LocalDate.of(2026, 12, 2), result.getDate());
        assertEquals(LocalTime.of(18, 30), result.getTime());
        assertTrue(result.hasTime());
    }

    @Test
    public void parse_validDateOnly_parsedSuccessfully() throws BraunException {
        DateTimeUtil.TaskDateTime result = DateTimeUtil.parse("2026-08-30");
        assertEquals(LocalDate.of(2026, 8, 30), result.getDate());
        assertNull(result.getTime());
        assertFalse(result.hasTime());
        assertEquals("Aug 30 2026", result.formatForDisplay());
        assertEquals("2026-08-30", result.formatForFile());
    }

    @Test
    public void parse_timeWithDefaultDate_dateInferredCorrectly() throws BraunException {
        LocalDate fallback = LocalDate.of(2026, 9, 15);
        DateTimeUtil.TaskDateTime result = DateTimeUtil.parse("1400", fallback);
        assertEquals(fallback, result.getDate());
        assertEquals(LocalTime.of(14, 0), result.getTime());
    }

    @Test
    public void parse_invalidDateFormat_exceptionThrown() {
        assertThrows(BraunException.class, () -> DateTimeUtil.parse("invalid-date-string"));
    }

    @Test
    public void parse_emptyString_exceptionThrown() {
        assertThrows(BraunException.class, () -> DateTimeUtil.parse("   "));
    }

    @Test
    public void parseDate_validDate_success() throws BraunException {
        LocalDate date = DateTimeUtil.parseDate("2026-08-24");
        assertEquals(LocalDate.of(2026, 8, 24), date);
        assertEquals("Aug 24 2026", DateTimeUtil.formatDate(date));
    }

    @Test
    public void parseDate_invalidFormat_exceptionThrown() {
        assertThrows(BraunException.class, () -> DateTimeUtil.parseDate("not-a-date"));
    }

    @Test
    public void taskDateTime_isBefore_correctComparison() throws BraunException {
        DateTimeUtil.TaskDateTime earlier = DateTimeUtil.parse("2026-08-24 1400");
        DateTimeUtil.TaskDateTime later = DateTimeUtil.parse("2026-08-24 1600");
        DateTimeUtil.TaskDateTime nextDay = DateTimeUtil.parse("2026-08-25 0900");

        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
        assertTrue(earlier.isBefore(nextDay));
        assertFalse(nextDay.isBefore(earlier));
    }
}
