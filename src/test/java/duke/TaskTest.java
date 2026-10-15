package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task state, display text, and serialization. */
public class TaskTest {
    @Test
    public void markAndUnmark_updatesStatusIconAndSaveStatus() {
        Task task = new Todo("read book");

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("1", task.getSaveStatus());

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("0", task.getSaveStatus());
    }

    @Test
    public void datedTasks_displayAndSerializeTheirTimes() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 18, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 18, 10, 0);

        Deadline deadline = new Deadline("submit report", end);
        Event event = new Event("meeting", start, end);
        Reminder reminder = new Reminder("call mum", start);

        assertEquals("submit report (by: Sep 18 2026, 10:00AM)", deadline.getDisplayText());
        assertEquals("E | 0 | meeting | 2026-09-18T09:00 -> 2026-09-18T10:00", event.toSaveString());
        assertEquals("call mum (at: Sep 18 2026, 9:00AM)", reminder.getDisplayText());
    }
}
