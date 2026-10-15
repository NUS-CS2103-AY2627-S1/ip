package bob.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import bob.BobException;

/**
 * Tests {@link Event}, and above all {@link Event#occursOn}.
 *
 * <p>An event is the one kind of task that covers a stretch of time rather than
 * a point in it, and {@code occursOn} is where that difference is written down:
 * it is widened from the inherited version so that asking what is on the
 * Wednesday of a week-long orientation finds it. That widening is easy to get
 * wrong at the ends of the stretch, so the day it starts, the day it ends, and
 * the days on either side are all checked.
 *
 * <p>The other date questions are deliberately <em>not</em> widened: an event is
 * placed by its start for ordering and for "before" and "after". The test at the
 * end pins that down, since it looks like an oversight until it is stated.
 *
 * <p>{@link Event#requireValidPeriod} is tested closely too, because adding, editing
 * and loading an event all rely on it. Its boundary is an end at the same moment as
 * the start: refused when both ends have a time, allowed for a one-day event without.
 */
public class EventTest {

    @Test
    public void getTypeIcon_anyEvent_returnsE() throws BobException {
        assertEquals("E", eventFrom("2026-12-01", "2026-12-05").getTypeIcon());
        assertEquals(Event.TYPE_ICON, eventFrom("2026-12-01", "2026-12-05").getTypeIcon());
    }

    @Test
    public void occursOn_dayInTheMiddleOfTheEvent_true() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");

