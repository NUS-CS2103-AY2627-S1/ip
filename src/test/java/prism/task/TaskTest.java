package prism.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import prism.PrismException;

/** Tests shared task behavior and date-aware task behavior. */
public class TaskTest {
    @Test
    public void todo_lifecycleAndSerialization_returnsExpectedValues() {
        Todo todo = new Todo("read book");
        assertEquals(TaskType.TODO, todo.getType());
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileFormat());

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("T | 1 | read book", todo.toFileFormat());
        todo.markAsNotDone();
        assertFalse(todo.isDone());
    }

    @Test
    public void deadline_validDateAndReschedule_updatesDate() throws PrismException {
        Deadline deadline = new Deadline("submit report", "2025-12-02 1800");
        assertEquals(LocalDateTime.of(2025, 12, 2, 18, 0), deadline.getBy());
        assertTrue(deadline.toString().contains("Dec 02 2025"));

        deadline.reschedule("3/12/2025 0900");
        assertEquals(LocalDateTime.of(2025, 12, 3, 9, 0), deadline.getBy());
    }

    @Test
    public void deadline_invalidDate_throwsException() {
        assertThrows(PrismException.class, () -> new Deadline("task", "not a date"));
        assertThrows(PrismException.class, () -> new Deadline("task", "2025-02-30 1800"));
    }

    @Test
    public void event_reschedule_preservesDuration() throws PrismException {
        Event event = new Event("meeting", "2025-12-02 0900", "2025-12-02 1030");
        event.reschedule("2025-12-03 1300");
        assertEquals(LocalDateTime.of(2025, 12, 3, 13, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2025, 12, 3, 14, 30), event.getTo());
        assertTrue(event.toFileFormat().startsWith("E | 0 | meeting | 2025-12-03 1300"));
    }

    @Test
    public void event_invalidDate_throwsException() {
        assertThrows(PrismException.class, () -> new Event("meeting", "2025-12-02 0900", "bad"));
        assertThrows(PrismException.class, () -> new Event("meeting", "31/04/2025 0900",
                "2025-04-30 1030"));
    }

    @Test
    public void event_validLeapDay_returnsEvent() throws PrismException {
        Event event = new Event("meeting", "2024-02-29 0900", "2024-02-29 1030");
        assertEquals(LocalDateTime.of(2024, 2, 29, 9, 0), event.getFrom());
    }
}
