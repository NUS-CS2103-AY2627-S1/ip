package peter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Event}.
 *
 * <p>{@link Event#occursOn(LocalDate)} is the most logic-heavy method on any
 * task: it decides whether a date falls inside an inclusive range, which is
 * exactly the shape that off-by-one errors hide in. Most tests below pin the
 * boundaries of that range.
 */
public class EventTest {

    /** A three-day event with times, used by most range tests. */
    private static Event threeDayEvent() {
        return new Event("camp",
                LocalDateTime.of(2019, 12, 2, 18, 0),
                LocalDateTime.of(2019, 12, 5, 9, 30));
    }

    // =====================================================================
    // occursOn(LocalDate)
    // =====================================================================

    @Test
    public void occursOn_dayBeforeStart_falseReturned() {
        assertFalse(threeDayEvent().occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_startDate_trueReturned() {
        // The range is inclusive at the start, and the 18:00 start time must
        // not exclude the earlier part of that day.
        assertTrue(threeDayEvent().occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_dateInsideRange_trueReturned() {
        assertTrue(threeDayEvent().occursOn(LocalDate.of(2019, 12, 3)));
        assertTrue(threeDayEvent().occursOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    public void occursOn_endDate_trueReturned() {
        // Inclusive at the end too, despite the event finishing at 09:30.
        assertTrue(threeDayEvent().occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void occursOn_dayAfterEnd_falseReturned() {
        assertFalse(threeDayEvent().occursOn(LocalDate.of(2019, 12, 6)));
    }

    @Test
    public void occursOn_singleDayEvent_onlyThatDateMatches() {
        // Start and end on the same day: the narrowest possible range, where
        // an off-by-one would either match nothing or match neighbors.
        Event event = new Event("meeting",
                LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 17, 0));

        assertFalse(event.occursOn(LocalDate.of(2019, 12, 1)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_eventSpanningMonthEnd_trueReturned() {
        // Date arithmetic across a month boundary, which a naive day-number
        // comparison would get wrong.
        Event event = new Event("holiday",
                LocalDateTime.of(2019, 11, 29, 0, 0),
                LocalDateTime.of(2019, 12, 2, 0, 0));

        assertTrue(event.occursOn(LocalDate.of(2019, 11, 30)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 1)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_eventSpanningYearEnd_trueReturned() {
        Event event = new Event("break",
                LocalDateTime.of(2019, 12, 30, 0, 0),
                LocalDateTime.of(2020, 1, 2, 0, 0));

        assertTrue(event.occursOn(LocalDate.of(2019, 12, 31)));
        assertTrue(event.occursOn(LocalDate.of(2020, 1, 1)));
    }

    @Test
    public void occursOn_sameDateDifferentYear_falseReturned() {
        // The comparison must use the full date, not just month and day.
        assertFalse(threeDayEvent().occursOn(LocalDate.of(2020, 12, 3)));
    }

    @Test
    public void occursOn_backwardsEvent_falseReturned() {
        // Parser now rejects a backwards event, but the constructor itself
        // does not validate order, so this records why that guard matters:
        // the range is empty and matches nothing, including the two dates the
        // user actually typed. Such data can still reach here from a
        // hand-edited save file.
        Event backwards = new Event("camp",
                LocalDateTime.of(2019, 12, 5, 0, 0),
                LocalDateTime.of(2019, 12, 2, 0, 0));

        assertFalse(backwards.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(backwards.occursOn(LocalDate.of(2019, 12, 3)));
        assertFalse(backwards.occursOn(LocalDate.of(2019, 12, 5)));
    }

    // =====================================================================
    // toDataString()
    // =====================================================================

    @Test
    public void toDataString_notDone_zeroStatusWritten() {
        assertEquals("E | 0 | camp | 2019-12-02T18:00 | 2019-12-05T09:30",
                threeDayEvent().toDataString());
    }

    @Test
    public void toDataString_done_oneStatusWritten() {
        Event event = threeDayEvent();
        event.markAsDone();
        assertEquals("E | 1 | camp | 2019-12-02T18:00 | 2019-12-05T09:30",
                event.toDataString());
    }

    @Test
    public void toDataString_midnightTimes_isoTimesWritten() {
        // Storage relies on LocalDateTime.toString(), which always includes a
        // time, so a date-only event still round trips.
        assertEquals("E | 0 | camp | 2019-12-02T00:00 | 2019-12-05T00:00",
                new Event("camp",
                        LocalDateTime.of(2019, 12, 2, 0, 0),
                        LocalDateTime.of(2019, 12, 5, 0, 0)).toDataString());
    }

    // =====================================================================
    // getScheduleDetails() and type icon
    // =====================================================================

    @Test
    public void getScheduleDetails_datesWithTimes_bothTimesShown() {
        assertEquals(" (from: Dec 2 2019, 6:00 PM to: Dec 5 2019, 9:30 AM)",
                threeDayEvent().getScheduleDetails());
    }

    @Test
    public void getScheduleDetails_midnightTimes_datesOnlyShown() {
        assertEquals(" (from: Dec 2 2019 to: Dec 5 2019)",
                new Event("camp",
                        LocalDateTime.of(2019, 12, 2, 0, 0),
                        LocalDateTime.of(2019, 12, 5, 0, 0)).getScheduleDetails());
    }

    @Test
    public void getTaskTypeIcon_event_eIconReturned() {
        assertEquals("E", threeDayEvent().getTaskTypeIcon());
    }
}
