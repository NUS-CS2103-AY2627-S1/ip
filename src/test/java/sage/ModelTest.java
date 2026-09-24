package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sage.model.Deadline;
import sage.model.Event;
import sage.model.TaskList;
import sage.model.Todo;

class ModelTest {
    @Test
    void deadlineStoresDateAndUsesDisplayFormat() {
        LocalDateTime due = LocalDateTime.of(2026, 9, 12, 10, 0);
        Deadline deadline = new Deadline("submit report", due);

        assertEquals(due, deadline.getBy());
        assertEquals("[D][ ] submit report (by: Sep 12 2026, 10:00AM)", deadline.toString());
    }

    @Test
    void eventStoresTimesAndUsesDisplayFormat() {
        LocalDateTime from = LocalDateTime.of(2026, 9, 12, 11, 0);
        LocalDateTime to = LocalDateTime.of(2026, 9, 12, 12, 0);
        Event event = new Event("meeting", from, to);

        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
        assertEquals("[E][ ] meeting (from: Sep 12 2026, 11:00AM to: Sep 12 2026, 12:00PM)",
                event.toString());
    }

    @Test
    void taskListRecognizesEquivalentTasksAndRejectsDifferentTypes() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.containsEquivalent(new Todo("read book")));
        assertFalse(tasks.containsEquivalent(new Todo("read notes")));
        assertFalse(tasks.containsEquivalent(new Deadline("read book", LocalDateTime.of(2026, 1, 1, 1, 0))));
    }
}
