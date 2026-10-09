package sumo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task validation, identity, status, display, and storage representations. */
public class TaskTest {
    @Test
    public void constructor_horizontalWhitespace_descriptionNormalized() {
        Task task = new Task("  read\t  the book  ");

        assertEquals("read the book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("[ ] read the book", task.toString());
        assertEquals(" | 0 | read the book", task.toDataString());
    }

    @Test
    public void constructor_invalidDescriptions_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
        assertThrows(IllegalArgumentException.class, () -> new Task(" \t "));
        assertThrows(IllegalArgumentException.class, () -> new Task("first\nsecond"));
        assertThrows(IllegalArgumentException.class, () -> new Task("first\u2028second"));
        assertThrows(IllegalArgumentException.class, () -> new Task("first|second"));
    }

    @Test
    public void completionStatus_markAndUnmark_allRepresentationsUpdated() {
        Todo todo = new Todo("read");

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] read", todo.toString());
        assertEquals("T | 1 | read", todo.toDataString());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void hasSameDetails_typeDescriptionAndStatus_comparedCorrectly() {
        Todo completed = new Todo("READ BOOK");
        completed.markAsDone();
        Todo incomplete = new Todo("read book");

        assertTrue(completed.hasSameDetails(incomplete));
        assertFalse(completed.hasSameDetails(new Todo("write book")));
        assertFalse(completed.hasSameDetails(new Task("read book")));
        assertFalse(completed.hasSameDetails(null));
    }

    @Test
    public void deadline_dateOnlyAndDateTime_expectedRepresentationsReturned() {
        Deadline dateOnly = new Deadline("submit", LocalDate.of(2026, 2, 3));
        Deadline withTime = new Deadline("submit", LocalDateTime.of(2026, 2, 3, 14, 5));

        assertEquals("D", dateOnly.getTypeIcon());
        assertEquals(LocalDateTime.of(2026, 2, 3, 0, 0), dateOnly.getBy());
        assertEquals("D | 0 | submit | 2026-02-03", dateOnly.toDataString());
        assertEquals("[D][ ] submit (by: Feb 03 2026)", dateOnly.toString());
        assertEquals("D | 0 | submit | 2026-02-03T14:05", withTime.toDataString());
        assertEquals("[D][ ] submit (by: Feb 03 2026 2:05 PM)", withTime.toString());
    }

    @Test
    public void deadlineSameDetails_datesAndTypes_comparedCorrectly() {
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 2, 3));

        assertTrue(deadline.hasSameDetails(new Deadline("SUBMIT", LocalDate.of(2026, 2, 3))));
        assertFalse(deadline.hasSameDetails(new Deadline("submit", LocalDate.of(2026, 2, 4))));
        assertFalse(deadline.hasSameDetails(new Todo("submit")));
        assertFalse(deadline.hasSameDetails(null));
    }

    @Test
    public void event_mixedDatePrecision_expectedRepresentationsReturned() {
        Event event = new Event("camp",
                LocalDateTime.of(2026, 2, 3, 0, 0),
                LocalDateTime.of(2026, 2, 4, 17, 30), false, true);

        assertEquals("E", event.getTypeIcon());
        assertEquals(LocalDateTime.of(2026, 2, 3, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 2, 4, 17, 30), event.getTo());
        assertEquals("E | 0 | camp | 2026-02-03 | 2026-02-04T17:30", event.toDataString());
        assertEquals("[E][ ] camp (from: Feb 03 2026 to: Feb 04 2026 5:30 PM)", event.toString());
    }

    @Test
    public void event_dateConstructorsAndSameDetails_valuesComparedCorrectly() {
        Event event = new Event("camp", LocalDate.of(2026, 2, 3), LocalDate.of(2026, 2, 4));
        Event same = new Event("CAMP", LocalDate.of(2026, 2, 3), LocalDate.of(2026, 2, 4));

        assertTrue(event.hasSameDetails(same));
        assertFalse(event.hasSameDetails(
                new Event("camp", LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 4))));
        assertFalse(event.hasSameDetails(
                new Event("camp", LocalDate.of(2026, 2, 3), LocalDate.of(2026, 2, 5))));
        assertFalse(event.hasSameDetails(new Todo("camp")));
        assertFalse(event.hasSameDetails(null));
    }

    @Test
    public void event_endNotAfterStart_exceptionThrown() {
        LocalDateTime start = LocalDateTime.of(2026, 2, 3, 9, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("camp", start, start));
        assertThrows(IllegalArgumentException.class, () -> new Event("camp", start, start.minusMinutes(1)));
    }
}
