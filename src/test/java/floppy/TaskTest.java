package floppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void markAndUnmark_todo_updatesStatusAndDisplay() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.toString());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
    }

    @Test
    void toString_datedTaskTypes_formatsDatesAndSymbols() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 20));
        Event event = new Event(
                "camp", LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 22));
        PeriodTask periodTask = new PeriodTask(
                "collect certificate", LocalDate.of(2026, 9, 23), LocalDate.of(2026, 9, 25));

        assertEquals("[D][ ] submit report (by: Sep 20 2026)", deadline.toString());
        assertEquals("[E][ ] camp (from: Sep 21 2026 to: Sep 22 2026)", event.toString());
        assertEquals("[P][ ] collect certificate (within: Sep 23 2026 to Sep 25 2026)",
                periodTask.toString());
        assertEquals(TaskType.DEADLINE, deadline.getTaskType());
        assertEquals("submit report", deadline.getDescription());
    }
}
