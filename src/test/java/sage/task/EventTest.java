package sage.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {
    @Test
    void parseDateTime_handlesDateAndDateTimeInputs() {
        assertEquals(LocalDateTime.of(2025, 2, 14, 9, 0), Event.parseDateTime("14/2/2025 0900"));
        assertEquals(LocalDateTime.of(2025, 2, 14, 0, 0), Event.parseDateTime("2025-02-14"));
        assertNull(Event.parseDateTime("bad-date"));
    }

    @Test
    void constructorAndToString_formatDatesForDisplay() {
        Event event = new Event("team sync", "2025-02-14 09:00", "2025-02-14 10:30");

        assertNotNull(event.getFrom());
        assertNotNull(event.getTo());
        assertEquals("2025-02-14 09:00", event.getFromText());
        assertEquals("2025-02-14 10:30", event.getToText());
        assertTrue(event.toString().contains("team sync"));
        assertTrue(event.toString().contains("Feb 14 2025, 9:00AM"));
        assertTrue(event.toString().contains("Feb 14 2025, 10:30AM"));
    }

    @Test
    void constructor_explicitDateTimes_retainsIsoTextAndReadableDisplay() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 18, 14, 30);
        LocalDateTime end = start.plusHours(1);
        Event event = new Event("meeting", start, end);

        assertEquals(start, event.getFrom());
        assertEquals(end, event.getTo());
        assertEquals("2026-09-18T14:30:00", event.getFromText());
        assertEquals("2026-09-18T15:30:00", event.getToText());
        assertEquals("[E][ ] meeting (from: Sep 18 2026, 2:30PM to: Sep 18 2026, 3:30PM)", event.toString());
    }

    @Test
    void constructor_missingExplicitDateTimes_retainsEmptyFallbacks() {
        Event event = new Event("meeting", (LocalDateTime) null, null);

        assertNull(event.getFrom());
        assertNull(event.getTo());
        assertEquals("", event.getFromText());
        assertEquals("", event.getToText());
        assertEquals("[E][ ] meeting (from:  to: )", event.toString());
    }
}
