package ekud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import ekud.EkudException;

/**
 * Tests {@link TaskDateTime} parsing, display, and save formats.
 */
public class TaskDateTimeTest {
    @Test
    public void parse_isoDate_success() throws EkudException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2), value.toLocalDate());
        assertFalse(value.hasTime());
        assertEquals("Dec 02 2019", value.toDisplayString());
        assertEquals("2019-12-02", value.toSaveString());
    }

    @Test
    public void parse_slashDate_isDayMonthYear() throws EkudException {
        TaskDateTime value = TaskDateTime.parse("2/12/2019");
        assertEquals(LocalDate.of(2019, 12, 2), value.toLocalDate());
        assertEquals("2019-12-02", value.toSaveString());
    }

    @Test
    public void parse_isoDateWithHHmm_includesTime() throws EkudException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02 1800");
        assertTrue(value.hasTime());
        assertEquals(LocalTime.of(18, 0), value.toLocalTime());
        assertEquals("Dec 02 2019, 6:00 PM", value.toDisplayString());
        assertEquals("2019-12-02 1800", value.toSaveString());
    }

    @Test
    public void parse_slashDateWithColonTime_includesTime() throws EkudException {
        TaskDateTime value = TaskDateTime.parse("2/12/2019 18:00");
        assertEquals(LocalDate.of(2019, 12, 2), value.toLocalDate());
        assertEquals(LocalTime.of(18, 0), value.toLocalTime());
        assertEquals("2019-12-02 1800", value.toSaveString());
    }

    @Test
    public void occursOn_sameCalendarDate_returnsTrue() throws EkudException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02 1800");
        assertTrue(value.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(value.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void toDisplayDate_usesSameFormAsDateOnlyTasks() {
        assertEquals("Dec 02 2019", TaskDateTime.toDisplayDate(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void parse_blank_throwsEkudException() {
        assertThrows(EkudException.class, () -> TaskDateTime.parse(""));
        assertThrows(EkudException.class, () -> TaskDateTime.parse("   "));
        assertThrows(EkudException.class, () -> TaskDateTime.parse(null));
    }

    @Test
    public void parse_unsupportedFormat_throwsEkudException() {
        EkudException exception = assertThrows(EkudException.class, () -> TaskDateTime.parse("next Friday"));
        assertEquals(
                "Please use a date such as 2019-12-02 or 2/12/2019, optionally with a time (1800 or 18:00).",
                exception.getMessage());
    }

    @Test
    public void parse_isoDateWithColonTime_includesTime() throws EkudException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02 18:00");
        assertEquals(LocalTime.of(18, 0), value.toLocalTime());
        assertEquals("2019-12-02 1800", value.toSaveString());
    }

    @Test
    public void parse_slashDateWithHHmm_includesTime() throws EkudException {
        TaskDateTime value = TaskDateTime.parse("2/12/2019 1800");
        assertEquals(LocalDate.of(2019, 12, 2), value.toLocalDate());
        assertEquals(LocalTime.of(18, 0), value.toLocalTime());
    }

    @Test
    public void parse_february30_resolvesToLastValidDay() throws EkudException {
        // DateTimeFormatter uses SMART resolution, so 30 Feb becomes 28 Feb 2019.
        TaskDateTime value = TaskDateTime.parse("2019-02-30");
        assertEquals(LocalDate.of(2019, 2, 28), value.toLocalDate());
    }

    @Test
    public void parse_invalidTime_throwsEkudException() {
        assertThrows(EkudException.class, () -> TaskDateTime.parse("2019-12-02 2560"));
        assertThrows(EkudException.class, () -> TaskDateTime.parse("2019-12-02 25:00"));
    }

    @Test
    public void toLocalTime_dateOnly_returnsNull() throws EkudException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02");
        assertNull(value.toLocalTime());
    }
}