        assertTrue(event.occursOn(LocalDate.of(2026, 12, 3)));
    }

    @Test
    public void occursOn_firstAndLastDayOfTheEvent_true() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");

        // Both ends count as days the event is running.
        assertTrue(event.occursOn(LocalDate.of(2026, 12, 1)));
        assertTrue(event.occursOn(LocalDate.of(2026, 12, 5)));
    }

    @Test
    public void occursOn_dayJustOutsideTheEvent_false() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");

        assertFalse(event.occursOn(LocalDate.of(2026, 11, 30)));
        assertFalse(event.occursOn(LocalDate.of(2026, 12, 6)));
    }

    @Test
    public void occursOn_eventWithinOneDay_thatDayOnly() throws BobException {
        Event event = eventFrom("2026-12-02 1800", "2026-12-02 2000");

        assertTrue(event.occursOn(LocalDate.of(2026, 12, 2)));
        assertFalse(event.occursOn(LocalDate.of(2026, 12, 1)));
        assertFalse(event.occursOn(LocalDate.of(2026, 12, 3)));
    }

    @Test
    public void occursOn_timesOfDayAtTheEnds_ignored() throws BobException {
        // The question is about a day, so an event that ends at nine in the
        // morning is still running on the day it ends.
        Event event = eventFrom("2026-12-01 2100", "2026-12-05 0900");

        assertTrue(event.occursOn(LocalDate.of(2026, 12, 1)));
        assertTrue(event.occursOn(LocalDate.of(2026, 12, 5)));
    }

    @Test
    public void getScheduledDate_anyEvent_returnsTheStart() throws BobException {
        Event event = eventFrom("2026-12-01 1400", "2026-12-05 1600");

        // The start is when the event first wants the user's attention.
        assertEquals("2026-12-01 1400", event.getScheduledDate().orElseThrow().toSaveField());
    }

    @Test
    public void dateChecks_eventAlreadyRunning_measuredFromItsStart() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");
        LocalDate day = LocalDate.of(2026, 12, 3);

        // An event already running is not still to come, so it is not "after"
        // today; occursOn is the question that finds it.
        assertFalse(event.isAfter(day));
        assertTrue(event.isBefore(day));
        assertTrue(event.occursOn(day));
    }

    @Test
    public void dateChecks_eventStillToCome_isAfter() throws BobException {
        Event event = eventFrom("2026-12-10", "2026-12-12");

        assertTrue(event.isAfter(LocalDate.of(2026, 12, 3)));
        assertFalse(event.isBefore(LocalDate.of(2026, 12, 3)));
    }

    @Test
    public void toString_newEvent_showsBothEndsInTheFriendlyForm() throws BobException {
        Event event = new Event("project meeting",
                TaskDateTime.parse("2026-12-02 1800"), TaskDateTime.parse("2026-12-02 2000"));

        assertEquals("[E][ ] project meeting (from: Dec 02 2026 18:00 to: Dec 02 2026 20:00)",
                event.toString());
    }

    @Test
    public void toString_doneEvent_showsCrossInStatusBox() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");
        event.markAsDone();

        assertEquals("[E][X] orientation (from: Dec 01 2026 to: Dec 05 2026)", event.toString());
    }

    @Test
    public void toSaveFields_anyEvent_addsStartAndEndAsTwoFields() throws BobException {
        Event event = new Event("project meeting",
                TaskDateTime.parse("2026-12-02 1800"), TaskDateTime.parse("2026-12-02 2000"));

        // Two fields rather than one, so reading them back is a matter of taking
        // two fields apart rather than splitting one down the middle.
        assertEquals(List.of("E", Task.NOT_DONE_FLAG, "project meeting",
                "2026-12-02 1800", "2026-12-02 2000"), event.toSaveFields());
    }

    @Test
    public void toSaveFields_doneEvent_returnsDoneFlag() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");
        event.markAsDone();

        assertEquals(Task.DONE_FLAG, event.toSaveFields().get(1));
    }

    @Test
    public void requireValidPeriod_endBeforeStart_exceptionShowsBothDates() throws BobException {
        TaskDateTime from = TaskDateTime.parse("2026-12-05 1800");
        TaskDateTime to = TaskDateTime.parse("2026-12-05 0900");

        BobException exception = assertThrows(BobException.class, () -> Event.requireValidPeriod(from, to));

        assertEquals("An event can't end before it starts."
                + "\nIt would run from Dec 05 2026 18:00 to Dec 05 2026 09:00.", exception.getMessage());
    }

    @Test
    public void requireValidPeriod_sameMomentWithTimes_exceptionSuggestsDeadline() throws BobException {
        TaskDateTime from = TaskDateTime.parse("2026-12-05 0900");
        TaskDateTime to = TaskDateTime.parse("2026-12-05 0900");

        BobException exception = assertThrows(BobException.class, () -> Event.requireValidPeriod(from, to));

        assertEquals("An event can't start and end at the same moment."
                + " For a single moment, use a deadline.", exception.getMessage());
    }

    @Test
    public void requireValidPeriod_sameDayWithoutTimes_accepted() {
        // A day with no time means the whole of that day, so a one-day event runs from
        // the start of the 5th to the end of it, and is not a single moment.
        assertDoesNotThrow(() -> Event.requireValidPeriod(TaskDateTime.parse("2026-12-05"),
                TaskDateTime.parse("2026-12-05")));
    }

    @Test
    public void requireValidPeriod_endDayWithoutATimeAfterATimedStart_accepted() {
        // The end is the end of the 5th, which is after 18:00 on the 5th.
        assertDoesNotThrow(() -> Event.requireValidPeriod(TaskDateTime.parse("2026-12-05 1800"),
                TaskDateTime.parse("2026-12-05")));
    }

    @Test
    public void requireValidPeriod_startDayWithoutATimeEndingAtMidnight_refused() throws BobException {
        TaskDateTime from = TaskDateTime.parse("2026-12-05");
        TaskDateTime to = TaskDateTime.parse("2026-12-05 0000");

        // The start counts as midnight at the start of the 5th, which is the very
        // moment the user asked the event to end at.
        BobException exception = assertThrows(BobException.class, () -> Event.requireValidPeriod(from, to));

        assertEquals("An event can't start and end at the same moment."
                + " For a single moment, use a deadline.", exception.getMessage());
    }

    @Test
    public void requireValidPeriod_endDayBeforeATimedStartOnTheSameDay_exceptionThrown() throws BobException {
        TaskDateTime from = TaskDateTime.parse("2026-12-06 0900");
        TaskDateTime to = TaskDateTime.parse("2026-12-05");

        // The end of the 5th is still before 09:00 on the 6th.
        BobException exception = assertThrows(BobException.class, () -> Event.requireValidPeriod(from, to));

        assertTrue(exception.getMessage().startsWith("An event can't end before it starts."));
    }

    @Test
    public void requireValidPeriod_endOneMinuteAfterStart_accepted() {
        assertDoesNotThrow(() -> Event.requireValidPeriod(TaskDateTime.parse("2026-12-05 0900"),
                TaskDateTime.parse("2026-12-05 0901")));
    }

    @Test
    public void withEdit_endOnly_startKept() throws BobException {
        Task edited = eventFrom("2026-12-01", "2026-12-05").withEdit(endsEdit(null, "2026-12-07"));

        assertEquals("[E][ ] orientation (from: Dec 01 2026 to: Dec 07 2026)", edited.toString());
    }

    @Test
    public void withEdit_bothEnds_bothChanged() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");

        Task edited = event.withEdit(endsEdit("2026-12-10", "2026-12-12"));

        assertEquals("[E][ ] orientation (from: Dec 10 2026 to: Dec 12 2026)", edited.toString());
    }

    @Test
    public void withEdit_endMovedBeforeStart_exceptionThrown() throws BobException {
        Event event = eventFrom("2026-12-02 1800", "2026-12-02 2000");

        BobException exception = assertThrows(BobException.class, () ->
                event.withEdit(endsEdit(null, "2026-12-02 1700")));

        // The start is shown too, though it was not typed, since it is what the new end clashes with.
        assertEquals("An event can't end before it starts."
                + "\nIt would run from Dec 02 2026 18:00 to Dec 02 2026 17:00.",
                exception.getMessage());
    }

    @Test
    public void withEdit_startMovedPastEnd_exceptionThrown() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");

        assertThrows(BobException.class, () -> event.withEdit(endsEdit("2026-12-06", null)));
    }

    @Test
    public void withEdit_startMovedOntoEndWithTimes_exceptionThrown() throws BobException {
        Event event = eventFrom("2026-12-02 1800", "2026-12-02 2000");

        BobException exception = assertThrows(BobException.class, () ->
                event.withEdit(endsEdit("2026-12-02 2000", null)));

        // Refused as it is when an event is added, since both use the same rule.
        assertEquals("An event can't start and end at the same moment."
                + " For a single moment, use a deadline.", exception.getMessage());
    }

    @Test
    public void withEdit_startMovedOntoEndDayWithoutTimes_accepted() throws BobException {
        Task edited = eventFrom("2026-12-01", "2026-12-05").withEdit(endsEdit("2026-12-05", null));

        assertEquals("[E][ ] orientation (from: Dec 05 2026 to: Dec 05 2026)", edited.toString());
    }

    @Test
    public void withEdit_descriptionOnly_endsKept() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");

        Task edited = event.withEdit(new TaskEdit("orientation week", null, null, null));

        assertEquals("[E][ ] orientation week (from: Dec 01 2026 to: Dec 05 2026)", edited.toString());
    }

    @Test
    public void withEdit_dueDate_exceptionThrown() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");
        TaskDateTime date = TaskDateTime.parse("2026-12-02");

        BobException exception = assertThrows(BobException.class, () ->
                event.withEdit(new TaskEdit(null, date, null, null)));

        assertEquals("An event has a start and an end, not a due date.", exception.getMessage());
    }

    @Test
    public void isSameTaskAs_sameStartAndEnd_true() throws BobException {
        Event retyped = new Event("ORIENTATION",
                TaskDateTime.parse("2026-12-01"), TaskDateTime.parse("2026-12-05"));

        assertTrue(eventFrom("2026-12-01", "2026-12-05").isSameTaskAs(retyped));
    }

    @Test
    public void isSameTaskAs_differentStartOrEnd_false() throws BobException {
        Event event = eventFrom("2026-12-01", "2026-12-05");

        assertFalse(event.isSameTaskAs(eventFrom("2026-12-02", "2026-12-05")));
        // Starting together is not enough: both ends are compared.
        assertFalse(event.isSameTaskAs(eventFrom("2026-12-01", "2026-12-06")));
        assertFalse(event.isSameTaskAs(new Deadline("orientation", TaskDateTime.parse("2026-12-01"))));
    }

    /** Returns an event called {@code orientation} running between two dates. */
    private static Event eventFrom(String from, String to) throws BobException {
        return new Event("orientation", TaskDateTime.parse(from), TaskDateTime.parse(to));
    }

    /**
     * Returns an edit that moves the start to {@code from} and the end to {@code to},
     * either of which may be {@code null} to leave that end where it is.
     */
    private static TaskEdit endsEdit(String from, String to) throws BobException {
        TaskDateTime newFrom = (from == null) ? null : TaskDateTime.parse(from);
        TaskDateTime newTo = (to == null) ? null : TaskDateTime.parse(to);
        return new TaskEdit(null, null, newFrom, newTo);
    }
}
