package wodan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import wodan.WodanException;

/**
 * Unit tests for {@link TaskDateTime} parsing and formatting.
 */
public class TaskDateTimeTest {
    @Test
    public void parse_isoDate_dateOnlyAtMidnight() throws WodanException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2), value.toLocalDate());
        assertFalse(value.hasTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), value.toLocalDateTime());
    }

    @Test
    public void parse_dayFirstDate_secondOfDecember() throws WodanException {
        TaskDateTime value = TaskDateTime.parse("2/12/2019");
        assertEquals(LocalDate.of(2019, 12, 2), value.toLocalDate());
        assertFalse(value.hasTime());
    }

    @Test
    public void parse_isoDateTimeHHmm_hasTime() throws WodanException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02 1800");
        assertTrue(value.hasTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), value.toLocalDateTime());
    }

    @Test
    public void parse_dayFirstDateTimeColon_hasTime() throws WodanException {
        TaskDateTime value = TaskDateTime.parse("2/12/2019 18:00");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), value.toLocalDateTime());
    }

    @Test
    public void parse_storageIsoT_hasTime() throws WodanException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02T18:00");
        assertTrue(value.hasTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), value.toLocalDateTime());
    }

    @Test
    public void parse_invalidDate_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> TaskDateTime.parse(
                "2019-02-31"));
        assertTrue(ex.getMessage().contains("2019-02-31"));
    }

    @Test
    public void parse_monthOutOfRangeInDayFirst_exceptionThrown() {
        assertThrows(WodanException.class, () -> TaskDateTime.parse("08/26/2019"));
    }

    @Test
    public void parse_unreadableText_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> TaskDateTime.parse(
                "not-a-date"));
        assertEquals(
                "The ravens cannot read 'not-a-date'. Try: " + TaskDateTime.HINT,
                ex.getMessage());
    }

    @Test
    public void parseStorage_validAndInvalid_valueOrNull() {
        assertEquals(LocalDate.of(2019, 6, 6),
                TaskDateTime.parseStorage("2019-06-06").toLocalDate());
        assertNull(TaskDateTime.parseStorage("garbage"));
    }

    @Test
    public void toDisplayString_dateOnlyAndWithTime_englishLowercaseAmPm() throws WodanException {
        assertEquals("Dec 02 2019", TaskDateTime.parse("2019-12-02").toDisplayString());
        assertEquals("Dec 02 2019, 6:00pm",
                TaskDateTime.parse("2019-12-02 1800").toDisplayString());
        assertEquals("Dec 02 2019, 12:00am",
                TaskDateTime.parse("2019-12-02 0000").toDisplayString());
    }

    @Test
    public void toStorageString_dateOnlyAndWithTime_iso() throws WodanException {
        assertEquals("2019-12-02", TaskDateTime.parse("2019-12-02").toStorageString());
        assertEquals("2019-12-02T18:00",
                TaskDateTime.parse("2019-12-02 1800").toStorageString());
    }

    @Test
    public void formatDate_englishMonth() {
        assertEquals("Oct 15 2019", TaskDateTime.formatDate(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void parse_isoDateTimeWithColon_hasTime() throws WodanException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02 18:00");
        assertTrue(value.hasTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), value.toLocalDateTime());
    }

    @Test
    public void parse_dayFirstDateTimeHHmm_hasTime() throws WodanException {
        TaskDateTime value = TaskDateTime.parse("2/12/2019 1800");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), value.toLocalDateTime());
    }

    @Test
    public void hasSameValue_sameInstantAndTimeFlag() throws WodanException {
        TaskDateTime withTime = TaskDateTime.parse("2019-12-02 0000");
        TaskDateTime dateOnly = TaskDateTime.parse("2019-12-02");
        assertTrue(withTime.hasSameValue(TaskDateTime.parse("2019-12-02 0000")));
        assertFalse(withTime.hasSameValue(dateOnly));
        assertFalse(dateOnly.hasTime());
    }

    @Test
    public void parse_februaryThirty_exceptionThrown() {
        assertThrows(WodanException.class, () -> TaskDateTime.parse("2019-02-30"));
    }

    @Test
    public void parse_isoDateTimeWithSeconds_hasTime() throws WodanException {
        TaskDateTime value = TaskDateTime.parse("2019-12-02T18:00:00");
        assertTrue(value.hasTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), value.toLocalDateTime());
    }

    @Test
    public void parseStorage_invalidIsoT_null() {
        assertNull(TaskDateTime.parseStorage("2019-12-02Tnot-a-time"));
    }
}
