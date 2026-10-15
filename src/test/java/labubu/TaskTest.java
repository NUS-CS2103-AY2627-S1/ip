package labubu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task subtype behavior and status indicators. */
public class TaskTest {
    @Test
    public void reportsStatusIndicators() {
        Task task = new ToDo("task");

        assertEquals(" ", task.getStatusIndicator());
        task.setStatus(Task.Status.IN_PROGRESS);
        assertEquals("-", task.getStatusIndicator());
        task.setStatus(Task.Status.COMPLETED);
        assertEquals("X", task.getStatusIndicator());
    }

    @Test
    public void formatsDeadlineTask() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 7, 6, 18, 30));

        assertEquals("D", deadline.getMarker());
        assertEquals("return book (by: 06 July 2026 [6:30pm])", deadline.getTaskDescription());
        assertEquals("D|INCOMPLETE|return book|2026-07-06T18:30", deadline.toSaveFormat());
    }

    @Test
    public void formatsEventTask() {
        Event event = new Event("team meeting",
                LocalDateTime.of(2026, 7, 6, 14, 0),
                LocalDateTime.of(2026, 7, 6, 15, 30));

        assertEquals("E", event.getMarker());
        assertEquals("team meeting (from: 06 July 2026 [2:00pm], to: 06 July 2026 [3:30pm])",
                event.getTaskDescription());
        assertEquals("E|INCOMPLETE|team meeting|2026-07-06T14:00|2026-07-06T15:30",
                event.toSaveFormat());
    }
}
