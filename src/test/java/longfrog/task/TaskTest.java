package longfrog.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void todo_completionStateChanges_updatesStatusDisplayAndSaveFormat() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        assertEquals("read book", todo.getName());
        assertEquals(" ", todo.getStatusIcon());
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileFormat());

        todo.markAsDone();

        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toFileFormat());

        todo.unmarkAsDone();

        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    void occursOn_tasksWithDifferentDateSemantics_returnsExpectedResults() {
        LocalDate targetDate = LocalDate.of(2024, 1, 2);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", targetDate.atTime(9, 0));
        Event event = new Event("camp", targetDate.minusDays(1).atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay());

        assertFalse(todo.occursOn(targetDate));
        assertTrue(deadline.occursOn(targetDate));
        assertTrue(event.occursOn(targetDate));
        assertTrue(event.occursOn(targetDate.minusDays(1)));
        assertTrue(event.occursOn(targetDate.plusDays(1)));
        assertFalse(event.occursOn(targetDate.minusDays(2)));
        assertFalse(event.occursOn(targetDate.plusDays(2)));
    }

    @Test
    void deadline_toStringAndToFileFormat_formatsDateAndCompletionState() {
        LocalDateTime by = LocalDateTime.of(2024, 1, 2, 9, 5);
        Deadline deadline = new Deadline("submit report", by);

        assertEquals(by, deadline.getBy());
        assertEquals("[D][ ] submit report (by: Jan 02 2024, 9:05 am)", deadline.toString());
        assertEquals("D | 0 | submit report | 2/1/2024 0905", deadline.toFileFormat());

        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Jan 02 2024, 9:05 am)", deadline.toString());
        assertEquals("D | 1 | submit report | 2/1/2024 0905", deadline.toFileFormat());
    }

    @Test
    void event_toStringAndToFileFormat_formatsTimeRangeAndCompletionState() {
        LocalDateTime from = LocalDateTime.of(2024, 12, 31, 23, 0);
        LocalDateTime to = LocalDateTime.of(2025, 1, 1, 0, 30);
        Event event = new Event("new year event", from, to);

        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
        assertEquals("[E][ ] new year event (from: Dec 31 2024, 11:00 pm to: Jan 01 2025, 12:30 am)",
                event.toString());
        assertEquals("E | 0 | new year event | 31/12/2024 2300 | 1/1/2025 0030", event.toFileFormat());

        event.markAsDone();

        assertEquals("[E][X] new year event (from: Dec 31 2024, 11:00 pm to: Jan 01 2025, 12:30 am)",
                event.toString());
        assertEquals("E | 1 | new year event | 31/12/2024 2300 | 1/1/2025 0030", event.toFileFormat());
    }

    @Test
    void isDuplicateOf_todosWithNormalizedDescriptions_ignoresCaseWhitespaceAndCompletion() {
        Todo original = new Todo("Read Book");
        Todo equivalent = new Todo("  read   book  ");
        equivalent.markAsDone();

        assertTrue(original.isDuplicateOf(equivalent));
        assertTrue(equivalent.isDuplicateOf(original));
        assertTrue(new Todo("read\u2003book").isDuplicateOf(new Todo("read book")));
        assertFalse(original.isDuplicateOf(new Todo("read book!")));
        assertFalse(new Todo("caf\u00e9").isDuplicateOf(new Todo("cafe\u0301")));
        assertFalse(original.isDuplicateOf(new Deadline("read book", LocalDateTime.of(2024, 1, 2, 9, 0))));
        assertFalse(original.isDuplicateOf(null));
    }

    @Test
    void isDuplicateOf_deadlines_comparesParsedDeadline() {
        LocalDateTime deadlineTime = LocalDateTime.of(2024, 1, 2, 9, 0);
        Deadline original = new Deadline("Submit Report", deadlineTime);

        assertTrue(original.isDuplicateOf(new Deadline("submit   report", deadlineTime)));
        assertFalse(original.isDuplicateOf(new Deadline("different report", deadlineTime)));
        assertFalse(original.isDuplicateOf(new Deadline("submit report", deadlineTime.plusMinutes(1))));
    }

    @Test
    void isDuplicateOf_events_comparesCompleteTimeRange() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 2, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 2, 10, 0);
        Event original = new Event("Team Meeting", start, end);

        assertTrue(original.isDuplicateOf(new Event("team meeting", start, end)));
        assertFalse(original.isDuplicateOf(new Event("different meeting", start, end)));
        assertFalse(original.isDuplicateOf(new Event("team meeting", start.plusMinutes(30), end)));
        assertFalse(original.isDuplicateOf(new Event("team meeting", start, end.plusMinutes(30))));
    }
}
