package wally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests task formatting, command serialization, and constructor validation. */
public class TaskTest {
    @Test
    public void toString_incompleteAndCompletedTasks_formatsTypeAndStatus() throws InvalidEventException {
        Task[] tasks = {
            new Task("read"), new ToDo("read"), new Deadline("read", "2026-09-14 23:59"),
            new Event("read", "2026-09-14 10:00", "2026-09-14 11:00")
        };
        String[] expected = {
            "[ ] read", "[T][ ] read", "[D][ ] read (by: 14 Sep 2026, 23:59)",
            "[E][ ] read (from: 14 Sep 2026, 10:00 to: 14 Sep 2026, 11:00)"
        };
        for (int i = 0; i < tasks.length; i++) {
            assertEquals(expected[i], tasks[i].toString());
            tasks[i].setCompleted(true);
            assertEquals(expected[i].replace("[ ]", "[X]"), tasks[i].toString());
        }
    }

    @Test
    public void getCommand_taskTypes_serializesCommands() throws InvalidEventException {
        assertEquals("read", new Task("read").getCommand());
        assertEquals("todo read", new ToDo("read").getCommand());
        assertEquals("deadline read /by 2026-09-14 23:59",
                new Deadline("read", "2026-09-14 23:59").getCommand());
        assertEquals("event read /from 2026-09-14 10:00 /to 2026-09-14 11:00",
                new Event("read", "2026-09-14 10:00", "2026-09-14 11:00").getCommand());
    }

    @Test
    public void constructors_invalidDatesAndRanges_rejectTasks() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("read", "2026-02-29 12:00"));
        assertThrows(DateTimeParseException.class, () ->
                new Event("read", "2026-02-29 12:00", "2026-03-01 12:00"));
        assertThrows(DateTimeParseException.class, () ->
                new Event("read", "2026-02-28 12:00", "2026-03-01 24:00"));
        assertThrows(InvalidEventException.class, () ->
                new Event("read", "2026-03-01 12:00", "2026-03-01 12:00"));
        assertThrows(InvalidEventException.class, () ->
                new Event("read", "2026-03-02 12:00", "2026-03-01 12:00"));
    }
}
