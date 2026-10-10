package cassava.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTaskTest {

    @Test
    public void testGetTaskTypeToken() {
        EventTask task = new EventTask(
                "project meeting", LocalDate.parse("2026-08-28"), LocalDate.parse("2026-08-29"));
        assertEquals("E", task.getTaskTypeToken());
    }

    @Test
    public void testConstructor_plain_toString() {
        EventTask task = new EventTask(
                "project meeting", LocalDate.parse("2026-08-28"), LocalDate.parse("2026-08-29"));
        assertEquals("[E] [ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)", task.toString());
    }

    @Test
    public void testConstructor_fullArgs_toString() {
        EventTask task = new EventTask(
                "project meeting", true, Task.TaskPriority.MEDIUM,
                LocalDate.parse("2026-08-28"), LocalDate.parse("2026-08-29"));
        assertEquals(
                "[E] [X] project meeting [medium] (from: Aug 28 2026 to: Aug 29 2026)", task.toString());
    }

    @Test
    public void testToDataString() {
        EventTask task = new EventTask(
                "project meeting", false, Task.TaskPriority.UNKNOWN,
                LocalDate.parse("2026-08-28"), LocalDate.parse("2026-08-29"));
        assertEquals(
                "E | 0 | project meeting | unknown | 2026-08-28 | 2026-08-29", task.toDataString());
    }

}
