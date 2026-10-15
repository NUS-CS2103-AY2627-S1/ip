package slotbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTypeTest {

    @Test
    public void todo_toString_includesTypeStatusAndDescription() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void deadline_accessorsAndToString_includeDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 18));

        assertEquals(LocalDate.of(2026, 9, 18), deadline.getDate());
        assertEquals("[D][ ] submit report (by: Sep 18 2026)", deadline.toString());
    }

    @Test
    public void event_accessorsAndToString_includeTimeRange() {
        LocalDateTime from = LocalDateTime.of(2026, 9, 18, 10, 0);
        LocalDateTime to = LocalDateTime.of(2026, 9, 18, 11, 30);
        Event event = new Event("meeting", from, to);

        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
        assertEquals("[E][ ] meeting (from: Sep 18 2026 10:00 to: Sep 18 2026 11:30)",
                event.toString());
    }

    @Test
    public void event_invalidTimeRanges_exceptionThrown() {
        LocalDateTime time = LocalDateTime.of(2026, 9, 18, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", time, time));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", time.plusHours(1), time));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", null, time));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", time, null));
    }
}
