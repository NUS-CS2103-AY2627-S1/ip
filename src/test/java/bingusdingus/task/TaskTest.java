package bingusdingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task state transitions and the display/storage representations of task types. */
class TaskTest {
    @Test
    void task_markingChangesStatusAndSerialization() {
        Task task = new Todo("buy milk");
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("buy milk", task.getDescription());
        assertEquals("T | 0 | buy milk", task.toFileFormat());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[T][X] buy milk", task.toString());
        assertEquals("T | 1 | buy milk", task.toFileFormat());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    void deadlineAndEvent_includeDatesInDisplayAndStorageFormats() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 6, 6, 0, 0));
        assertEquals("[D][ ] return book (by: Jun 06 2019)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-06-06T00:00", deadline.toFileFormat());

        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 10, 15, 14, 0),
                LocalDateTime.of(2019, 10, 15, 16, 0));
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 2:00 PM to: Oct 15 2019 4:00 PM)",
                event.toString());
        assertEquals("E | 0 | project meeting | 2019-10-15T14:00 | 2019-10-15T16:00", event.toFileFormat());
    }

    @Test
    void event_rejectsEndAtOrBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 7, 15, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, start));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, start.minusMinutes(1)));
    }

    @Test
    void deadlineAndEvent_rejectNullDateTimes() {
        assertThrows(NullPointerException.class, () -> new Deadline("submit report", (LocalDateTime) null));
        assertThrows(NullPointerException.class, () ->
                new Event("meeting", null, LocalDateTime.of(2026, 9, 7, 16, 0)));
        assertThrows(NullPointerException.class, () ->
                new Event("meeting", LocalDateTime.of(2026, 9, 7, 15, 0), null));
    }

    @Test
    void hasSameDetailsAs_comparesTaskSpecificDetailsButIgnoresCompletionState() {
        Todo incompleteTodo = new Todo("buy milk");
        Todo completedTodo = new Todo("buy milk");
        completedTodo.markAsDone();
        Deadline sameDeadline = new Deadline("return book", LocalDateTime.of(2019, 6, 6, 0, 0));
        Deadline differentDeadline = new Deadline("return book", LocalDateTime.of(2019, 6, 7, 0, 0));
        Event sameEvent = new Event("meeting", LocalDateTime.of(2019, 6, 6, 9, 0),
                LocalDateTime.of(2019, 6, 6, 10, 0));
        Event differentEvent = new Event("meeting", LocalDateTime.of(2019, 6, 6, 9, 0),
                LocalDateTime.of(2019, 6, 6, 11, 0));

        assertTrue(incompleteTodo.hasSameDetailsAs(completedTodo));
        assertFalse(incompleteTodo.hasSameDetailsAs(sameDeadline));
        assertTrue(sameDeadline.hasSameDetailsAs(
                new Deadline("return book", LocalDateTime.of(2019, 6, 6, 0, 0))));
        assertFalse(sameDeadline.hasSameDetailsAs(differentDeadline));
        assertTrue(sameEvent.hasSameDetailsAs(
                new Event("meeting", LocalDateTime.of(2019, 6, 6, 9, 0),
                        LocalDateTime.of(2019, 6, 6, 10, 0))));
        assertFalse(sameEvent.hasSameDetailsAs(differentEvent));
    }
}
