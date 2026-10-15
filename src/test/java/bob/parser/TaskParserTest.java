package bob.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bob.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Todo;

/**
 * Tests {@link TaskParser}, which reads the description and dates of a task being
 * added.
 *
 * <p>Each method under test returns the task it built, so the task is checked
 * through its {@code toString}, which shows the description and every date that
 * was read.
 */
public class TaskParserTest {

    @Test
    public void parseTodo_description_todoBuilt() throws BobException {
        Todo todo = TaskParser.parseTodo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void parseTodo_noDescription_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseTodo(""));
    }

    @Test
    public void parseTodo_markerTextInsideAWord_keptInDescription() throws BobException {
        Todo todo = TaskParser.parseTodo("fly /tokyo and a/b test");

        assertEquals("[T][ ] fly /tokyo and a/b test", todo.toString());
    }

    @Test
    public void parseTodo_dueDateMarker_exceptionSuggestsDeadline() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseTodo("read book /by 2026-12-02"));

        assertEquals("A todo has no dates. Did yu mean deadline?"
                + "\nLike dis: deadline return book /by 2026-12-02", exception.getMessage());
    }

    @Test
    public void parseTodo_startOrEndMarker_exceptionSuggestsEvent() {
        BobException bothException = assertThrows(BobException.class, () ->
                TaskParser.parseTodo("party /from 2026-12-02 /to 2026-12-03"));
        BobException endException = assertThrows(BobException.class, () ->
                TaskParser.parseTodo("party /to 2026-12-03"));

        String expected = "A todo has no dates. Did yu mean event?"
                + "\nLike dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000";
        assertEquals(expected, bothException.getMessage());
        assertEquals(expected, endException.getMessage());
    }

    @Test
    public void parseTodo_descMarker_exceptionSaysItIsForEdit() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseTodo("/desc read book"));

        assertEquals("/desc is only used with edit. Write the description straight after todo."
                + "\nLike dis: todo borrow book", exception.getMessage());
    }

    @Test
    public void parseDeadline_descriptionAndDate_deadlineBuilt() throws BobException {
        Deadline deadline = TaskParser.parseDeadline("return book /by 2026-12-02");

        assertEquals("[D][ ] return book (by: Dec 02 2026)", deadline.toString());
    }

    @Test
    public void parseDeadline_dateWithTime_timeKept() throws BobException {
        Deadline deadline = TaskParser.parseDeadline("return book /by 2026-12-02 1800");

        assertEquals("[D][ ] return book (by: Dec 02 2026 18:00)", deadline.toString());
    }

    @Test
    public void parseDeadline_markerTextInsideAWord_notTakenAsTheMarker() throws BobException {
        // Looking for the letters "/by" anywhere would have split at /byzantine.
        Deadline deadline = TaskParser.parseDeadline("study /byzantine /by 2026-12-02");

        assertEquals("[D][ ] study /byzantine (by: Dec 02 2026)", deadline.toString());
    }

    @Test
    public void parseDeadline_missingMarker_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_missingDescription_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseDeadline("/by 2026-12-02"));
    }

    @Test
    public void parseDeadline_missingDate_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseDeadline("return book /by"));
        assertThrows(BobException.class, () -> TaskParser.parseDeadline("return book /by   "));
    }

    @Test
    public void parseDeadline_dateThatIsNotADate_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseDeadline("return book /by Sunday"));
        assertThrows(BobException.class, () -> TaskParser.parseDeadline("return book /by 2026-02-30"));
    }

    @Test
    public void parseDeadline_markerWrittenTwice_exceptionThrown() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseDeadline("return book /by 2026-12-02 /by 2026-12-03"));

        assertEquals("You wrote /by twice. Give just one.", exception.getMessage());
    }

    @Test
    public void parseDeadline_eventMarker_exceptionSaysWhichMarkerItTakes() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseDeadline("return book /by 2026-12-02 /from 2026-12-01"));

        assertEquals("A deadline only takes /by, not /from."
                + "\nLike dis: deadline return book /by 2026-12-02", exception.getMessage());
    }

    @Test
    public void parseDeadline_descMarker_exceptionSaysItIsForEdit() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseDeadline("/desc return book /by 2026-12-02"));

        assertEquals("/desc is only used with edit. Write the description straight after deadline."
                + "\nLike dis: deadline return book /by 2026-12-02", exception.getMessage());
    }

    @Test
    public void parseDeadline_markerInCapitals_exceptionSuggestsLowercase() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseDeadline("return book /BY 2026-12-02"));

        assertEquals("Markers are lowercase. Did yu mean /by?", exception.getMessage());
    }

    @Test
    public void parseEvent_descriptionStartAndEnd_eventBuilt() throws BobException {
        Event event = TaskParser.parseEvent("project meeting /from 2026-12-02 1800 /to 2026-12-02 2000");

        assertEquals("[E][ ] project meeting (from: Dec 02 2026 18:00 to: Dec 02 2026 20:00)",
                event.toString());
    }

    @Test
    public void parseEvent_markerTextInsideAWord_keptInDescription() throws BobException {
        Event event = TaskParser.parseEvent("trip /tokyo /from 2026-12-02 /to 2026-12-03");

        assertEquals("[E][ ] trip /tokyo (from: Dec 02 2026 to: Dec 03 2026)", event.toString());
    }

    @Test
    public void parseEvent_endMarkerAlsoInsideDescription_exceptionThrown() {
        // One rule for every marker in every command: a marker written as a word of
        // its own counts as a marker, even where it was meant as part of the description.
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseEvent("walk /to town /from 2026-12-02 /to 2026-12-03"));

        assertEquals("You wrote /to twice. Give just one.", exception.getMessage());
    }

    @Test
    public void parseEvent_endMarkerBeforeStartMarker_exceptionSaysPutStartFirst() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseEvent("meeting /to 2026-12-03 /from 2026-12-02"));

        assertEquals("Put /from before /to."
                + "\nLike dis: event project meeting /from 2026-12-02 1800 /to 2026-12-02 2000",
                exception.getMessage());
    }

    @Test
    public void parseEvent_dueDateMarker_exceptionSaysWhichMarkersItTakes() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseEvent("meeting /from 2026-12-02 /to 2026-12-03 /by 2026-12-01"));

        assertTrue(exception.getMessage().startsWith("An event only takes /from and /to, not /by.\n"));
    }

    @Test
    public void parseEvent_endSameMomentAsStart_exceptionSuggestsDeadline() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseEvent("photo /from 2026-12-02 1800 /to 2026-12-02 1800"));

        assertEquals("An event can't start and end at the same moment."
                + " For a single moment, use a deadline.", exception.getMessage());
    }

    @Test
    public void parseEvent_sameDayWithoutTimes_oneDayEventBuilt() throws BobException {
        Event event = TaskParser.parseEvent("open day /from 2026-12-02 /to 2026-12-02");

        assertEquals("[E][ ] open day (from: Dec 02 2026 to: Dec 02 2026)", event.toString());
    }

    @Test
    public void parseEvent_endBeforeStart_exceptionShowsBothDates() {
        BobException exception = assertThrows(BobException.class, () ->
                TaskParser.parseEvent("meeting /from 2026-12-02 2000 /to 2026-12-02 1800"));

        assertEquals("An event can't end before it starts."
                + "\nIt would run from Dec 02 2026 20:00 to Dec 02 2026 18:00.", exception.getMessage());
    }

    @Test
    public void parseEvent_missingMarker_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseEvent("meeting /to 2026-12-02"));
        assertThrows(BobException.class, () -> TaskParser.parseEvent("meeting /from 2026-12-02"));
    }

    @Test
    public void parseEvent_missingDescription_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseEvent("/from 2026-12-02 /to 2026-12-03"));
    }

    @Test
    public void parseEvent_missingStartOrEnd_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseEvent("meeting /from /to 2026-12-03"));
        assertThrows(BobException.class, () -> TaskParser.parseEvent("meeting /from 2026-12-02 /to"));
    }

    @Test
    public void parseEvent_timeThatIsNotADate_exceptionThrown() {
        assertThrows(BobException.class, () -> TaskParser.parseEvent("meeting /from soon /to 2026-12-03"));
    }
}
