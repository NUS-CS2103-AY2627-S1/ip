package shai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the core state and formatting behavior shared by all task types. */
class TaskTest {
    @Test
    void constructor_descriptionProvided_startsIncompleteWithDescription() {
        Task task = new Task("submit report");

        assertEquals("submit report", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void constructor_nullDescription_reportsBrokenTaskContract() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    @Test
    void deadlineAndEvent_nullTimeValues_reportBrokenTaskContracts() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit report", null));
        assertThrows(IllegalArgumentException.class, () -> new Event("team meeting", null,
                LocalDateTime.of(2026, 9, 10, 10, 0)));
        assertThrows(IllegalArgumentException.class, () -> new Event("team meeting",
                LocalDateTime.of(2026, 9, 10, 10, 0), null));
    }

    @Test
    void event_endBeforeStart_reportsBrokenTaskContract() {
        assertThrows(IllegalArgumentException.class, () -> new Event("team meeting",
                LocalDateTime.of(2026, 9, 10, 12, 0),
                LocalDateTime.of(2026, 9, 10, 10, 0)));
    }

    @Test
    void timedTask_defaultReminder_usesOneDayBeforeTarget() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 15, 17, 0));
        Event event = new Event("team meeting", LocalDateTime.of(2026, 9, 18, 14, 0),
                LocalDateTime.of(2026, 9, 18, 16, 0));

        assertEquals(Reminder.DEFAULT_MINUTES_BEFORE, deadline.getReminderMinutesBefore());
        assertEquals(Reminder.DEFAULT_MINUTES_BEFORE, event.getReminderMinutesBefore());
    }

    @Test
    void markAsDone_incompleteTask_becomesCompleted() {
        Task task = new Task("submit report");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] submit report", task.toString());
    }

    @Test
    void unmark_completedTask_becomesIncomplete() {
        Task task = new Task("submit report");
        task.markAsDone();

        task.unmark();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] submit report", task.toString());
    }
}
