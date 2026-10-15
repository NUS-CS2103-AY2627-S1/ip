package walnut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    void parseTask_event_createsEventCorrectly() {
        Task task = Parser.parseTask(
                "E | 0 | meeting | 2026/09/01 1000-2026/09/01 1200"
        );

        assertTrue(task instanceof Event);

        Event event = (Event) task;

        assertEquals("meeting", event.getDescription());
        assertEquals("2026/09/01 1000", event.getEventStartTime());
        assertEquals("2026/09/01 1200", event.getEventEndTime());
        assertFalse(event.isDone());
    }

    @Test
    void parseTask_event_createsCompletedEvent() {
        Task task = Parser.parseTask(
                "E | 1 | meeting | 2026/09/01 1000-2026/09/01 1200"
        );

        assertTrue(task instanceof Event);

        Event event = (Event) task;

        assertEquals("meeting", event.getDescription());
        assertEquals("2026/09/01 1000", event.getEventStartTime());
        assertEquals("2026/09/01 1200", event.getEventEndTime());
        assertTrue(event.isDone());
    }

    @Test
    void parseTask_event_rejectsInvalidStartDate() {
        Task task = Parser.parseTask(
                "E | 0 | meeting | invalid-date-2026/09/01 1200"
        );

        assertNull(task);
    }

    @Test
    void parseTask_event_rejectsMissingEndDate() {
        Task task = Parser.parseTask(
                "E | 0 | meeting | 2026/09/01 1000-"
        );

        assertNull(task);
    }

    @Test
    void parseTask_event_rejectsMissingSeparator() {
        Task task = Parser.parseTask(
                "E | 0 | meeting | 2026/09/01 1000"
        );

        assertNull(task);
    }
}
