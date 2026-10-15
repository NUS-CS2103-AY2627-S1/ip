package eva.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    void todo_markAndUnmark_updatesDisplayAndFileStatus() {
        Todo todo = new Todo("read book");

        assertEquals(" ", todo.getStatusIcon());
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileString());

        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toFileString());

        todo.markAsNotDone();
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    void event_displaysAndStoresTimes() {
        Event event = new Event("team meeting", "09:00", "10:00");

        assertEquals(
                "[E][ ] team meeting (from: 09:00 to: 10:00)",
                event.toString());
        assertEquals(
                "E | 0 | team meeting | 09:00 | 10:00",
                event.toFileString());
    }

    @Test
    void containsKeyword_ignoresLetterCase() {
        Todo todo = new Todo("Submit Report");

        assertTrue(todo.containsKeyword("report"));
        assertTrue(todo.containsKeyword("SUBMIT"));
        assertFalse(todo.containsKeyword("meeting"));
    }
}
