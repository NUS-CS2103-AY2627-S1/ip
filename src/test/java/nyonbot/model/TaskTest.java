package nyonbot.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void task_newTask_isIncompleteAndExposesName() {
        Task task = new Task("read book");

        assertEquals("read book", task.getName());
        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void task_unsafeStorageName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("a|b"));
        assertThrows(IllegalArgumentException.class, () -> new Task("first\nsecond"));
        assertThrows(IllegalArgumentException.class, () -> new Task(" "));
    }

    @Test
    void completeAndUncompleteTask_updatesCompletionState() {
        Task task = new Task("read book");

        task.completeTask();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());

        task.uncompleteTask();
        assertFalse(task.isDone());
    }

    @Test
    void isSameTask_sameAndDifferentNames_returnsExpectedResult() {
        Task task = new Task("read book");

        assertTrue(task.isSameTask("read book"));
        assertFalse(task.isSameTask("Read book"));
    }

    @Test
    void deadline_getterAndToString_returnsDeadlineDetails() {
        LocalDateTime date = LocalDateTime.of(2026, 9, 5, 9, 7);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals(date, deadline.getDeadline());
        assertEquals("[D][ ] submit report (by: 05 Sep 2026 0907)",
                deadline.toString());
    }

    @Test
    void event_getterAndToString_returnsEventDetails() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 5, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 5, 10, 30);
        Event event = new Event("meeting", start, end);

        assertEquals(start, event.getEventTimes()[0]);
        assertEquals(end, event.getEventTimes()[1]);
        assertEquals("[E][ ] meeting (from: 05 Sep 2026 0900 to: 05 Sep 2026 1030)",
                event.toString());
    }
}
