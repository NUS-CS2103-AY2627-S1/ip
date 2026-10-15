package snoopy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the completion state and common saved/display formats of tasks.
 */
public class TaskTest {
    @Test
    public void completionState_markAndUnmark_updatesAllRepresentations() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0 | read book", task.toDataString());
        assertEquals("[ ] read book", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("1 | read book", task.toDataString());
        assertEquals("[X] read book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0 | read book", task.toDataString());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void todo_formatsIncludeTaskTypeAndState() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toDataString());
        assertEquals("[T][ ] read book", todo.toString());

        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toDataString());
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void deadline_formatsIncludeEnglishDisplayDateAndIsoStorageDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 3));

        assertEquals("D | 0 | return book | 2026-08-03", deadline.toDataString());
        assertEquals("[D][ ] return book (by: Aug 03 2026)", deadline.toString());

        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2026-08-03", deadline.toDataString());
        assertEquals("[D][X] return book (by: Aug 03 2026)", deadline.toString());
    }

    @Test
    public void event_formatsIncludeEnglishDisplayDatesAndIsoStorageDates() {
        Event event = new Event("camp", LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 12));

        assertEquals("E | 0 | camp | 2026-09-01 | 2026-09-12", event.toDataString());
        assertEquals("[E][ ] camp (from: Sep 01 2026 to: Sep 12 2026)", event.toString());

        event.markAsDone();
        assertEquals("E | 1 | camp | 2026-09-01 | 2026-09-12", event.toDataString());
        assertEquals("[E][X] camp (from: Sep 01 2026 to: Sep 12 2026)", event.toString());
    }

    @Test
    public void containsKeyword_exactSubstring_matchesDescriptionOnly() {
        Task task = new Deadline("return Book", LocalDate.of(2026, 8, 30));

        assertTrue(task.containsKeyword("return"));
        assertTrue(task.containsKeyword("Book"));
        assertFalse(task.containsKeyword("book"));
        assertFalse(task.containsKeyword("Aug 30"));
    }

    @Test
    public void updateDescription_completedDeadline_preservesTypeDateAndStatus() {
        Task task = new Deadline("return Book", LocalDate.of(2026, 8, 30));
        task.markAsDone();

        task.updateDescription("return library books");

        assertEquals("D | 1 | return library books | 2026-08-30", task.toDataString());
        assertEquals("[D][X] return library books (by: Aug 30 2026)", task.toString());
    }

    @Test
    public void constructors_missingRequiredValues_failAssertions() {
        assertThrows(AssertionError.class, () -> new Task(null));
        assertThrows(AssertionError.class, () -> new Task("   "));
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
        assertThrows(AssertionError.class, () ->
                new Event("meeting", null, LocalDate.of(2026, 9, 2)));
        assertThrows(AssertionError.class, () ->
                new Event("meeting", LocalDate.of(2026, 9, 1), null));
    }

    @Test
    public void containsKeyword_nullKeyword_failsAssertion() {
        Task task = new Task("read book");

        assertThrows(AssertionError.class, () -> task.containsKeyword(null));
    }
}
