package ada;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StorageTest {

    //Todo
    @Test
    void parseLineToTask_validTodo_returnsTodo() {
        Storage storage = new Storage();
        Task task = storage.parseLineToTask("T | 0 | finish homework");
        assertTrue(task instanceof Todo);
        assertEquals("[T][ ] finish homework", task.toString());
        assertFalse(task.isDone());
    }

    // Deadline
    @Test
    void parseLineToTask_validDeadline_returnsDeadline() {
        Storage storage = new Storage();
        Task task = storage.parseLineToTask("D | 1 | submit report | 2026-08-27T23:00:00");
        assertTrue(task instanceof Deadline);
        assertEquals("[D][X] submit report (by: Aug 27 2026 11:00 PM)", task.toString());
        assertTrue(task.isDone());
    }

    // Event
    @Test
    void parseLineToTask_validEvent_returnsEvent() {
        Storage storage = new Storage();
        Task task = storage.parseLineToTask("E | 0 | team meeting | 2026-08-27T14:00:00 | 2026-08-27T16:00:00");
        assertTrue(task instanceof Event);
        assertEquals("[E][ ] team meeting (from: Aug 27 2026 2:00 PM to: Aug 27 2026 4:00 PM)", task.toString());
        assertFalse(task.isDone());
    }

    // Null line
    @Test
    void parseLineToTask_emptyString_throwsException() {
        Storage storage = new Storage();
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storage.parseLineToTask("");
        });
    }

    // no '|' in the line
    @Test
    void parseLineToTask_noDelimiter_throwsException() {
        Storage storage = new Storage();
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storage.parseLineToTask("T 0 readbook");
        });
    }

    // Deadline lacks date
    @Test
    void parseLineToTask_deadlineMissingTime_throwsException() {
        Storage storage = new Storage();
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storage.parseLineToTask("D | 0 | essay");
        });
    }

    // Event lacks date
    @Test
    void parseLineToTask_eventMissingEndTime_throwsException() {
        Storage storage = new Storage();
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            storage.parseLineToTask("E | 0 | team meeting | 2026-08-27T14:00:00");
        });
    }

    // Deadline date invalid
    @Test
    void parseLineToTask_deadlineBadTimeFormat_throwsDateTimeParseException() {
        Storage storage = new Storage();
        assertThrows(java.time.format.DateTimeParseException.class, () -> {
            storage.parseLineToTask("D | 0 | essay | wrongtime");
        });
    }

    // Event date invalid
    @Test
    void parseLineToTask_eventBadTimeFormat_throwsDateTimeParseException() {
        Storage storage = new Storage();
        assertThrows(java.time.format.DateTimeParseException.class, () -> {
            storage.parseLineToTask("E | 0 | team meeting | abc | 2026-08-27T16:00:00");
        });
    }

    // Invalid task type
    @Test
    void parseLineToTask_unknownType_returnNull() {
        Storage storage = new Storage();
        Task task = storage.parseLineToTask("X | 0 | random task");
        assertNull(task);
    }
}
