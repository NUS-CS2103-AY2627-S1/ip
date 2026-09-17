package wobble.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests subtype-specific task formatting and date accessors. */
class TaskSubtypeTest {
    @Test
    void todo_toString_includesTodoType() {
        assertEquals("[T][ ] tidy room", new Todo("tidy room").toString());
    }

    @Test
    void deadline_toString_includesFormattedDeadline() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2099, 8, 27, 18, 0));
        assertEquals(LocalDateTime.of(2099, 8, 27, 18, 0), deadline.getBy());
        assertEquals("[D][ ] submit report (by: Aug 27 2099 6:00 pm)", deadline.toString());
    }

    @Test
    void deadline_nullDueDate_rejectsInvalidDeadline() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit report", null));
    }

    @Test
    void deadline_pastUndoneDeadline_includesOverdueTag() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.now().minusMinutes(1));

        assertTrue(deadline.isOverdue());
        assertTrue(deadline.toString().contains("[D][ ] [OVERDUE] submit report"));
    }

    @Test
    void deadline_pastDoneDeadline_doesNotIncludeOverdueTag() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.now().minusMinutes(1));
        deadline.markAsDone();

        assertFalse(deadline.isOverdue());
        assertFalse(deadline.toString().contains("[OVERDUE]"));
    }

    @Test
    void event_pastUndoneEvent_includesPastTag() {
        LocalDateTime end = LocalDateTime.now().minusMinutes(1);
        Event event = new Event("meeting", end.minusHours(1), end);

        assertTrue(event.isPast());
        assertTrue(event.toString().contains("[E][ ] [PAST] meeting"));
    }

    @Test
    void event_toString_includesFormattedRange() {
        Event event = new Event("meeting", LocalDateTime.of(2099, 8, 27, 14, 0),
                LocalDateTime.of(2099, 8, 27, 16, 0));
        assertEquals("[E][ ] meeting (from: Aug 27 2099 2:00 pm to: Aug 27 2099 4:00 pm)", event.toString());
    }

    @Test
    void event_pastDoneEvent_doesNotIncludePastTag() {
        LocalDateTime end = LocalDateTime.now().minusMinutes(1);
        Event event = new Event("meeting", end.minusHours(1), end);
        event.markAsDone();

        assertFalse(event.isPast());
        assertFalse(event.toString().contains("[PAST]"));
    }

    @Test
    void event_sameStartAndEnd_rejectsInvalidRange() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 27, 14, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", date, date));
    }

    @Test
    void event_nullDateOrReversedRange_rejectsInvalidEvent() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 27, 14, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 27, 16, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", null, end));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, null));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", end, start));
    }

    @Test
    void deadline_sameAndDifferentDetails_returnExpectedResult() {
        LocalDateTime dueDate = LocalDateTime.of(2099, 8, 27, 18, 0);
        Deadline deadline = new Deadline("submit report", dueDate);

        assertTrue(deadline.hasSameDetailsAs(new Deadline("submit report", dueDate)));
        assertFalse(deadline.hasSameDetailsAs(new Deadline("submit report", dueDate.plusDays(1))));
        assertFalse(deadline.hasSameDetailsAs(new Todo("submit report")));
        assertFalse(deadline.hasSameDetailsAs(null));
    }

    @Test
    void event_sameAndDifferentDetails_returnExpectedResult() {
        LocalDateTime start = LocalDateTime.of(2099, 8, 27, 14, 0);
        LocalDateTime end = LocalDateTime.of(2099, 8, 27, 16, 0);
        Event event = new Event("meeting", start, end);

        assertTrue(event.hasSameDetailsAs(new Event("meeting", start, end)));
        assertFalse(event.hasSameDetailsAs(new Event("meeting", start.plusDays(1), end.plusDays(1))));
        assertFalse(event.hasSameDetailsAs(new Todo("meeting")));
        assertFalse(event.hasSameDetailsAs(null));
    }
}
