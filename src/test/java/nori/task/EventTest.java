package nori.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import nori.NoriException;

/**
 * Tests event date validation and matching at interval boundaries.
 */
public class EventTest {

    @Test
    public void constructor_endBeforeStart_acceptsEvent() {
        assertDoesNotThrow(() -> new Event("backwards", "2026-09-03", "2026-09-01"));
    }

    @Test
    public void constructor_impossibleTime_acceptsEvent() {
        // An event saved before times were checked must still load from disk.
        assertDoesNotThrow(() -> new Event("old", "2026-10-01 25:00", "2026-10-02"));
    }

    @Test
    public void findTimeError_impossibleStartTime_reportsTheTime() {
        assertEquals(Optional.of("NOOT?! I cannot understand \"25:00\" as an event time."
                        + " Use a time like \"1400\" or \"2:30pm\"."),
                Event.findTimeError("2026-10-01 25:00", "2026-10-02"));
    }

    @Test
    public void findTimeError_impossibleEndTime_reportsTheTime() {
        assertEquals(Optional.of("NOOT?! I cannot understand \"2400\" as an event time."
                        + " Use a time like \"1400\" or \"2:30pm\"."),
                Event.findTimeError("2026-10-01 1000", "2026-10-01 2400"));
    }

    @Test
    public void findTimeError_realTimesOrNoTimes_returnsEmpty() {
        assertEquals(Optional.empty(), Event.findTimeError("2026-10-01 0930", "2026-10-01 11:45"));
        assertEquals(Optional.empty(), Event.findTimeError("Mon 2pm", "Tue 1pm"));
        assertEquals(Optional.empty(), Event.findTimeError("2026-10-01", "2026-10-02"));
        assertEquals(Optional.empty(), Event.findTimeError(null, null));
    }

    @Test
    public void constructor_impossibleDate_throwsNoriException() {
        assertThrows(NoriException.class, () ->
                new Event("impossible", "2019-02-31 1400", "2019-02-31 1500"));
    }

    @Test
    public void findOrderingError_endDateBeforeStartDate_reportsReversedDates() {
        assertEquals(Optional.of("NOOT?! An event cannot end before it starts."
                + " Time only waddles forward."),
                Event.findOrderingError("2026-09-03", "2026-09-01"));
    }

    @Test
    public void findOrderingError_bareTimesOutOfOrder_reportsReversedTimes() {
        assertEquals(Optional.of("NOOT?! That event ends at or before it starts."
                + " Time only waddles forward."),
                Event.findOrderingError("2pm", "1pm"));
    }

    @Test
    public void findOrderingError_sameDateTimesOutOfOrder_reportsReversedTimes() {
        assertEquals(Optional.of("NOOT?! That event ends at or before it starts."
                + " Time only waddles forward."),
                Event.findOrderingError("2019-06-06 1000", "2019-06-06 0900"));
    }

    @Test
    public void findOrderingError_endUndatedTimeBeforeStart_reportsReversedTimes() {
        assertEquals(Optional.of("NOOT?! That event ends at or before it starts."
                + " Time only waddles forward."),
                Event.findOrderingError("2019-06-06 1000", "0800"));
    }

    @Test
    public void findOrderingError_identicalTimes_reportsReversedTimes() {
        assertEquals(Optional.of("NOOT?! That event ends at or before it starts."
                + " Time only waddles forward."),
                Event.findOrderingError("0900", "0900"));
    }

    @Test
    public void findOrderingError_detailsNamingDaysInWords_returnsEmpty() {
        assertEquals(Optional.empty(), Event.findOrderingError("Mon 2pm", "Tue 1pm"));
        assertEquals(Optional.empty(), Event.findOrderingError("Mon 2pm", "1pm"));
        assertEquals(Optional.empty(), Event.findOrderingError("Aug 6th 2pm", "4pm"));
    }

    @Test
    public void findOrderingError_orderedDetails_returnsEmpty() {
        assertEquals(Optional.empty(), Event.findOrderingError("2pm", "4pm"));
        assertEquals(Optional.empty(), Event.findOrderingError("2019-06-06 1000", "1800"));
        assertEquals(Optional.empty(), Event.findOrderingError("2019-06-06", "2019-06-08"));
    }

