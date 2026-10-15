package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

class EventTest {
    @Test
    void constructor_parsesStartAndEndDateTimes() throws ThomasException {
        Event event = new Event("meeting", "2026-08-26 0900", "2026-08-27 1030");

        assertEquals(LocalDate.of(2026, 8, 26), event.getFromDate());
        assertEquals(LocalTime.of(9, 0), event.getFromTime());
        assertEquals(LocalDate.of(2026, 8, 27), event.getToDate());
        assertEquals(LocalTime.of(10, 30), event.getToTime());
    }

    @Test
    void constructor_endBeforeStart_throwsException() {
        assertThrows(ThomasException.class, () ->
                new Event("meeting", "2026-08-27", "2026-08-26"));
    }

    @Test
    void constructor_sameOrEarlierEndTime_throwsException() {
        assertThrows(ThomasException.class, () ->
                new Event("meeting", "2026-08-26 1000", "2026-08-26 1000"));
        assertThrows(ThomasException.class, () ->
                new Event("meeting", "2026-08-26 1100", "2026-08-26 1000"));
        assertThrows(ThomasException.class, () ->
                new Event("meeting", "2026-08-26", "2026-08-26"));
    }

    @Test
    void occursOn_dateWithinInclusiveRange_returnsTrue() throws ThomasException {
        Event event = new Event("conference", "2026-08-26", "2026-08-28");

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 26)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 27)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 28)));
    }

    @Test
    void occursOn_dateOutsideRange_returnsFalse() throws ThomasException {
        Event event = new Event("conference", "2026-08-26", "2026-08-28");

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 25)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 29)));
    }

    @Test
    void toFileFormat_containsEventData() throws ThomasException {
        Event event = new Event("conference", "2026-08-26 0900", "2026-08-26 1030");

        assertEquals("E | N | conference | 2026-08-26 0900 | 2026-08-26 1030", event.toFileFormat());
    }
}
