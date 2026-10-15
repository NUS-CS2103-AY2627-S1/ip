package skynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests event validation, formatting, and copying.
 */
public class EventTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2026, 1, 1, 9, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Test
    void constructor_validTimes_createsEvent() {
        Event event = new Event("meeting", FROM, TO);

        assertEquals(FROM, event.getFrom());
        assertEquals(TO, event.getTo());
        assertEquals("meeting", event.getDescription());
    }

    @Test
    void constructor_missingOrReversedTimes_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", null, TO));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", FROM, null));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", FROM, FROM));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", TO, FROM));
    }

    @Test
    void toString_containsFormattedEventTimes() {
        Event event = new Event("meeting", FROM, TO);

        assertEquals(
                "[E][ ] meeting (from: Jan 01 2026, 09:00 AM to: Jan 01 2026, 10:00 AM)",
                event.toString());
    }

    @Test
    void copy_preservesEventDetails() {
        Event original = new Event("meeting", FROM, TO);
        original.markAsDone();

        Event copy = (Event) original.copy();

        assertEquals(original.getDescription(), copy.getDescription());
        assertEquals(original.getFrom(), copy.getFrom());
        assertEquals(original.getTo(), copy.getTo());
        assertEquals(original.isDone(), copy.isDone());
    }
}
