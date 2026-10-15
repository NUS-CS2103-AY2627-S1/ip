package orion.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {
    @Test
    public void toFileFormat_uncompletedEvent_success() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 15, 14, 30);
        LocalDateTime end = LocalDateTime.of(2026, 9, 15, 16, 30);
        Event event = new Event("project meeting", start, end);
        assertEquals("E | 0 | project meeting | 2026-09-15 1430 | 2026-09-15 1630", event.toFileFormat());
    }
}
