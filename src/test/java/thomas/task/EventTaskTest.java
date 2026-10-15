package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

/**
 * Tests EventTask.
 * This is the task type with real logic in it, in two places. The constructor
 * refuses an event that ends before it starts, and refuses it there rather than
 * in the command that reads one, so that loading the save file is held to the
 * same rule. The cases below check the rule at the constructor, and StorageTest
 * checks that loading is really held to it.
 * The occursOn method is a range test with both ends included, and both ends
 * are tested. Testing the middle alone would still pass if the first and last
 * day of every event were dropped.
 */
public class EventTaskTest {

    private static final LocalDate DEC_02 = LocalDate.of(2019, 12, 2);
    private static final LocalDate DEC_03 = LocalDate.of(2019, 12, 3);
    private static final LocalDate DEC_04 = LocalDate.of(2019, 12, 4);

    /** An event running from 2pm on one day to 4pm on another. */
    private static EventTask eventFrom(LocalDate start, LocalDate end) throws ThomasException {
        return new EventTask("project meeting", start.atTime(14, 0), end.atTime(16, 0));
    }

    // ---- the constructor's rule ----

    @Test
    public void constructor_endAfterStart_eventCreated() throws ThomasException {
        EventTask event = eventFrom(DEC_02, DEC_04);

        assertEquals("project meeting", event.description);
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () ->
                new EventTask("project meeting", DEC_02.atTime(16, 0), DEC_02.atTime(14, 0)));

        assertEquals("Bust my buffers! Your event arrives before it sets off! Check its /from and /to.",
                e.getMessage());
    }

    @Test
    public void constructor_endOneMinuteBeforeStart_exceptionThrown() {
        // A day out is still backwards, however small the gap.
        assertThrows(ThomasException.class, () ->
                new EventTask("project meeting", DEC_02.atTime(14, 1), DEC_02.atTime(14, 0)));
    }

    @Test
    public void constructor_endEqualToStart_eventCreated() throws ThomasException {
        // Equal times are allowed. An event lasting no time is odd but says nothing false, while one ending before it
        // begins cannot be true -- so the check is isAfter rather than a comparison that would refuse this too.
        EventTask event = new EventTask("project meeting", DEC_02.atTime(14, 0), DEC_02.atTime(14, 0));

        assertTrue(event.occursOn(DEC_02));
    }

    @Test
    public void constructor_endOnEarlierDay_exceptionThrown() {
        // Running backwards across days is refused as much as within one day.
        assertThrows(ThomasException.class, () ->
                new EventTask("project meeting", DEC_04.atTime(14, 0), DEC_02.atTime(16, 0)));
    }

    // ---- display ----

    @Test
    public void toString_notDoneEvent_showsTagAndBothDates() throws ThomasException {
        assertEquals("[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                eventFrom(DEC_02, DEC_02).toString());
    }

    @Test
    public void toString_doneEvent_showsTickedBox() throws ThomasException {
        EventTask event = eventFrom(DEC_02, DEC_02);
        event.markAsDone();

        assertEquals("[E][X] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                event.toString());
    }

    @Test
    public void toString_eventSpanningDays_showsBothDays() throws ThomasException {
        assertEquals("[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)",
                eventFrom(DEC_02, DEC_04).toString());
    }

    // ---- occursOn ----

    @Test
    public void occursOn_firstDay_isTrue() throws ThomasException {
        // The first day counts, which a strict range test would drop.
        assertTrue(eventFrom(DEC_02, DEC_04).occursOn(DEC_02));
    }

    @Test
    public void occursOn_middleDay_isTrue() throws ThomasException {
        assertTrue(eventFrom(DEC_02, DEC_04).occursOn(DEC_03));
    }

    @Test
    public void occursOn_lastDay_isTrue() throws ThomasException {
        // And so does the last: an event covers every day from its start to its end.
        assertTrue(eventFrom(DEC_02, DEC_04).occursOn(DEC_04));
    }

    @Test
    public void occursOn_dayBeforeItStarts_isFalse() throws ThomasException {
        assertFalse(eventFrom(DEC_03, DEC_04).occursOn(DEC_02));
    }

    @Test
    public void occursOn_dayAfterItEnds_isFalse() throws ThomasException {
        assertFalse(eventFrom(DEC_02, DEC_03).occursOn(DEC_04));
    }

    @Test
    public void occursOn_singleDayEvent_isOnThatDayOnly() throws ThomasException {
        // An event within one day is on that day and no other.
        EventTask event = eventFrom(DEC_02, DEC_02);

        assertTrue(event.occursOn(DEC_02));
        assertFalse(event.occursOn(DEC_03));
    }

    @Test
    public void occursOn_endingJustAfterMidnight_stillCoversThatDay() throws ThomasException {
        // The times of day are dropped before comparing, so an event ending at one minute past midnight still covers
        // the day it ends on.
        EventTask event = new EventTask("project meeting", DEC_02.atTime(14, 0), DEC_03.atTime(0, 1));

        assertTrue(event.occursOn(DEC_03));
    }

    // ---- save format ----

    @Test
    public void toSaveFormat_notDoneEvent_writesTagFlagDescriptionAndBothDates() throws ThomasException {
        assertEquals("E | 0 | project meeting | 2019-12-02 1400 | 2019-12-04 1600",
                eventFrom(DEC_02, DEC_04).toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneEvent_writesTheDoneFlag() throws ThomasException {
        EventTask event = eventFrom(DEC_02, DEC_04);
        event.markAsDone();

        assertEquals("E | 1 | project meeting | 2019-12-02 1400 | 2019-12-04 1600",
                event.toSaveFormat());
    }

    @Test
    public void toSaveFormat_anyEvent_writesStartBeforeEnd() throws ThomasException {
        // The start is written before the end, in the order the loader reads them.
        String saved = eventFrom(DEC_02, DEC_04).toSaveFormat();

        assertTrue(saved.indexOf("2019-12-02 1400") < saved.indexOf("2019-12-04 1600"));
    }
}
