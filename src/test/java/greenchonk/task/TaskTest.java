package greenchonk.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void status_newTaskAndTransitions_statusRemainsConsistent() {
        Task task = new Todo("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[T][X] read book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void todo_accessorsAndScheduleValues_valuesMatchTask() {
        Task task = new Todo("read book");

        assertEquals("read book", task.getDescription());
        assertEquals("T", task.getTypeIcon());
        assertEquals("[T][ ] read book", task.toString());
        assertFalse(task.occursOn(LocalDate.of(2026, 8, 28)));
    }

    @Test
    void matches_partialKeywordIgnoringCase_matchResultReturned() {
        Task task = new Todo("Read handbook");

        assertTrue(task.matches("BOOK"));
        assertTrue(task.matches("hand"));
        assertFalse(task.matches("write"));
    }

    @Test
    void hasSameDetails_nullTypeAndDescription_comparisonIsSemantic() {
        Task task = new Todo("read book");

        assertTrue(task.hasSameDetails(new Todo("read book")));
        assertFalse(task.hasSameDetails(new Todo("Read book")));
        assertFalse(task.hasSameDetails(
                new Deadline("read book", LocalDate.of(2026, 8, 28))));
        assertFalse(task.hasSameDetails(null));
    }
}
