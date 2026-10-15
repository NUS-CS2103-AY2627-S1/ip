package bot67.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TypedTaskTest {
    @Test
    void todo_formatsDescriptionAndPersistence() {
        Todo todo = new Todo("todo buy milk");
        assertEquals("[T][ ] buy milk", todo.getDescription());
        assertEquals("T | 0 | buy milk", todo.toFileFormat());
    }

    @Test
    void deadline_formatsIsoDateAndDateTime() {
        Deadline date = new Deadline("deadline report /by 2026-10-15");
        Deadline dateTime = new Deadline("deadline meeting /by 2026-10-15T14:00");
        assertEquals("[D][ ] report (by: Oct 15 2026)", date.getDescription());
        assertEquals("[D][ ] meeting (by: Oct 15 2026 14:00)", dateTime.getDescription());
        assertEquals("D | 0 | report | 2026-10-15", date.toFileFormat());
    }

    @Test
    void event_formatsDateRangeAndPersistence() {
        Event event = new Event("event meeting /from 2026-10-15T14:00 /to 2026-10-15T16:30");
        assertEquals("[E][ ] meeting (from: Oct 15 2026 14:00 to: Oct 15 2026 16:30)",
                event.getDescription());
        assertEquals("E | 0 | meeting | 2026-10-15T14:00 | 2026-10-15T16:30", event.toFileFormat());
    }
}
