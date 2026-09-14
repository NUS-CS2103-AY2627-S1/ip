package cookie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/** Tests task state transitions and type-specific representations. */
public class TaskTest {
    @Test
    public void constructors_invalidInternalState_triggerAssertionErrors() {
        DateTimeValue date = new DateTimeValue(LocalDate.of(2026, 8, 27), null);

        assertThrows(AssertionError.class, () -> new Todo(null));
        assertThrows(AssertionError.class, () -> new Todo(" "));
        assertThrows(AssertionError.class, () -> new Todo("unsafe | description"));
        assertThrows(AssertionError.class, () -> new Deadline("submit report", null));
        assertThrows(AssertionError.class, () -> new Event("meeting", null, date));
        assertThrows(AssertionError.class, () -> new Event("meeting", date, null));
    }

    @Test
    public void todo_initialStateAndMarking_updatesStatusAndRepresentations() {
        Todo todo = new Todo("buy milk");
        assertEquals("buy milk", todo.getDescription());
        assertFalse(todo.isDone());
        assertEquals("[ ]", todo.getCheckbox());
        assertEquals("[T][ ] buy milk", todo.toString());
        assertEquals("T | Not Done | buy milk", todo.toFileFormat());

        todo.mark();
        assertTrue(todo.isDone());
        assertEquals("[X]", todo.getCheckbox());
        assertEquals("T | Done | buy milk", todo.toFileFormat());
        todo.unmark();
        assertFalse(todo.isDone());
    }

    @Test
    public void deadline_formatsItsDateTimeAndPreservesCompletionState() {
        DateTimeValue by = new DateTimeValue(LocalDate.of(2026, 8, 27), LocalTime.of(17, 5));
        Deadline deadline = new Deadline("submit report", by);

        assertEquals(by, deadline.getBy());
        assertEquals("[D][ ] submit report (by: Aug 27 2026, 5:05 PM)", deadline.toString());
        assertEquals("D | Not Done | submit report | 2026-08-27T17:05",
                deadline.toFileFormat());
        deadline.mark();
        assertEquals("D | Done | submit report | 2026-08-27T17:05", deadline.toFileFormat());
    }

    @Test
    public void event_formatsStartAndEndAndPreservesCompletionState() {
        DateTimeValue start = new DateTimeValue(null, LocalTime.of(9, 0));
        DateTimeValue end = new DateTimeValue(null, LocalTime.of(10, 30));
        Event event = new Event("team meeting", start, end);

        assertEquals(start, event.getStart());
        assertEquals(end, event.getEnd());
        assertEquals("[E][ ] team meeting (from: 9:00 AM to: 10:30 AM)", event.toString());
        assertEquals("E | Not Done | team meeting | 09:00 to 10:30", event.toFileFormat());
        event.mark();
        assertTrue(event.isDone());
        assertEquals("E | Done | team meeting | 09:00 to 10:30", event.toFileFormat());
    }

    @Test
    public void event_invalidPeriod_throwsIllegalArgumentException() {
        DateTimeValue morning = new DateTimeValue(null, LocalTime.of(9, 0));
        DateTimeValue evening = new DateTimeValue(null, LocalTime.of(18, 0));
        DateTimeValue date = new DateTimeValue(LocalDate.of(2026, 8, 27), null);

        assertThrows(IllegalArgumentException.class, () ->
                new Event("same time", morning, morning));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("backwards", evening, morning));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("mixed", date, morning));
    }

    @Test
    public void hasSameDetails_matchingTasks_ignoresStatusButChecksTypeAndTemporalValues() {
        Todo firstTodo = new Todo("buy milk");
        Todo matchingTodo = new Todo("buy milk");
        matchingTodo.mark();
        Deadline deadline = new Deadline("buy milk",
                new DateTimeValue(LocalDate.of(2026, 8, 27), null));

        assertTrue(firstTodo.hasSameDetails(matchingTodo));
        assertFalse(firstTodo.hasSameDetails(deadline));
        assertFalse(firstTodo.hasSameDetails(new Todo("buy bread")));
        assertFalse(firstTodo.hasSameDetails(null));

        Deadline matchingDeadline = new Deadline("buy milk",
                new DateTimeValue(LocalDate.of(2026, 8, 27), null));
        Deadline differentDeadline = new Deadline("buy milk",
                new DateTimeValue(LocalDate.of(2026, 8, 28), null));
        assertTrue(deadline.hasSameDetails(matchingDeadline));
        assertFalse(deadline.hasSameDetails(differentDeadline));
    }

    @Test
    public void occursOn_differentTaskTypes_appliesSubtypeDateRules() {
        LocalDate queriedDate = LocalDate.of(2026, 8, 27);
        Todo todo = new Todo("ordinary todo");
        Deadline deadline = new Deadline("submit report", new DateTimeValue(queriedDate, null));
        Event event = new Event("conference",
                new DateTimeValue(queriedDate.minusDays(1), null),
                new DateTimeValue(queriedDate.plusDays(1), null));

        assertFalse(todo.occursOn(queriedDate));
        assertTrue(deadline.occursOn(queriedDate));
        assertTrue(event.occursOn(queriedDate));
        assertFalse(event.occursOn(queriedDate.plusDays(2)));
    }

    @Test
    public void event_timeOnlyPeriodAndDetailComparison_applyEventRules() {
        LocalDate queriedDate = LocalDate.of(2026, 8, 27);
        DateTimeValue start = new DateTimeValue(null, LocalTime.of(9, 0));
        DateTimeValue end = new DateTimeValue(null, LocalTime.of(10, 0));
        Event event = new Event("meeting", start, end);
        Event matchingEvent = new Event("meeting", start, end);
        Event differentEvent = new Event("meeting", start,
                new DateTimeValue(null, LocalTime.of(11, 0)));

        assertFalse(event.occursOn(queriedDate));
        assertTrue(event.hasSameDetails(matchingEvent));
        assertFalse(event.hasSameDetails(differentEvent));
    }
}
