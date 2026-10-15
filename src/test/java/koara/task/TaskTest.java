package koara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests task state, identity, formatting, and constructor invariants.
 */
public class TaskTest {

    @Test
    public void taskState_markAndUnmark_updatesStatusAndFormats() {
        Task task = new Task(TaskType.TODO, "read book", "");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toDataString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("T | 1 | read book", task.toDataString());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    public void containsKeyword_matchingAndCaseDifference_returnsExpectedResult() {
        Task task = new Todo("Read Java book");

        assertTrue(task.containsKeyword("Java"));
        assertFalse(task.containsKeyword("java"));
        assertFalse(task.containsKeyword("Python"));
    }

    @Test
    public void hasSameDetails_statusIgnoredButContentCompared() {
        Todo first = new Todo("read book");
        Todo same = new Todo("read book");
        first.markAsDone();

        assertTrue(first.hasSameDetails(same));
        assertFalse(first.hasSameDetails(new Todo("write book")));
        assertFalse(first.hasSameDetails(new Deadline(
                "read book", LocalDate.of(2026, 9, 13))));
    }

    @Test
    public void taskSubclasses_displayAndStorageFormats_includeDates() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 13));
        Event event = new Event("workshop",
                LocalDate.of(2026, 9, 13), LocalDate.of(2026, 9, 14));

        assertEquals("[D][ ] submit report (by: Sep 13 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-09-13", deadline.toDataString());
        assertEquals("[E][ ] workshop (from: Sep 13 2026 to: Sep 14 2026)", event.toString());
        assertEquals("E | 0 | workshop | 2026-09-13 | 2026-09-14", event.toDataString());
        assertEquals("T", TaskType.TODO.getIcon());
        assertEquals("D", TaskType.DEADLINE.getIcon());
        assertEquals("E", TaskType.EVENT.getIcon());
    }

    @Test
    public void constructors_invalidInternalArguments_throwAssertionError() {
        assertThrows(AssertionError.class, () -> new Task(null, "task", ""));
        assertThrows(AssertionError.class, () -> new Task(TaskType.TODO, " ", ""));
        assertThrows(AssertionError.class, () -> new Task(TaskType.TODO, "task", null));
        assertThrows(AssertionError.class, () -> new Deadline("task", null));
        assertThrows(AssertionError.class, () -> new Event(
                "event", null, LocalDate.of(2026, 9, 13)));
        assertThrows(AssertionError.class, () -> new Event(
                "event", LocalDate.of(2026, 9, 13), null));
        assertThrows(AssertionError.class, () -> new Event(
                "event", LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 13)));
    }
}
