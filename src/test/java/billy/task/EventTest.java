package billy.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import billy.BillyException;

/**
 * Tests {@link Event}, a task running from one moment until another.
 *
 * <p>An event is the only task covering more than one day, so
 * {@link Event#occursOn(LocalDate)} is the one place a range is worked out
 * rather than a single date compared. Both ends count as part of the event,
 * which makes the boundaries worth testing on purpose: an event from the 2nd to
 * the 4th must be found on the 2nd, the 3rd and the 4th alike.
 *
 * <p>The constructor also enforces a rule no other task has — an event may not
 * end before it starts — and refusing it there is what keeps such an event out
 * of the list entirely.
 */
public class EventTest {

    /** Builds an event running between the two typed moments. */
    private static Event running(String from, String to) throws BillyException {
        return new Event("project meeting", TaskDate.parse(from), TaskDate.parse(to));
    }

    // ---------------------------------------------------------------
    // The days an event covers
    // ---------------------------------------------------------------

    @Test
    public void occursOn_firstDay_true() throws BillyException {
        // The day it starts is part of it: a boundary, and inclusive.
        assertTrue(running("2019-12-02", "2019-12-04").occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_lastDay_true() throws BillyException {
        // So is the day it ends, which is the boundary most easily lost.
        assertTrue(running("2019-12-02", "2019-12-04").occursOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    public void occursOn_dayInTheMiddle_true() throws BillyException {
        // Nothing happens on the 3rd except that the event is still running,
        // which is exactly why a range is needed rather than two date checks.
        assertTrue(running("2019-12-02", "2019-12-04").occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dayBeforeItStarts_false() throws BillyException {
        assertFalse(running("2019-12-02", "2019-12-04").occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_dayAfterItEnds_false() throws BillyException {
        assertFalse(running("2019-12-02", "2019-12-04").occursOn(LocalDate.of(2019, 12, 5)));
    }

    @Test
    public void occursOn_eventWithinOneDay_thatDayOnly() throws BillyException {
        Event event = running("2019-12-02 1000", "2019-12-02 1400");
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_eventSpanningMonths_daysAcrossTheBoundary() throws BillyException {
        Event event = running("2019-11-28", "2019-12-03");
        assertTrue(event.occursOn(LocalDate.of(2019, 11, 30)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 1)));
        assertFalse(event.occursOn(LocalDate.of(2019, 11, 27)));
    }

    // ---------------------------------------------------------------
    // An event that could not happen
    // ---------------------------------------------------------------

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        // Such an event covers no days at all, so it would sit in the list yet
        // never be found by `on` — even on the days it names. Refusing it here
        // is why that can never happen.
        assertThrows(BillyException.class, () -> running("2019-12-04", "2019-12-02"));
    }

    @Test
    public void constructor_endEarlierOnTheSameDay_exceptionThrown() {
        assertThrows(BillyException.class, () -> running("2019-12-02 1600", "2019-12-02 1400"));
    }

    @Test
    public void constructor_endBeforeStart_messageNamesBothEnds() {
        BillyException thrown =
                assertThrows(BillyException.class, () -> running("2019-12-04", "2019-12-02"));
        // The user is shown the two moments so they can see which to correct.
        assertTrue(thrown.getMessage().contains("Dec 4 2019"));
        assertTrue(thrown.getMessage().contains("Dec 2 2019"));
    }

    @Test
    public void constructor_startAndEndAtTheSameMoment_exceptionThrown() {
        // Changed deliberately: an event from 2pm to 2pm covers no time at all,
        // which is almost always a mistyped end time rather than something the
        // user meant. It used to be accepted on the grounds that equal is not
        // backwards.
        BillyException thrown = assertThrows(BillyException.class, () ->
                running("2019-12-02 1400", "2019-12-02 1400"));
        assertTrue(thrown.getMessage().contains("last some time"), thrown.getMessage());
    }

    @Test
    public void constructor_sameDayAndOnlyOneEndHasATime_allowed() {
        // A date with no time says nothing about the hour, so the order cannot
        // be shown to be wrong and the event is not refused on a guess.
        assertDoesNotThrow(() -> running("2019-12-02 1000", "2019-12-02"));
    }

    // ---------------------------------------------------------------
    // Writing an event out
    // ---------------------------------------------------------------

    @Test
    public void toString_eventWithTimes_bothEndsShown() throws BillyException {
        assertEquals("[E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)",
                running("2019-12-02 1400", "2019-12-02 1600").toString());
    }

    @Test
    public void toString_doneEvent_ticked() throws BillyException {
        Event event = running("2019-12-02", "2019-12-04");
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Dec 2 2019 to: Dec 4 2019)",
                event.toString());
    }

    @Test
    public void toSaveFormat_eventWithTimes_bothEndsAreOwnFields() throws BillyException {
        assertEquals("E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600",
                running("2019-12-02 1400", "2019-12-02 1600").toSaveFormat());
    }

    @Test
    public void toSaveFormat_eventWithoutTimes_datesOnly() throws BillyException {
        assertEquals("E | 0 | project meeting | 2019-12-02 | 2019-12-04",
                running("2019-12-02", "2019-12-04").toSaveFormat());
    }

    @Test
    public void constructor_sameDayWithNoTimes_allowed() {
        // The trap in refusing the case above: an all-day event on one day is
        // written exactly like this, and is perfectly ordinary. Refusing it
        // would break a case that has always worked.
        assertDoesNotThrow(() -> new Event("conference", TaskDate.parse("2019-12-02"),
                TaskDate.parse("2019-12-02")));
    }

    @Test
    public void constructor_sameDayDifferentTimes_allowed() {
        assertDoesNotThrow(() -> new Event("meeting", TaskDate.parse("2019-12-02 1400"),
                TaskDate.parse("2019-12-02 1600")));
    }

    @Test
    public void isSameTask_sameWordingAndSamePeriod_true() throws BillyException {
        assertTrue(new Event("meeting", TaskDate.parse("2019-12-02 1400"),
                        TaskDate.parse("2019-12-02 1600"))
                .isSameTask(new Event("meeting", TaskDate.parse("2019-12-02 1400"),
                        TaskDate.parse("2019-12-02 1600"))));
    }

    @Test
    public void isSameTask_sameWordingButADifferentEnd_false() throws BillyException {
        assertFalse(new Event("meeting", TaskDate.parse("2019-12-02 1400"),
                        TaskDate.parse("2019-12-02 1600"))
                .isSameTask(new Event("meeting", TaskDate.parse("2019-12-02 1400"),
                        TaskDate.parse("2019-12-02 1700"))));
    }

    @Test
    public void isSameTask_differentDescription_false() throws BillyException {
        // Stops at the description without ever looking at the dates.
        assertFalse(new Event("meeting", TaskDate.parse("2019-12-02 1400"),
                        TaskDate.parse("2019-12-02 1600"))
                .isSameTask(new Event("standup", TaskDate.parse("2019-12-02 1400"),
                        TaskDate.parse("2019-12-02 1600"))));
    }

    @Test
    public void isSameTask_sameWordingButADifferentStart_false() throws BillyException {
        // The start is checked as well as the end. Comparing only the end would
        // call two differently scheduled meetings the same event.
        assertFalse(new Event("meeting", TaskDate.parse("2019-12-02 1400"),
                        TaskDate.parse("2019-12-02 1600"))
                .isSameTask(new Event("meeting", TaskDate.parse("2019-12-02 1500"),
                        TaskDate.parse("2019-12-02 1600"))));
    }
}
