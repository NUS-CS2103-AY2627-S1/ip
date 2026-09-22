package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toFileString_newEvent_formattedCorrectly() {
        LocalDateTime from = LocalDateTime.of(2026, 9, 12, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 9, 12, 16, 0);
        Event event = new Event("project meeting", from, to);
        assertEquals("E | 0 | project meeting | 2026-09-12 1400 | 2026-09-12 1600", event.toFileString());
    }

    @Test
    public void toString_doneEvent_formattedCorrectly() {
        LocalDateTime from = LocalDateTime.of(2026, 9, 12, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 9, 12, 16, 0);
        Event event = new Event("project meeting", from, to);
        event.markAsDone();
        assertEquals(
                "[E][X] project meeting (from: Sep 12 2026 1400 to: Sep 12 2026 1600)",
                event.toString()
        );
    }

    @Test
    public void getters_returnCorrectValues() {
        LocalDateTime from = LocalDateTime.of(2026, 9, 12, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 9, 12, 16, 0);
        Event event = new Event("project meeting", from, to);
        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
    }

    @Test
    public void equalsAndHashCode_sameValues_equal() {
        LocalDateTime from = LocalDateTime.of(2026, 9, 12, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 9, 12, 16, 0);
        Event e1 = new Event("meeting", from, to);
        Event e2 = new Event("MEETING", from, to);
        Event e3 = new Event("other", from, to);
        Event e4 = new Event("meeting", from.plusHours(1), to);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1, e3);
        assertNotEquals(e1, e4);
    }
}