    @Test
    public void findOrderingError_missingDetail_returnsEmpty() {
        assertEquals(Optional.empty(), Event.findOrderingError(null, "4pm"));
        assertEquals(Optional.empty(), Event.findOrderingError("2pm", null));
        assertEquals(Optional.empty(), Event.findOrderingError(null, null));
    }

    @Test
    public void findOrderingError_detailsWithoutTimes_returnsEmpty() {
        assertEquals(Optional.empty(), Event.findOrderingError("soon", "later"));
    }

    @Test
    public void constructor_sameDate_acceptsEvent() {
        assertDoesNotThrow(() -> new Event("workshop", "2026-09-03 0900", "2026-09-03 1700"));
    }

    @Test
    public void constructor_onlyStartHasDate_acceptsEvent() {
        assertDoesNotThrow(() -> new Event("meeting", "2026-09-03 0900", "later"));
    }

    @Test
    public void occursOn_multiDayEvent_matchesEverySpannedDate() throws NoriException {
        Event event = new Event("conference", "2026-09-01 0900", "2026-09-03 1700");

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 1)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 2)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 3)));
    }

    @Test
    public void occursOn_dateOutsideEvent_returnsFalse() throws NoriException {
        Event event = new Event("conference", "2026-09-01", "2026-09-03");

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 31)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 4)));
    }

    @Test
    public void occursOn_onlyEndHasDate_matchesThatDate() throws NoriException {
        Event event = new Event("release", "after testing", "2026-09-03 1700");

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 3)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 2)));
    }

    @Test
    public void occursOn_undatedEvent_returnsFalse() throws NoriException {
        Event event = new Event("lunch", "Monday noon", "1pm");

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 1)));
    }

    @Test
    public void occursInDateRange_eventEnclosesRange_returnsTrue() throws NoriException {
        Event event = new Event("conference", "2026-09-01", "2026-09-10");

        assertTrue(event.occursInDateRange(LocalDate.of(2026, 9, 4), LocalDate.of(2026, 9, 6)));
    }

    @Test
    public void occursInDateRange_adjacentRange_returnsFalse() throws NoriException {
        Event event = new Event("conference", "2026-09-02", "2026-09-03");

        assertFalse(event.occursInDateRange(LocalDate.of(2026, 9, 4), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void findStartTimeOn_startNamesDateAndTime_returnsThatTime() throws NoriException {
        Event event = new Event("exam", "2026-09-01 0900", "2026-09-01 1200");

        assertEquals(Optional.of(LocalTime.of(9, 0)), event.findStartTimeOn(LocalDate.of(2026, 9, 1)));
    }

    @Test
    public void findStartTimeOn_startNamesDateOnly_returnsEmpty() throws NoriException {
        Event event = new Event("conference", "2026-09-01", "2026-09-03");

        assertTrue(event.findStartTimeOn(LocalDate.of(2026, 9, 1)).isEmpty());
    }

    @Test
    public void findStartTimeOn_dayAfterAMultiDayEventStarts_returnsEmpty() throws NoriException {
        Event event = new Event("conference", "2026-09-01 0900", "2026-09-03 1700");

        assertTrue(event.findStartTimeOn(LocalDate.of(2026, 9, 2)).isEmpty());
    }

    @Test
    public void findStartTimeOn_undatedEvent_returnsEmpty() throws NoriException {
        Event event = new Event("lunch", "Monday 1pm", "2pm");

        assertTrue(event.findStartTimeOn(LocalDate.of(2026, 9, 1)).isEmpty());
    }

    @Test
    public void findStartTimeOn_dateDigitsBesideTheTime_readsOnlyTheTime() throws NoriException {
        Event event = new Event("standup", "2026-09-01 2pm", "2026-09-01 3pm");

        assertEquals(Optional.of(LocalTime.of(14, 0)), event.findStartTimeOn(LocalDate.of(2026, 9, 1)));
    }
}
