package elsa.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Event}, the kind of task that runs between two dates.
 *
 * <p>Most of these are about {@link Event#occursOn}, which decides whether a day
 * is part of the event. It is written as "not before the start and not after the
 * end" so that the first and last days count, and that is exactly the kind of
 * boundary that is easy to get wrong by one day in either direction. Every day
 * from before the start to after the end is therefore checked.
 */
public class EventTest {

    private static final LocalDate FROM = LocalDate.of(2019, 10, 14);
    private static final LocalDate TO = LocalDate.of(2019, 10, 16);

    /** A three day event, running from the 14th to the 16th of October 2019. */
    private static Event threeDayEvent() {
        return new Event("project meeting", FROM, TO);
    }

    // ------------------------------------------------------------------
    // Telling one event from another
    // ------------------------------------------------------------------

    @Test
    public void isSameTask_sameDescriptionAndDates_returnsTrue() {
        assertTrue(threeDayEvent().isSameTask(threeDayEvent()));
    }

    /**
     * An event carries two dates, and either one of them differing makes it a
     * different event. Both are checked, because a comparison that weighed only
     * the start date would pass the first of these and fail nothing.
     */
    @Test
    public void isSameTask_sameDescriptionDifferentDates_returnsFalse() {
        Event laterEnd = new Event("project meeting", FROM, TO.plusDays(1));
        Event laterStart = new Event("project meeting", FROM.plusDays(1), TO);

        assertFalse(threeDayEvent().isSameTask(laterEnd));
        assertFalse(threeDayEvent().isSameTask(laterStart));
    }

    // ------------------------------------------------------------------
    // Which days are part of the event
    // ------------------------------------------------------------------

    @Test
    public void occursOn_theFirstDay_returnsTrue() {
        assertTrue(threeDayEvent().occursOn(FROM));
    }

    @Test
    public void occursOn_theLastDay_returnsTrue() {
        assertTrue(threeDayEvent().occursOn(TO));
    }

    @Test
    public void occursOn_aDayInBetween_returnsTrue() {
        assertTrue(threeDayEvent().occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_theDayBeforeItStarts_returnsFalse() {
        assertFalse(threeDayEvent().occursOn(FROM.minusDays(1)));
    }

    @Test
    public void occursOn_theDayAfterItEnds_returnsFalse() {
        assertFalse(threeDayEvent().occursOn(TO.plusDays(1)));
    }

    @Test
    public void occursOn_aDayFarOutside_returnsFalse() {
        Event event = threeDayEvent();
        assertFalse(event.occursOn(LocalDate.of(2018, 1, 1)));
        assertFalse(event.occursOn(LocalDate.of(2999, 1, 1)));
    }

    /** An event that starts and ends on one day still runs on that day. */
    @Test
    public void occursOn_singleDayEventOnItsOnlyDay_returnsTrue() {
        Event event = new Event("standup", FROM, FROM);
        assertTrue(event.occursOn(FROM));
        assertFalse(event.occursOn(FROM.plusDays(1)));
    }

    /**
     * Whether an event is done has nothing to do with when it runs, so marking it
     * must not change which days it falls on.
     */
    @Test
    public void occursOn_eventMarkedDone_stillReturnsTrue() {
        Event event = threeDayEvent();
        event.markAsDone();
        assertTrue(event.occursOn(FROM));
    }

    // ------------------------------------------------------------------
    // Writing it out
    // ------------------------------------------------------------------

    @Test
    public void toString_notDone_showsTypeStatusAndBothDates() {
        assertEquals("[E][ ] project meeting (from: Oct 14 2019 to: Oct 16 2019)",
                threeDayEvent().toString());
    }

    @Test
    public void toString_done_showsCrossInStatusBrackets() {
        Event event = threeDayEvent();
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Oct 14 2019 to: Oct 16 2019)",
                event.toString());
    }

    /**
     * An event that has finished is not a debt, so it never carries the overdue
     * note that a deadline in the past does.
     */
    @Test
    public void toString_eventEntirelyInThePast_hasNoOverdueNote() {
        assertFalse(threeDayEvent().toString().contains("overdue"));
    }

    /**
     * The two dates are kept in separate fields rather than as one piece of text,
     * so that reading the file back does not have to split them apart again.
     */
    @Test
    public void toSaveFormat_notDone_writesBothDatesAsSeparateFields() {
        assertEquals("E | 0 | project meeting | 2019-10-14 | 2019-10-16",
                threeDayEvent().toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_writesTheDoneMarker() {
        Event event = threeDayEvent();
        event.markAsDone();
        assertEquals("E | 1 | project meeting | 2019-10-14 | 2019-10-16",
                event.toSaveFormat());
    }

    @Test
    public void toSaveFormatThenDecode_event_givesAnEqualEvent() throws Exception {
        Event event = threeDayEvent();
        Task reloaded = TaskFormat.decode(event.toSaveFormat());
        assertEquals(event.toString(), reloaded.toString());
    }
}
